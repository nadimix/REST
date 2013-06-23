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
	public Response insertFollowedArtistJSON(
			@PathParam("username") String username,
			@QueryParam("idartist") int idartist) {
		insertFollowedArtist(username, idartist);
		Response response = null;
		try {
			response = Response.status(204)
					.location(new URI("/users/" + username + "/following"))
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
			@QueryParam("idartist") int idartist) {
		deleteFollowedArtist(username, idartist);
		return Response.status(204).build();
	}

	private List<Artist> getArtistsFollowed(String username) {
		if (security.isUserInRole("registered")) {
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
				int iduser = obtainIdUser(username);
				List<Artist> artists = getArtistFollowed(iduser);
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
		} else {
			throw new WebApplicationException(Response
					.status(Response.Status.FORBIDDEN)
					.entity(APIErrorBuilder.buildError(
							Response.Status.FORBIDDEN.getStatusCode(),
							"FORBIDDEN", request)).build());
		}
	}

	private void deleteFollowedArtist(String username, int idartist) {
		if (security.isUserInRole("registered")) {
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
				StringBuilder sb = new StringBuilder(
						"select id from user where username ='" + username
								+ "';");
				ResultSet rs = stmt.executeQuery(sb.toString());
				rs.next();
				int iduser = rs.getInt("id");
				System.out.println("id artista= " + idartist);
				Statement stmt2 = connection.createStatement();
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
		} else {
			throw new WebApplicationException(Response
					.status(Response.Status.FORBIDDEN)
					.entity(APIErrorBuilder.buildError(
							Response.Status.FORBIDDEN.getStatusCode(),
							"FORBIDDEN", request)).build());
		}
	}

	private void insertFollowedArtist(String username, int idartist) {
		if (security.isUserInRole("registered")) {
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
				StringBuilder sb = new StringBuilder(
						"select id from user where username ='" + username
								+ "';");
				ResultSet rs = stmt.executeQuery(sb.toString());
				rs.next();
				int iduser = rs.getInt("id");
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
		} else {
			throw new WebApplicationException(Response
					.status(Response.Status.FORBIDDEN)
					.entity(APIErrorBuilder.buildError(
							Response.Status.FORBIDDEN.getStatusCode(),
							"FORBIDDEN", request)).build());
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

	public boolean isFollowed(int idartist, int iduser) {
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
			// SELECT * FROM follow WHERE idartist=1 AND iduser=1;
			StringBuilder sb = new StringBuilder(
					"SELECT * FROM follow WHERE idartist=" + idartist
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

	public List<Artist> getArtistFollowed(int userId) {
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
			// SELECT artist.id, artist.name, artist.idgenre1, artist.idgenre2,
			// artist.info FROM artist INNER JOIN follow on
			// follow.idartist=artist.id and follow.iduser=1 order by name;
			StringBuilder sb = new StringBuilder(
					"SELECT artist.id, artist.name, artist.idgenre1, artist.idgenre2, ");
			sb.append("artist.info FROM artist INNER JOIN follow on ");
			sb.append("follow.idartist=artist.id and ");
			sb.append("follow.iduser=" + userId + " order by name;");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			List<Artist> artistList = new ArrayList<>();
			while (rs.next()) {
				Artist artist = new Artist();
				artist.setArtistid(rs.getInt("id"));
				artist.setName(rs.getString("name"));
				artist.setGenreId(rs.getInt("idgenre1"));
				artist.setGenre2Id(rs.getInt("idgenre2"));
				artist.setInfo(rs.getString("info"));
				boolean foll = isFollowed(artist.getArtistId(), userId);
				artist.setFollowed(foll);
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