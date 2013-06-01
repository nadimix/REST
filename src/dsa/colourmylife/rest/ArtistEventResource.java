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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import dsa.colourmylife.rest.model.Artist;
import dsa.colourmylife.rest.model.Event;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/artists/{artist}/events/{eventid}")
public class ArtistEventResource {
	// Recurso Event: ./artists/{artist}/events/{eventid}
	// GET → Obtener evento. *
	// PUT → Actualizar evento. *
	// DELETE → Eliminar evento.
	// id int(11) NOT NULL AUTO_INCREMENT,
	// idkind int(11) NOT NULL,
	// idartist int(11) NOT NULL,
	// date datetime default NULL,
	// place varchar(128) NOT NULL,
	// city varchar(50) NOT NULL,

	@Context
	private UriInfo uri;

	@Context
	protected HttpServletRequest request;

	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Event getEventJSON(@PathParam("eventid") int eventid,
			@PathParam("artist") String artistname) {
		return getEvent(eventid, artistname);
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	// Al tener Produces, se le pasa el objeto Event por parámetro
	public Response updateEventJson(@PathParam("eventid") int eventid,
			Event event, @PathParam("artist") String artistname) {
		updateEvent(eventid, event, artistname);
		// Le paso un evento y un artista (string) que al introducirlo en un
		// atributo del evento luego puedo obtenerlo invocándolo a partir del
		// event.getArtist
		Response response = null;
		try {
			response = Response
					.status(204)
					.location(
							new URI("/artists/" + event.getArtist()
									+ "/events/" + event.getEventId())).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteArtistJSON(@PathParam("eventid") int eventid,
			@PathParam("artist") String artistname) {
		// TODO deleteEvent method
		deleteEvent(eventid, artistname);
		return Response.status(204).build();
	}

	public Event getEvent(int eventid, String artistname) {
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
			Statement stmt = connection.createStatement();
			// TODO poner query que permita obtener un artistid, id event,
			// idkind, place, city
			ResultSet rs = stmt.executeQuery("query");
			if (!rs.next()) {
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Artist not found.", request)).build());
			}

			Event event = new Event();
			event.setEventId(rs.getInt("id"));
			event.setKindId(rs.getInt("idkind"));
			event.setArtistId(rs.getInt("idartist"));
			// TODO métodos que me pasen de idkind e idartist a sus respectivos
			// strings.
			// event.setArtist("artistname"));
			// event.setKind("kind");
			stmt.close();
			connection.close();
			return event;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	public void updateEvent(int eventid, Event event, String artistname) {
		if (security.isUserInRole("admin")) {
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
				// podemos modificar date, place, city, country
				// TODO consulta
				StringBuilder sb = new StringBuilder("consulta");
				if (event.getCity() == null || event.getCountry() == null) {
					throw new WebApplicationException(
							Response.status(Response.Status.BAD_REQUEST)
									.entity(APIErrorBuilder.buildError(
											Response.Status.BAD_REQUEST
													.getStatusCode(),
											"Country and City must not be NULL",
											request)).build());
				}
				sb.append("terminar consulta");
				System.out.println(sb);

				int rs = stmt.executeUpdate(sb.toString());
				if (rs == 0)
					throw new WebApplicationException(
							Response.status(Response.Status.NOT_FOUND)
									.entity(APIErrorBuilder.buildError(
											Response.Status.NOT_FOUND
													.getStatusCode(),
											"Problem Occurs Between Chair And Keyboard",
											request)).build());
				stmt.close();
				connection.close();
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
	}

	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response createEventJSON(Event event, String artistname) {
	// // TODO Hacer método insertEvent
	// insertEvent(event, artistname);
	// // Le paso un evento y un artista (string) que al introducirlo en un
	// // atributo del evento luego puedo obtenerlo invocándolo a partir del
	// // event.getArtist
	// Response response = null;
	// try {
	// response = Response
	// .status(204)
	// .location(
	// new URI("/artists/" + event.getArtist()
	// + "/events/" + event.getEventId())).build();
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// }
	// return response;
	// }
}
