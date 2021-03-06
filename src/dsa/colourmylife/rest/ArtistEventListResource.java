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

import com.google.common.base.CharMatcher;

import dsa.colourmylife.rest.model.Event;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/artists/{artist}/events")
public class ArtistEventListResource {
	@Context
	private UriInfo uri;
	@Context
	protected HttpServletRequest request;
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getEventListJSON(@PathParam("artist") String artistname,
			@QueryParam("city") String city, @QueryParam("kind") String kind,
			@QueryParam("user") String username) {
		return getEventList(artistname, city, kind, username);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEventJSON(Event event,
			@PathParam("artist") String artistname) {
		int eventid = insertEvent(event, artistname);
		Response response = null;
		try {
			response = Response
					.status(204)
					.location(
							new URI("/artists/" + artistname + "/events/"
									+ eventid)).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
	}

	private List<Event> getEventList(String artist, String city, String kind,
			String username) {
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
				StringBuilder sb = new StringBuilder();
				if (artist == null) {
					// SELECT * FROM event;
					sb = new StringBuilder("SELECT * FROM event;");
					System.out.println(sb);
				}
				if (city != null && kind == null) {
					// SELECT * FROM event WHERE artist='Florence' AND
					// city='Badalona';
					sb = new StringBuilder("SELECT * FROM event WHERE artist='"
							+ artist + "' AND city='" + city + "';");
					System.out.println(sb);
				}
				if (city == null && kind != null) {
					// SELECT * FROM event WHERE artist='Florence' AND idkind=1;
					int kindId = obtainKindId(kind);
					sb = new StringBuilder("SELECT * FROM event WHERE artist='"
							+ artist + "' AND idkind='" + kindId + "';");
					System.out.println(sb);
				}
				if (kind != null && city != null) {
					// SELECT * FROM event WHERE artist='Florence' AND idkind=1
					// AND
					// city='Badalona';
					int kindId = obtainKindId(kind);
					sb = new StringBuilder("SELECT * FROM event WHERE artist='"
							+ artist + "' AND idkind='" + kindId
							+ "' AND city='" + city + "';");
					System.out.println(sb);
				}
				if (city == null && kind == null) {
					// SELECT * FROM event WHERE artist='Florence';
					sb = new StringBuilder("SELECT * FROM event WHERE artist='"
							+ artist + "';");
					System.out.println(sb);
				}
				System.out.println(sb);

				ResultSet rs = stmt.executeQuery(sb.toString());
				List<Event> artistEventList = new ArrayList<Event>();
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
					if (username != null) {
						int iduser = obtainIdUser(username);
						boolean fav = isFav(event.getEventId(), iduser);
						event.setFav(fav);
					}
					event.setLink(uri.getAbsolutePath().toString());
					System.out.println("Link: " + event.getLink());
					event.setSameKindLink(uri.getBaseUri().toString()
							+ "artists/" + artist + "/events?kind="
							+ event.getKind());
					event.setSameCountryLink(uri.getBaseUri().toString()
							+ "artists/" + artist + "/events?country="
							+ event.getCountry());
					artistEventList.add(event);
				}
				if (artistEventList.size() == 0)
					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"No Event List found.", request)).build());
				stmt.close();
				connection.close();
				return artistEventList;
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

	private int insertEvent(Event event, String artist) {
		if (!security.isUserInRole("admin")) {
			throw new WebApplicationException(Response
					.status(Response.Status.FORBIDDEN)
					.entity(APIErrorBuilder.buildError(
							Response.Status.FORBIDDEN.getStatusCode(),
							"FORBIDDEN", request)).build());
		}
		int eventid = 0;
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
			if (artistExist(artist) == false) {
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Artist not found.", request)).build());
			}
			if (event.getKindId() == 0 || event.getCountry() == null) {
				throw new WebApplicationException(Response
						.status(Response.Status.CONFLICT)
						.entity(APIErrorBuilder.buildError(
								Response.Status.CONFLICT.getStatusCode(),
								"KindId or Country fields mustn't been empty",
								request)).build());
			}
			if (idKindExists(event.getKindId()) == false) {
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Kind not found.", request)).build());
			}

			String insert = buildInsert(event, artist);
			stmt.executeUpdate(insert, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				eventid = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {

			}
		}
		return eventid;
	}

	public String buildInsert(Event event, String artist) {
		// INSERT INTO event VALUES (NULL, 1, 'Florence', '2013-09-20 22:00:00',
		// 'Palau Sant Jordi', 'Barcelona', 'Catalunya', 'Va a ser inolvidable',
		// NOW());
		// StringBuilder sb = new StringBuilder();
		// sb.append("INSERT INTO event VALUES (NULL, " + event.getKindId());
		// sb.append(", '" + artist + "', '" + event.getDate() + "', '");
		// sb.append(event.getPlace() + "', '" + event.getCity() + "', '");
		// sb.append(event.getCountry() + "', '" + event.getInfo() +
		// "', NOW());");
		StringBuilder sb = new StringBuilder("INSERT INTO event VALUES (NULL, ");
		sb.append(event.getKindId() + ", '");
		sb.append(artist + "', ");
		if (event.getDate() != null) {
			sb.append("'" + event.getDate() + "', ");
		} else
			sb.append("NULL, ");
		if (event.getPlace() != null) {
			sb.append("'" + event.getPlace() + "', ");
		} else
			sb.append("NULL, ");
		if (event.getCity() != null) {
			sb.append("'" + event.getCity() + "', ");
		} else
			sb.append("NULL, ");
		sb.append("'" + event.getCountry() + "', ");
		if (event.getInfo() != null) {
			if (isAscii(event.getInfo()) != true) {
				throw new WebApplicationException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(APIErrorBuilder.buildError(
								Response.Status.BAD_REQUEST.getStatusCode(),
								"Only ASCII characters are allowed", request))
						.build());
			}
			sb.append("'" + event.getInfo() + "', ");
		} else
			sb.append("NULL, ");
		sb.append("NOW());");
		System.out.println("Query: " + sb.toString());
		return sb.toString();
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

	public boolean idKindExists(int idkind) {
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
			// SELECT * FROM kind WHERE id=1;
			StringBuilder sb = new StringBuilder("SELECT * FROM kind WHERE id="
					+ idkind + ";");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			if (!rs.next()) {
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
