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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import com.google.common.base.CharMatcher;

import dsa.colourmylife.rest.model.Event;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/artists/{artist}/events/{eventid}")
public class ArtistEventResource {
	@Context
	private UriInfo uri;

	@Context
	protected HttpServletRequest request;

	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	// Entiendo que para hacer un GET basta con especificar el eventid
	public Event getEventJSON(@PathParam("eventid") int eventid,
			@PathParam("artist") String name,
			@QueryParam("user") String username) {
		return getEvent(eventid, name, username);
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateEventJson(@PathParam("eventid") int eventid,
			Event event, @PathParam("artist") String name) {
		updateEvent(eventid, event, name);
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
	// Entiendo que para eliminar un evento basta con el eventid
	public Response deleteEventJSON(@PathParam("eventid") int eventid,
			@PathParam("artist") String name) {
		deleteEvent(eventid, name);
		return Response.status(204).build();
	}

	private Event getEvent(int eventid, String artistname, String username) {
		if (security.isUserInRole("registered")
				|| security.isUserInRole("admin")) {
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
				// select * from event where id = 1 and artist = "Florence";
				ResultSet rs = stmt
						.executeQuery("SELECT * FROM event WHERE id=" + eventid
								+ " and artist='" + artistname + "';");
				if (!rs.next()) {
					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"NOT FOUND", request)).build());
				}
				Event event = new Event();
				event.setEventId(rs.getInt("id"));
				event.setKindId(rs.getInt("idkind"));
				String kind2 = obtainKind(rs.getInt("idkind"));
				event.setKind(kind2);
				event.setArtist(rs.getString("artist"));
				event.setDate(rs.getString("date"));
				event.setPlace(rs.getString("place"));
				event.setCity(rs.getString("city"));
				event.setCountry(rs.getString("country"));
				event.setInfo(rs.getString("info"));
				event.setInsertdate(rs.getString("insertdate"));
				if (username != null) {
					int iduser = obtainIdUser(username);
					boolean fav = isFav(event.getEventId(), iduser);
					event.setFav(fav);
				}
				event.setLink(uri.getAbsolutePath().toString());
				System.out.println("Link: " + event.getLink());
				// @Path("/artists/{artist}/events/{eventid}")
				event.setSameKindLink(uri.getBaseUri().toString() + "/artists/"
						+ artistname + "/events?idkind=" + event.getKindId());
				event.setSameCountryLink(uri.getBaseUri().toString()
						+ "/artists/" + artistname + "/events?country="
						+ event.getCountry());
				stmt.close();
				connection.close();
				return event;
			} catch (SQLException e) {
				throw new WebApplicationException(Response
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(APIErrorBuilder.buildError(
								Response.Status.INTERNAL_SERVER_ERROR
										.getStatusCode(),
								"Error accessing to database.", request))
						.build());
			}
		} else {
			throw new WebApplicationException(Response
					.status(Response.Status.FORBIDDEN)
					.entity(APIErrorBuilder.buildError(
							Response.Status.FORBIDDEN.getStatusCode(),
							"FORBIDDEN", request)).build());
		}
	}

	private void updateEvent(int eventid, Event event, String artistname) {
		if (!security.isUserInRole("admin")) {
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
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE
											.getStatusCode(),
									"Service unavailable.", request)).build());
		}
		try {
			Statement stmt = connection.createStatement();
			if (event.getCountry() == null) {
				throw new WebApplicationException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(APIErrorBuilder.buildError(
								Response.Status.BAD_REQUEST.getStatusCode(),
								"Country camp mustn't be empty", request))
						.build());
			}
			// UPDATE event SET date='2014-09-20 22:00:00', place='Palau
			// Joventut', city='Badalona', country='Catalunya', info='new
			// Location' WHERE artist='Florence' AND id=1;

			if (artistExist(artistname) == false) {
				throw new WebApplicationException(Response
						.status(Response.Status.CONFLICT)
						.entity(APIErrorBuilder.buildError(
								Response.Status.CONFLICT.getStatusCode(),
								"Artist not found", request)).build());
			}

			StringBuilder sb = new StringBuilder("UPDATE event SET ");
			if (event.getDate() != null) {
				sb.append("date='" + event.getDate() + "', ");
			}
			if (event.getPlace() != null) {
				sb.append("place='" + event.getPlace() + "', ");
			}
			if (event.getCity() != null) {
				sb.append("city='" + event.getCity() + "', ");
			}
			if (event.getInfo() != null) {
				if (isAscii(event.getInfo()) == false) {
					throw new WebApplicationException(
							Response.status(Response.Status.BAD_REQUEST)
									.entity(APIErrorBuilder.buildError(
											Response.Status.BAD_REQUEST
													.getStatusCode(),
											"Only ASCII characters are allowed",
											request)).build());
				}
				sb.append("info='" + event.getInfo() + "', ");
			}
			sb.append("country='" + event.getCountry() + "'");
			sb.append(" WHERE artist='" + artistname + "' AND id=" + eventid
					+ ";");
			System.out.println(sb);

			int rs = stmt.executeUpdate(sb.toString());
			if (rs == 0)
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Event NOT FOUND", request)).build());
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	private void deleteEvent(int eventid, String name) {
		if (!security.isUserInRole("admin")) {
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
			throw new WebApplicationException(
					Response.status(Response.Status.SERVICE_UNAVAILABLE)
							.entity(APIErrorBuilder.buildError(
									Response.Status.SERVICE_UNAVAILABLE
											.getStatusCode(),
									"Service unavailable.", request)).build());
		}
		try {
			Statement stmt = connection.createStatement();
			// DELETE FROM event WHERE id=2 and artist='Florence';
			int rs = stmt.executeUpdate("DELETE FROM event WHERE id=" + eventid
					+ " and artist='" + name + "';");
			if (rs == 0)
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Event not found.", request)).build());
			stmt.close();
			connection.close();
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	public String obtainKind(int kindid) {
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
			// SELECT name FROM kind WHERE id=1;
			StringBuilder sb = new StringBuilder(
					"SELECT name FROM kind WHERE id=" + kindid + ";");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			if (!rs.next()) {
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Kind not found.", request)).build());
			}
			String kind = rs.getString("name");
			System.out.println("kind: " + kind);
			stmt.close();
			connection.close();
			return kind;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	public int obtainIdUser(String username) {
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
			// SELECT id FROM user WHERE username='ubuntu';
			StringBuilder sb = new StringBuilder(
					"SELECT id FROM user WHERE username='" + username + "';");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			if (!rs.next()) {
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"User not found.", request)).build());
			}
			int id = rs.getInt("id");
			System.out.println("User id: " + id);
			stmt.close();
			connection.close();
			return id;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	public boolean isFav(int idevent, int iduser) {
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
			// SELECT * FROM assist WHERE idevent=1 AND iduser=1;
			StringBuilder sb = new StringBuilder(
					"SELECT * FROM assist WHERE idevent=" + idevent
							+ " AND iduser=" + iduser + ";");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			if (!rs.next()) {
				stmt.close();
				connection.close();
				return false;
			} else {
				stmt.close();
				connection.close();
				return true;
			}
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	public boolean isAscii(String someString) {
		return CharMatcher.ASCII.matchesAllOf(someString);
	}

	public boolean artistExist(String artistname) {
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
			// SELECT * FROM artist WHERE name='Florence';
			StringBuilder sb = new StringBuilder(
					"SELECT * FROM artist WHERE name = '" + artistname + "';");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			if (!rs.next()) {
				stmt.close();
				connection.close();
				return false;
			}
			stmt.close();
			connection.close();
			return true;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}
	
}
