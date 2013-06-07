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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import dsa.colourmylife.rest.model.Artist;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/artists/{artist}")
public class ArtistResource {
	// Recurso Artist: ./artists/{artist}
	// GET → Obtener perfil artista.
	// PUT → Actualizar artista.
	// DELETE → Eliminar artista.
	// id int(11) NOT NULL AUTO_INCREMENT,
	// name varchar(50) NOT NULL,
	// idgenre1 int(11) NOT NULL,
	// idgenre2 int(11) NULL,
	// info varchar(150),

	@Context
	private HttpServletRequest request;

	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Artist getArtistJSON(@PathParam("artist") String name) {
		return getArtist(name);
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteArtistJSON(@PathParam("artist") String name) {
		deleteArtist(name);
		return Response.status(204).build();
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateArtistJSON(@PathParam("artist") String name,
			Artist artist) {
		updateArtist(name, artist);
		Response response = null;
		// Podemos cambiar solo el apartado info
		try {
			response = Response.status(204)
					.location(new URI("/artists/" + name)).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
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
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Artist not found.", request)).build());
			}

			Artist artist = new Artist();
			artist.setArtistid(rs.getInt("id"));
			artist.setName(rs.getString("name"));
			artist.setGenreId(rs.getInt("idgenre1"));
			artist.setGenre2Id(rs.getInt("idgenre2"));
			artist.setInfo(rs.getString("info"));
			stmt.close();
			connection.close();
			String genre1 = obtainGenre(artist.getGenreId());
			artist.setGenre(genre1);
			// Genre2 can be null
			if (artist.getGenre2Id() != 0) {
				String genre2 = obtainGenre(artist.getGenre2Id());
				artist.setGenre2(genre2);
			}
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

	private void deleteArtist(String name) {
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
			// DELETE FROM artist WHERE name ='Florence';
			int rs = stmt.executeUpdate("DELETE FROM artist WHERE name ='"
					+ name + "';");
			if (rs == 0)
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Artist not found.", request)).build());
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

	private void updateArtist(String name, Artist artist) {
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
			// Only allow to change Information
			// UPDATE artist SET info='Formados por una vocalista besada por el
			// fuego' WHERE name='Florence';
			StringBuilder sb = new StringBuilder("UPDATE artist SET info='"
					+ artist.getInfo() + "' WHERE name='" + name + "';");
			System.out.println(sb);

			int rs = stmt.executeUpdate(sb.toString());

			if (rs == 0) {
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Artist not found.", request)).build());
			}

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