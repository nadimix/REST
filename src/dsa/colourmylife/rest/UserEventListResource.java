package dsa.colourmylife.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/users/{username}/events")
public class UserEventListResource {
	@Context
	protected HttpServletRequest request;
	@Context
	private SecurityContext security;

	/*
	 * RECURSOS: POST: añadir evento marcado a usuario DELETE: desmarcar evento
	 * marcado por el usuario
	 */

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assistEventJSON(@PathParam("username") String username,
			int idevent) {

		assistEvent(username, idevent);
		Response response = null;
		try {

			// PREGUNTAR QUE LOCATION TENEMOS QUE PONER DEL RESPONSE STATUS!!

			response = Response.status(204)
					.location(new URI("/users/{" + username + "}/events"))
					.build();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAssistEventJSON(
			@PathParam("username") String username, int idevent) {

		deleteAssistEvent(username, idevent);
		return Response.status(204).build();
	}

	private void assistEvent(String username, int idevent) {

		if (security.isUserInRole("registered")) {

			if (!security.getUserPrincipal().getName().equals(username)) {

				throw new WebApplicationException(Response
						.status(Response.Status.FORBIDDEN)
						.entity(APIErrorBuilder.buildError(
								Response.Status.FORBIDDEN.getStatusCode(),
								"No estas autorizado.", request)).build());
			}
		}
		// obtenemos la conexion
		Connection connection = null;

		try {
			connection = DataSourceSAP.getInstance().getDataSource()
					.getConnection();
		} catch (SQLException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE
											.getStatusCode(),
									"Service unavailable.", request)).build());
		}

		// Marcamos el evento al que el usuario ha asistido con la consulta a la
		// base de datos.

		try {

			Statement stmt = connection.createStatement();
			StringBuilder sb = new StringBuilder(
					"select * from user where username ='" + username + "'");
			ResultSet rs = stmt.executeQuery(sb.toString());

			rs.next();
			int iduser = rs.getInt("id");

			// Comprobamos que al artista al que quiera seguir no lo este
			// siguiendo ya.
			Statement stmt1 = connection.createStatement();

			StringBuilder sb1 = new StringBuilder(
					"SELECT id from assist where iduser=" + iduser
							+ " and idevent=" + idevent + "");

			ResultSet rs2 = stmt1.executeQuery(sb1.toString());

			if (rs2.next()) {

				throw new WebApplicationException(Response
						.status(Response.Status.CONFLICT)
						.entity(APIErrorBuilder.buildError(
								Response.Status.CONFLICT.getStatusCode(),
								"Ya has marcado este evento como asistido.",
								request)).build());
			}

			Statement stmt2 = connection.createStatement();

			// marcamos el evento como asistido por el usuario.
			StringBuilder sb2 = new StringBuilder(
					"INSERT INTO  assist (iduser,idevent) values (" + iduser
							+ "," + idevent + ")");

			int rc = stmt.executeUpdate(sb2.toString());
			if (rc == 0) {

				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Error al marcar el evento como asistido.",
								request)).build());
			}

			stmt.close();
			stmt1.close();
			stmt2.close();
			connection.close();

		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(), "Internal server error.",
							request)).build());
		}
	}

	private void deleteAssistEvent(String username, int idevent) {

		if (security.isUserInRole("registered")) {

			if (!security.getUserPrincipal().getName().equals(username)) {

				throw new WebApplicationException(Response
						.status(Response.Status.FORBIDDEN)
						.entity(APIErrorBuilder.buildError(
								Response.Status.FORBIDDEN.getStatusCode(),
								"No estas autorizado.", request)).build());
			}
		}

		// obtenemos la conexion
		Connection connection = null;

		try {
			connection = DataSourceSAP.getInstance().getDataSource()
					.getConnection();
		} catch (SQLException e) {
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE
											.getStatusCode(),
									"Service unavailable.", request)).build());
		}

		try {

			/*
			 * Statement stmt = connection.createStatement(); StringBuilder sb =
			 * new StringBuilder( "select id from user where name ='" +
			 * username+ "'"); ResultSet rs = stmt.executeQuery(sb.toString());
			 * 
			 * int iduser= rs.getInt("id");
			 * 
			 * Statement stmt1 = connection.createStatement();
			 * 
			 * StringBuilder sb1 = new StringBuilder(
			 * "select event.id from event where (idkind= (select id from kind where name ='"
			 * + eventkind+ "'))");
			 * 
			 * ResultSet rs1 = stmt1.executeQuery(sb1.toString());
			 * 
			 * int idevent= rs1.getInt("id");
			 */
			Statement stmt2 = connection.createStatement();

			// desmarcamos el evento asistido por el usuario.

			StringBuilder sb2 = new StringBuilder(
					"DELETE from assist where idevent=" + idevent + "");

			int rc = stmt2.executeUpdate(sb2.toString());
			if (rc == 0) {

				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Event assisted not found.", request)).build());
			}

			stmt2.close();
			connection.close();

		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(), "Internal server error.",
							request)).build());
		}

	}

}