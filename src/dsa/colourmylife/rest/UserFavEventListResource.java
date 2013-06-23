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
import javax.ws.rs.core.UriInfo;

import dsa.colourmylife.rest.model.Artist;
import dsa.colourmylife.rest.model.Event;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/users/{username}/events/fav")
public class UserFavEventListResource {
	@Context
	private UriInfo uri;
	@Context
	protected HttpServletRequest request;
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getEventListJSON(@PathParam("username") String username,
			@QueryParam("kind") String kind, @QueryParam("idevent") int eventid) {
		return getEventList(username, kind, eventid);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response assistEventJSON(@PathParam("username") String username,
			@QueryParam("idevent") int idevent) {

		assistEvent(username, idevent);
		Response response = null;
		try {

			// PREGUNTAR QUE LOCATION TENEMOS QUE PONER DEL RESPONSE STATUS!!

			response = Response.status(204)
					.location(new URI("/users/" + username + "/events/fav"))
					.build();

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteAssistEventJSON(
			@PathParam("username") String username,
			@QueryParam("idevent") int idevent) {

		deleteAssistEvent(username, idevent);
		return Response.status(204).build();
	}

	private void assistEvent(String username, int idevent) {
		if (security.isUserInRole("registered")
		/* || security.isUserInRole("admin") */) {
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

			// Marcamos el evento al que el usuario ha asistido con la consulta
			// a la
			// base de datos.

			try {

				Statement stmt = connection.createStatement();
				StringBuilder sb = new StringBuilder(
						"select * from user where username ='" + username + "'");
				System.out.println(sb);
				ResultSet rs = stmt.executeQuery(sb.toString());

				rs.next();
				int iduser = rs.getInt("id");

				// Comprobamos que al artista al que quiera seguir no lo este
				// siguiendo ya.
				Statement stmt1 = connection.createStatement();

				StringBuilder sb1 = new StringBuilder(
						"SELECT id from assist where iduser=" + iduser
								+ " and idevent='" + idevent + "';");
				System.out.println(sb1);

				ResultSet rs2 = stmt1.executeQuery(sb1.toString());

				if (rs2.next()) {
					throw new WebApplicationException(
							Response.status(Response.Status.CONFLICT)
									.entity(APIErrorBuilder.buildError(
											Response.Status.CONFLICT
													.getStatusCode(),
											"Ya has marcado este evento como asistido.",
											request)).build());
				}

				Statement stmt2 = connection.createStatement();
				// marcamos el evento como asistido por el usuario.
				StringBuilder sb2 = new StringBuilder(
						"INSERT INTO assist (iduser,idevent) values (" + iduser
								+ "," + idevent + ");");
				System.out.println(sb2);
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
										.getStatusCode(),
								"Internal server error.", request)).build());
			}
		}
	}

	private void deleteAssistEvent(String username, int idevent) {
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

				/*
				 * Statement stmt = connection.createStatement(); StringBuilder
				 * sb = new StringBuilder( "select id from user where name ='" +
				 * username+ "'"); ResultSet rs =
				 * stmt.executeQuery(sb.toString());
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
						"DELETE from assist where idevent='" + idevent + "';");

				int rc = stmt2.executeUpdate(sb2.toString());
				if (rc == 0) {

					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"Event assisted not found.", request))
							.build());
				}

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

	private List<Event> getEventList(String username, String kind, int eventid) {
		if (security.isUserInRole("registered")
		/* || security.isUserInRole("admin") */) {
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
				StringBuilder sb = null;
				int iduser = obtainIdUser(username);
				List<Event> idEventAssist = getEventAssistList(iduser);
				List<Event> eventAssist = new ArrayList<>();
				for (Event E : idEventAssist) {
					Statement stmt = connection.createStatement();
					if (kind == null) {
						sb = new StringBuilder("SELECT * FROM event WHERE id="
								+ E.getEventId() + ";");
						System.out.println(sb);
					} else {
						int kindId = obtainKindId(kind);
						sb = new StringBuilder("SELECT * FROM event WHERE id='"
								+ E.getEventId() + " AND idkind=" + kindId
								+ ";");
						System.out.println(sb);
					}
					ResultSet rs = stmt.executeQuery(sb.toString());
					while (rs.next()) {
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
						boolean fav = isFav(event.getEventId(), iduser);
						event.setFav(fav);
						event.setLink(uri.getBaseUri().toString() + "artists/"
								+ event.getArtist() + "/events/"
								+ event.getEventId());
						System.out.println("URL: " + event.getLink());
						event.setSameKindLink(uri.getBaseUri().toString()
								+ "artists/" + event.getArtist()
								+ "/events?kind=" + event.getKind());
						event.setSameCountryLink(uri.getBaseUri().toString()
								+ "artists/" + event.getArtist()
								+ "/events?country=" + event.getCountry());
						eventAssist.add(event);
					}
					stmt.close();
				}
				connection.close();
				return eventAssist;
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
		throw new WebApplicationException(Response
				.status(Response.Status.FORBIDDEN)
				.entity(APIErrorBuilder.buildError(
						Response.Status.FORBIDDEN.getStatusCode(), "FORBIDDEN",
						request)).build());
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

	public String obtainArtistName(int id) {
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
			// SELECT name FROM artist WHERE id=1;
			StringBuilder sb = new StringBuilder(
					"SELECT name FROM artist WHERE id=" + id + ";");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			if (!rs.next()) {
				throw new WebApplicationException(
						Response.status(Response.Status.NOT_FOUND)
								.entity(APIErrorBuilder.buildError(
										Response.Status.NOT_FOUND
												.getStatusCode(),
										"Artist not found maybe you need to follow someone.",
										request)).build());
			}
			String name = rs.getString("name");
			System.out.println("Artist :" + name);
			stmt.close();
			connection.close();
			return name;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	public List<Artist> getArtistNameList(int userId) {
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
			StringBuilder sb = new StringBuilder(
					"SELECT idartist FROM follow where iduser=" + userId + ";");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			List<Artist> artistList = new ArrayList<>();
			while (rs.next()) {
				Artist artist = new Artist();
				artist.setArtistid(rs.getInt("idartist"));
				System.out.println("Artist id: " + artist.getArtistId());
				String name = obtainArtistName(artist.getArtistId());
				System.out.println("Name check: " + name);
				artist.setName(name);
				System.out.println("Artist: " + artist.getName());
				artistList.add(artist);
			}
			stmt.close();
			connection.close();
			return artistList;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	public int obtainKindId(String kind) {
		System.out.println("kind: " + kind);
		if (kind.equals("Concert") || kind.equals("concert")
				|| kind.equals("concierto") || kind.equals("festival")
				|| kind.equals("live") || kind.equals("directo")
				|| kind.equals("fest")) {
			int kindId = 1;
			return kindId;
		}
		if (kind.equals("Studio Album Release")
				|| kind.equals("studio album release") || kind.equals("CD")
				|| kind.equals("album") || kind.equals("soundtrack")
				|| kind.equals("single") || kind.equals("sencillo")
				|| kind.equals("studio")) {
			int kindId = 2;
			return kindId;
		}
		if (kind.equals("Videoclip Release")
				|| kind.equals("videoclip release") || kind.equals("video")
				|| kind.equals("clip") || kind.equals("videoclip")) {
			int kindId = 3;
			return kindId;
		} else {
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
				// SELECT id FROM kind WHERE name ='Concert';
				StringBuilder sb = new StringBuilder(
						"SELECT id FROM kind WHERE name='" + kind + "';");
				System.out.println(sb);
				ResultSet rs = stmt.executeQuery(sb.toString());
				if (!rs.next()) {
					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"Kind not found.", request)).build());
				}
				int kindId = rs.getInt("id");
				System.out.println("kindId: " + kindId);
				stmt.close();
				connection.close();
				return kindId;
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

	public List<Event> getEventAssistList(int userid) {
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
			// SELECT idevent FROM assist WHERE iduser=1;
			StringBuilder sb = new StringBuilder(
					"SELECT idevent FROM assist WHERE iduser=" + userid + ";");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			List<Event> eventAssistList = new ArrayList<>();
			while (rs.next()) {
				Event event = new Event();
				event.setEventId(rs.getInt("idevent"));
				System.out.println("Eventid Assist: " + event.getEventId());
				eventAssistList.add(event);
			}
			stmt.close();
			connection.close();
			return eventAssistList;
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

}