package dsa.colourmylife.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import dsa.colourmylife.rest.model.Artist;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

/*RECURSOS:
 * GET: todos los artistas a los que sigue un usuario
 * DELETE:eliminar al artista a el que sigo
 * POST:marcar al artista al que sigo
 * Obtengo por parametro el nombre del artista
 *
 */
@Path("/users/{username}/following")
public class UserArtistListResource {

	@Context
	protected HttpServletRequest request;
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Artist> getFollowedArtistJSON(
			@PathParam("username") String username) {
		return getArtistsFollowed(username);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// seria mejor pasar por parametro el evento como objeto?
	public Response insertFollowedArtistJSON(
			@PathParam("username") String username,
			@QueryParam("idartist") int idartist) {

		insertFollowedArtist(username, idartist);
		Response response = null;
		try {
			response = Response.status(204)
					.location(new URI("/users/{" + username + "}/following"))
					.build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFollowedArtistJSON(
			@PathParam("username") String username,
			@QueryParam("idartist") int idartist) {// int idartist

		deleteFollowedArtist(username, idartist);
		return Response.status(204).build();
	}

	private List<Artist> getArtistsFollowed(String username) {
		if (security.isUserInRole("registered")
				|| security.isUserInRole("admin")) {
			if (security.isUserInRole("registered")
					&& !security.getUserPrincipal().getName().equals(username)) {
				throw new WebApplicationException(Response
						.status(Response.Status.FORBIDDEN)
						.entity(APIErrorBuilder.buildError(
								Response.Status.FORBIDDEN.getStatusCode(),
								"FORBIDDEN", request)).build());
			}
			Connection connection = null;
			try {
				connection = DataSourceSAP.getInstance().getDataSource()
						.getConnection();
			} catch (SQLException e) {
				throw new WebApplicationException(Response
						.status(Response.Status.SERVICE_UNAVAILABLE)
						.entity(APIErrorBuilder.buildError(
								Response.Status.SERVICE_UNAVAILABLE
										.getStatusCode(),
								"Service unavailable.", request)).build());
			}
			try {

				Statement stmt = connection.createStatement();
				// seleccionar los artistas a los que sigue un usuario.
				ResultSet rs = stmt
						.executeQuery("select id from user where username='"
								+ username + "'");
				rs.next();
				int iduser = rs.getInt("id");

				Statement stmt1 = connection.createStatement();
				ResultSet rs1 = stmt1
						.executeQuery("select idartist from follow where iduser="
								+ iduser + "");

				if (!rs1.next()) {
					// no esta siguiendo a ningun artista
					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"No estas siguiendo a ningun artista.",
									request)).build());

				}
				rs1.next();
				int idartist = rs1.getInt("idartist");

				Statement stmt2 = connection.createStatement();
				ResultSet rs2 = stmt2
						.executeQuery("select * from artist where id="
								+ idartist + "");
				List<Artist> artists = new ArrayList<>();
				while (rs2.next())// mientras haya artistas seguidos
				{
					Artist artist = new Artist();
					artist.setArtistid(rs.getInt("id"));
					artist.setName(rs.getString("name"));
					artist.setGenreId(rs.getInt("idgenre1"));
					artist.setGenre2Id(rs.getInt("idgenre2"));
					artist.setInfo(rs.getString("info"));
				}

				stmt.close();
				stmt1.close();
				stmt2.close();
				connection.close();
				return artists;

			} catch (SQLException e) {
				throw new WebApplicationException(Response
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(APIErrorBuilder.buildError(
								Response.Status.INTERNAL_SERVER_ERROR
										.getStatusCode(),
								"Error accessing to database.", request))
						.build());
			}
		}
		return null;
	}

	private void deleteFollowedArtist(String username, int idartist) {
		if (security.isUserInRole("registered")
				|| security.isUserInRole("admin")) {
			if (security.isUserInRole("registered")
					&& !security.getUserPrincipal().getName().equals(username)) {
				throw new WebApplicationException(Response
						.status(Response.Status.FORBIDDEN)
						.entity(APIErrorBuilder.buildError(
								Response.Status.FORBIDDEN.getStatusCode(),
								"FORBIDDEN", request)).build());
			}
			// obtenemos la conexion
			Connection connection = null;

			try {
				connection = DataSourceSAP.getInstance().getDataSource()
						.getConnection();
			} catch (SQLException e) {
				throw new WebApplicationException(Response
						.status(Response.Status.SERVICE_UNAVAILABLE)
						.entity(APIErrorBuilder.buildError(
								Response.Status.SERVICE_UNAVAILABLE
										.getStatusCode(),
								"Service unavailable.", request)).build());
			}

			try {

				// seleccionamos el id del usuario
				Statement stmt = connection.createStatement();
				StringBuilder sb = new StringBuilder(
						"select id from user where username ='" + username
								+ "';");
				ResultSet rs = stmt.executeQuery(sb.toString());

				rs.next();
				int iduser = rs.getInt("id");

				System.out.println("id artista= " + idartist);

				Statement stmt2 = connection.createStatement();

				// desmarcamos el evento asistido por el usuario.

				StringBuilder sb2 = new StringBuilder(
						"DELETE from follow where idartist=" + idartist
								+ " and iduser =" + iduser + ";");

				int rc = stmt2.executeUpdate(sb2.toString());
				if (rc == 0) {

					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"Follow artist not found.", request))
							.build());
				}

				stmt.close();
				stmt2.close();
				connection.close();

			} catch (SQLException e) {
				throw new WebApplicationException(Response
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(APIErrorBuilder.buildError(
								Response.Status.INTERNAL_SERVER_ERROR
										.getStatusCode(),
								"Internal server error.", request)).build());
			}
		}

	}

	private void insertFollowedArtist(String username, int idartist) {
		if (security.isUserInRole("registered")
				|| security.isUserInRole("admin")) {
			if (security.isUserInRole("registered")
					&& !security.getUserPrincipal().getName().equals(username)) {
				throw new WebApplicationException(Response
						.status(Response.Status.FORBIDDEN)
						.entity(APIErrorBuilder.buildError(
								Response.Status.FORBIDDEN.getStatusCode(),
								"FORBIDDEN", request)).build());
			}
			// obtenemos la conexion
			Connection connection = null;

			try {
				connection = DataSourceSAP.getInstance().getDataSource()
						.getConnection();
			} catch (SQLException e) {
				throw new WebApplicationException(Response
						.status(Response.Status.SERVICE_UNAVAILABLE)
						.entity(APIErrorBuilder.buildError(
								Response.Status.SERVICE_UNAVAILABLE
										.getStatusCode(),
								"Service unavailable.", request)).build());
			}

			try {

				// seleccionamos el id del usuario
				Statement stmt = connection.createStatement();
				StringBuilder sb = new StringBuilder(
						"select id from user where username ='" + username
								+ "';");
				ResultSet rs = stmt.executeQuery(sb.toString());

				rs.next();
				int iduser = rs.getInt("id");

				// Comprobamos que al artista al que quiera seguir no lo este
				// siguiendo ya.
				Statement stmt1 = connection.createStatement();

				StringBuilder sb1 = new StringBuilder(
						"SELECT id from follow where iduser='" + iduser
								+ "' and idartist='" + idartist + "';");

				ResultSet rs2 = stmt1.executeQuery(sb1.toString());

				if (rs2.next()) {

					throw new WebApplicationException(Response
							.status(Response.Status.CONFLICT)
							.entity(APIErrorBuilder.buildError(
									Response.Status.CONFLICT.getStatusCode(),
									"Ya estas siguiendo a ese artista.",
									request)).build());
				}

				Statement stmt2 = connection.createStatement();

				// marcamos el evento como asistido por el usuario.
				StringBuilder sb2 = new StringBuilder(
						"INSERT INTO  follow (iduser,idartist) values ('"
								+ iduser + "', '" + idartist + "');");

				int rc = stmt2.executeUpdate(sb2.toString());

				if (rc == 0) {

					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"Error al marcar el artista como seguido.",
									request)).build());
				}

				stmt2.close();
				stmt1.close();
				stmt.close();

				connection.close();

			} catch (SQLException e) {
				throw new WebApplicationException(Response
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(APIErrorBuilder.buildError(
								Response.Status.INTERNAL_SERVER_ERROR
										.getStatusCode(),
								"Internal server error.", request)).build());
			}
		}
	}
}