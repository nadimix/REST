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

import dsa.colourmylife.rest.model.Artist;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/artists")
public class ArtistListResource {
	// Recurso ArtistList: ./artists
	// GET → Lista artistas.
	// POST → Añadir artistas.

	@Context
	protected HttpServletRequest request;
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Artist> getArtistListJSON() {
		// TODO método para obtener lista de Artistas
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
					.location(new URI("/artists" + artist.getName())).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
	}

	private void insertArtist(Artist artist) {
		// Solo el Admin puede insertar Artistas
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
				if (artist.getName() == null || artist.getGenre() == null) {
					throw new WebApplicationException(
							Response.status(Response.Status.BAD_REQUEST)
									.entity(APIErrorBuilder.buildError(
											Response.Status.BAD_REQUEST
													.getStatusCode(),
											"The Artist and first genre mustn't be empty",
											request)).build());

				}
				if (getArtist(artist.getName()) != null) {
					throw new WebApplicationException(Response
							.status(Response.Status.CONFLICT)
							.entity(APIErrorBuilder.buildError(
									Response.Status.CONFLICT.getStatusCode(),
									"Usuario ya existe", request)).build());

				}
				connection.setAutoCommit(false);
				// Insertamos artista en la BD
				try {
					Statement stmt = connection.createStatement();
					// TODO query para insertar nombre artista y géneros
					// (considerar
					// caso
					// que hay 2 géneros en un if)
					StringBuilder sb = new StringBuilder("query");
					System.out.println(sb);
					int rs = stmt.executeUpdate(sb.toString());
					if (rs == 0)
						throw new WebApplicationException(Response
								.status(Response.Status.NOT_FOUND)
								.entity(APIErrorBuilder.buildError(
										Response.Status.NOT_FOUND
												.getStatusCode(),
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
				}
			} finally {
				try {
					connection.setAutoCommit(true);
					connection.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	// TODO preguntar a Sergio cómo utilizar esta clase desde fuera, para no
	// repetirla
	public Artist getArtist(String artistname) {
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
			// TODO poner query que permita obtener artistid, artistname,
			// idgenre1, idgendre2
			ResultSet rs = stmt.executeQuery("query");
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
			// TODO comprobar qué pasa si genre2Id no existe
			artist.setGenre2Id(rs.getInt("idgenre2"));
			// TODO hacer método que me pase la genreId a genre
			// artist.setGenre("genre");
			// artist.setGenre2("genre2");
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
			// TODO query que me de la lista de Artistas
			ResultSet rs = stmt.executeQuery("query");
			List<Artist> artistList = new ArrayList<>();
			while (rs.next()) {
				Artist artist = new Artist();
				artist.setArtistid(rs.getInt("id"));
				artist.setName(rs.getString("name"));
				artist.setGenreId(rs.getInt("idgenre1"));
				// TODO comprobar qué pasa si genre2Id no existe
				artist.setGenre2Id(rs.getInt("idgenre2"));
				// TODO hacer método que me pase la genreId a genre
				// artist.setGenre("genre");
				// artist.setGenre2("genre2");
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

}
