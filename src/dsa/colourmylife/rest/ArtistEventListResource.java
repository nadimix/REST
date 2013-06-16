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

import dsa.colourmylife.rest.model.Event;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/artists/{artist}/events")
public class ArtistEventListResource {
	// ArtistEventListResource: ./artists/{artist}/events
	// GET → Lista eventos.
	// POST → Crear eventos.
	// id int(11) NOT NULL AUTO_INCREMENT,
	// idkind int(11) NOT NULL,
	// artist varchar(50) NOT NULL,
	// date datetime default NULL,
	// place varchar(128) NULL,
	// city varchar(50) NOT NULL,
	// country varchar(50) NOT NULL,
	// info varchar(150),
	// insertdate datetime default current_timestamp,

	@Context
	private UriInfo uri;
	@Context
	protected HttpServletRequest request;
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getEventListJSON(@PathParam("artist") String artistname,
			@QueryParam("city") String city) {
		// All of this aren't NULL
		return getEventList(artistname, city);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createEventJSON(Event event, @PathParam("artist") String artistname) {
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

	private List<Event> getEventList(String artist, String city) {
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
			StringBuilder sb = null;
			// if (artist != null && city != null) {
			// throw new WebApplicationException(
			// Response.status(Response.Status.BAD_REQUEST)
			// .entity(APIErrorBuilder.buildError(
			// Response.Status.BAD_REQUEST
			// .getStatusCode(),
			// "Can't list events for artist and city at the same time.",
			// request)).build());
			// }
			if (city == null) {
				// SELECT * FROM event WHERE artist='Florence';
				sb = new StringBuilder("SELECT * FROM event WHERE artist='"
						+ artist + "';");
			}
			if (city != null) {
				// SELECT * FROM event WHERE artist='Florence' AND
				// city='Badalona';
				sb = new StringBuilder("SELECT * FROM event WHERE artist='"
						+ artist + "' AND city='" + city + "';");
			}
			if (artist == null && city == null) {
				// SELECT * FROM event;
				sb = new StringBuilder("SELECT * FROM event;");
			}
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
				event.setLink(uri.getAbsolutePath().toString());
				System.out.println("Link: "+event.getLink());
				// @Path("/artists/{artist}/events/{eventid}")
				event.setSameKindLink(uri.getBaseUri().toString() + "artists/"
						+ artist + "/events?idkind=" + event.getKindId());
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
							"Error accessing to database.", request)).build());
		}
	}

	private int insertEvent(Event event, String artist) {
		int id = 0;
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
			String insert = buildInsert(event, artist);
			id = event.getEventId();
			int rs = stmt.executeUpdate(insert);
			if (rs == 0)
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Event not found.", request)).build());
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
		return id;
	}

	private String buildInsert(Event event, String artist) {
		StringBuilder sb = new StringBuilder();
		// INSERT INTO event VALUES (NULL, 1, 'Florence', '2013-09-20 22:00:00',
		// 'Palau Sant Jordi', 'Barcelona', 'Catalunya', 'Va a ser inolvidable',
		// NOW());
		sb.append("INSERT INTO event VALUES (NULL, " + event.getEventId());
		sb.append(", '" + artist + "', '" + event.getDate() + "', '");
		sb.append(event.getPlace() + "', '" + event.getCity() + "', '");
		sb.append(event.getCountry() + "', '" + event.getInfo() + "', NOW());");
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
}
