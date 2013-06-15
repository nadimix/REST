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
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import dsa.colourmylife.rest.model.Artist;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/artists")
public class ArtistListResource {
	// Recurso ArtistList: ./artists
	// GET → Lista artistas.
	// POST → Añadir artistas.
	// id int(11) NOT NULL AUTO_INCREMENT,
	// name varchar(50) NOT NULL,
	// idgenre1 int(11) NOT NULL,
	// idgenre2 int(11) NULL,
	// info varchar(150),

	@Context
	private UriInfo uri;

	@Context
	protected HttpServletRequest request;

	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Artist> getArtistListJSON() {
		return getArtistList();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createArtistJSON(Artist artist) {
		insertArtist(artist);
		Response response = null;
		try {
			response = Response.status(204)
					.location(new URI("/artist" + artist.getName())).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
	}

	private void insertArtist(Artist artist) {
		// System.out.println(security.getUserPrincipal().getName());
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
			if (artist.getName() == null || artist.getGenreId() == 0
					|| artist.getInfo() == null) {
				throw new WebApplicationException(
						Response.status(Response.Status.BAD_REQUEST)
								.entity(APIErrorBuilder.buildError(
										Response.Status.BAD_REQUEST
												.getStatusCode(),
										"ArtistName, GenreId and Info camps mustn't be empty",
										request)).build());

			}

			if (getArtist(artist.getName()) != null) {
				throw new WebApplicationException(Response
						.status(Response.Status.CONFLICT)
						.entity(APIErrorBuilder.buildError(
								Response.Status.CONFLICT.getStatusCode(),
								"Artist already exists", request)).build());
			}

			connection.setAutoCommit(false);
			// Insertamos artista en la BD
			try {
				Statement stmt = connection.createStatement();
				// INSERT INTO artist VALUES (NULL, "Florence", 4, NULL,
				// "Grupo imprescindible");
				StringBuilder sb = new StringBuilder(
						"INSERT INTO artist VALUES (NULL, '" + artist.getName()
								+ "', " + artist.getGenreId() + ", "
								+ artist.getGenre2Id() + ", '"
								+ artist.getInfo() + "');");
				System.out.println(sb);
				int rs = stmt.executeUpdate(sb.toString());
				if (rs == 0)
					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"Artist not found.", request)).build());
				stmt.close();
			} catch (SQLException e) {
				throw new WebApplicationException(Response
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.entity(APIErrorBuilder.buildError(
								Response.Status.INTERNAL_SERVER_ERROR
										.getStatusCode(),
								"Error accessing to database.", request))
						.build());
			}
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} finally {
			try {
				connection.setAutoCommit(true);
				connection.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	private List<Artist> getArtistList() {
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
			StringBuilder sb = new StringBuilder("SELECT * FROM artist;");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			// SELECT name FROM genre WHERE id=(SELECT idgenre1 FROM artist
			// WHERE id=1);
			List<Artist> artistList = new ArrayList<>();
			while (rs.next()) {
				Artist artist = new Artist();
				artist.setArtistid(rs.getInt("id"));
				artist.setName(rs.getString("name"));
				artist.setGenreId(rs.getInt("idgenre1"));
				artist.setGenre2Id(rs.getInt("idgenre2"));
				artist.setInfo(rs.getString("info"));
				String genre1 = obtainGenre(artist.getGenreId());
				artist.setGenre(genre1);
				if (artist.getGenre2Id() != 0) {
					String genre2 = obtainGenre(artist.getGenre2Id());
					artist.setGenre2(genre2);
				}
				artistList.add(artist);
				System.out.println("Artist: "+artist.getName());
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

	public Artist getArtist(String name) {
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
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM artist WHERE name = '" + name
							+ "';");
			if (!rs.next()) {
				return null;
			}
			Artist artist = new Artist();
			artist.setName(rs.getString("name"));
			stmt.close();
			connection.close();
			return artist;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	public String obtainGenre(int genreid) {
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
			// SELECT name FROM genre WHERE id=1;
			ResultSet rs = stmt.executeQuery("SELECT name FROM genre WHERE id="
					+ genreid + ";");
			if (!rs.next()) {
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Genre not found.", request)).build());
			}
			String genre = rs.getString("name");
			System.out.println(genre);
			stmt.close();
			connection.close();
			return genre;
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
