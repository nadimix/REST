package dsa.musicalevents.rest;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import dsa.musicalevents.rest.model.Artist;
import dsa.musicalevents.rest.util.APIErrorBuilder;
import dsa.musicalevents.rest.util.DataSourceSAP;

@Path("/artists/{artist}")
public class ArtistResource {
	// Recurso Artist: ./artists/{artist}
	// GET → Obtener perfil artista.
	// PUT → Actualizar artista.
	// DELETE → Eliminar artista.
	@Context
	private HttpServletRequest request;
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Artist getArtistJSON(@PathParam("artist") String artistname) {
		return getArtist(artistname);
	}

	// @PUT
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response updateArtistJSON(@PathParam("artist") String artistname,
	// Artist artist) {
	// // TODO UpdateArtist(artisName, artist);
	// UpdateArtist(artistname, artist);
	// Response response = null;
	//
	// try {
	// response = Response.status(204)
	// .location(new URI("/artists/" + artistname)).build();
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// }
	// return response;
	// }

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteArtistJSON(@PathParam("artist") String artistname) {
		deleteArtist(artistname);
		return Response.status(204).build();
	}

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

	// private void updateArtist(String artistname, Artist artist) {
	// if (security.isUserInRole("registered")
	// || security.isUserInRole("admin")) {
	// if (security.isUserInRole("registered")) {
	// throw new WebApplicationException(
	// Response.status(Response.Status.FORBIDDEN)
	// .entity(APIErrorBuilder.buildError(
	// Response.Status.FORBIDDEN
	// .getStatusCode(),
	// "No tienes permiso para modificar este usuario",
	// request)).build());
	// }
	// Connection connection = null;
	// try {
	// connection = DataSourceSAP.getInstance().getDataSource()
	// .getConnection();
	// } catch (SQLException e) {
	// throw new WebApplicationException(Response
	// .status(Response.Status.SERVICE_UNAVAILABLE)
	// .entity(APIErrorBuilder.buildError(
	// Response.Status.SERVICE_UNAVAILABLE
	// .getStatusCode(),
	// "Service unavailable.", request)).build());
	// }
	//
	// Statement stmt = connection.createStatement();
	// // Podemos cambiar el nombre y los géneros
	// // TODO consulta que me de el id del artista
	// // TODO consula mediante la cual pueda cambiar el nombre
	// StringBuilder sb = new StringBuilder("update ...");
	// }
	// }

	private void deleteArtist(String artistname) {
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
				// TODO query borrar artista
				int rs = stmt.executeUpdate("query");
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
								"Error accessing to database.", request))
						.build());
			}
		}
	}
}
