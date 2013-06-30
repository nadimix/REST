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
import dsa.colourmylife.rest.model.Event;
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
		return getFollowedArtist(username);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response followArtistJSON(@PathParam("username") String username,
			@QueryParam("idartist") int idartist) {
		followArtist(username, idartist);
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
	public Response unFollowArtistJSON(@PathParam("username") String username,
			@QueryParam("idartist") int idartist) {
		unFollowArtist(username, idartist);
		return Response.status(204).build();
	}

	private List<Artist> getFollowedArtist(String username) {
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

	private void followArtist(String username, int idartist) {
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
				boolean exist = artistExist(idartist);
				if (exist == false) {
					throw new WebApplicationException(Response
							.status(Response.Status.CONFLICT)
							.entity(APIErrorBuilder.buildError(
									Response.Status.CONFLICT.getStatusCode(),
									"This artist doesn't exist", request))
							.build());
				}
				boolean foll = isFollowed(idartist, iduser);
				if (foll == true) {
					throw new WebApplicationException(
							Response.status(Response.Status.CONFLICT)
									.entity(APIErrorBuilder.buildError(
											Response.Status.CONFLICT
													.getStatusCode(),
											"This artist was already followed",
											request)).build());
				}
				Statement stmt = connection.createStatement();
				StringBuilder sb = new StringBuilder(
						"INSERT INTO  follow (iduser,idartist) values ("
								+ iduser + ", " + idartist + ");");
				System.out.println(sb.toString());
				int rc = stmt.executeUpdate(sb.toString());
				if (rc == 0) {
					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"Failed to follow this artist", request))
							.build());
				}
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

	private void unFollowArtist(String username, int idartist) {
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
				boolean exist = artistExist(idartist);
				if (exist == false) {
					throw new WebApplicationException(Response
							.status(Response.Status.CONFLICT)
							.entity(APIErrorBuilder.buildError(
									Response.Status.CONFLICT.getStatusCode(),
									"This artist doesn't exist", request))
							.build());
				}
				boolean foll = isFollowed(idartist, iduser);
				if (foll == false) {
					throw new WebApplicationException(Response
							.status(Response.Status.CONFLICT)
							.entity(APIErrorBuilder.buildError(
									Response.Status.CONFLICT.getStatusCode(),
									"You need to follow this artist first",
									request)).build());
				}
				Statement stmt = connection.createStatement();
				if (hasEvents(iduser, idartist) == true) {
					List<Event> idEventAssist = getEventAssistList(iduser,
							idartist);
					StringBuilder sb = new StringBuilder(
							"DELETE from follow where idartist=" + idartist
									+ " and iduser =" + iduser + ";");
					System.out.println(sb.toString());
					int rc = stmt.executeUpdate(sb.toString());
					if (rc == 0) {
						throw new WebApplicationException(Response
								.status(Response.Status.NOT_FOUND)
								.entity(APIErrorBuilder.buildError(
										Response.Status.NOT_FOUND
												.getStatusCode(),
										"Failled to unfollow this artist",
										request)).build());
					}
					for (Event E : idEventAssist) {
						StringBuilder sb2 = new StringBuilder(
								"DELETE FROM assist WHERE idevent="
										+ E.getEventId() + " ");
						sb2.append("AND iduser=" + iduser + ";");
						System.out.println("DELETE assist: " + sb2);
						int rc2 = stmt.executeUpdate(sb2.toString());
						if (rc2 == 0) {
							StringBuilder sb3 = new StringBuilder(
									"INSERT INTO follow VALUES (NULL, "
											+ iduser + ", " + idartist + ");");
							System.out.println(sb3.toString());
							int rc3 = stmt.executeUpdate(sb3.toString());
							if (rc3 == 0) {
								throw new WebApplicationException(Response
										.status(Response.Status.NOT_FOUND)
										.entity(APIErrorBuilder.buildError(
												Response.Status.NOT_FOUND
														.getStatusCode(),
												"Failled to follow artist previously unfollowed "
														+ E.getEventId(),
												request)).build());
							}
							throw new WebApplicationException(Response
									.status(Response.Status.NOT_FOUND)
									.entity(APIErrorBuilder.buildError(
											Response.Status.NOT_FOUND
													.getStatusCode(),
											"Failled to unmark event "
													+ E.getEventId(), request))
									.build());
						}
					}
				} else {
					StringBuilder sb = new StringBuilder(
							"DELETE from follow where idartist=" + idartist
									+ " and iduser =" + iduser + ";");
					System.out.println(sb.toString());
					int rc = stmt.executeUpdate(sb.toString());
					if (rc == 0) {
						throw new WebApplicationException(Response
								.status(Response.Status.NOT_FOUND)
								.entity(APIErrorBuilder.buildError(
										Response.Status.NOT_FOUND
												.getStatusCode(),
										"Failled to unfollow this artist",
										request)).build());
					}
				}
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

	public boolean artistExist(int idartist) {
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
			// SELECT * FROM artist WHERE id=1;
			StringBuilder sb = new StringBuilder(
					"SELECT * FROM artist WHERE id=" + idartist + ";");
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

	public List<Artist> getArtistFollowed(int iduser) {
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
			sb.append("follow.iduser=" + iduser + " order by name;");
			System.out.println(sb);
			ResultSet rs = stmt.executeQuery(sb.toString());
			List<Artist> artistList = new ArrayList<>();
			int i = 0;
			while (rs.next()) {
				Artist artist = new Artist();
				artist.setArtistid(rs.getInt("id"));
				artist.setName(rs.getString("name"));
				System.out.println(artist.getName());
				artist.setGenreId(rs.getInt("idgenre1"));
				artist.setGenre2Id(rs.getInt("idgenre2"));
				artist.setInfo(rs.getString("info"));
				String genre1 = obtainGenre(artist.getGenreId());
				artist.setGenre(genre1);
				if (artist.getGenre2Id() != 0) {
					String genre2 = obtainGenre(artist.getGenre2Id());
					artist.setGenre2(genre2);
				}
				boolean foll = isFollowed(artist.getArtistId(), iduser);
				artist.setFollowed(foll);
				artistList.add(artist);
				i++;
			}
			if (i == 0) {
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"Artist not found.", request)).build());
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

	public boolean hasEvents(int iduser, int idartist) {
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
			StringBuilder sb2 = new StringBuilder(
					"SELECT a.idevent FROM assist a INNER JOIN event e ");
			sb2.append("INNER JOIN follow f ON e.id=a.idevent ");
			sb2.append("AND a.iduser=f.iduser WHERE f.iduser=");
			sb2.append(iduser + " AND e.artist=(SELECT name FROM artist a ");
			sb2.append("WHERE a.id=" + idartist + ");");
			System.out.println("HasEvents?: " + sb2);
			ResultSet rs = stmt.executeQuery(sb2.toString());
			if (!rs.next()) {
				return false;
			}
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

	public List<Event> getEventAssistList(int iduser, int idartist) {
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
			// SELECT e.id FROM event e INNER JOIN assist a ON e.id=a.idevent
			// AND e.artist=(SELECT name FROM artist a WHERE a.id=5) AND
			// a.iduser=1;
			StringBuilder sb2 = new StringBuilder(
					"SELECT e.id FROM event e INNER JOIN assist a ON e.id=a.idevent ");
			sb2.append("AND e.artist=(SELECT name FROM artist a WHERE a.id=");
			sb2.append(idartist + ") AND a.iduser=" + iduser + ";");
			// StringBuilder sb2 = new StringBuilder(
			// "SELECT a.idevent FROM assist a INNER JOIN event e ");
			// sb2.append("INNER JOIN follow f ON e.id=a.idevent ");
			// sb2.append("AND a.iduser=f.iduser WHERE f.iduser=");
			// sb2.append(iduser + " AND e.artist=(SELECT name FROM artist a ");
			// sb2.append("WHERE a.id=" + idartist + ");");			
			System.out.println("id Event assist: " + sb2);
			ResultSet rs = stmt.executeQuery(sb2.toString());
			List<Event> eventAssistList = new ArrayList<>();
			while (rs.next()) {
				Event event = new Event();
				event.setEventId(rs.getInt("id"));
				System.out.println("Eventid Assist: " + event.getEventId());
				eventAssistList.add(event);
			}
			if (eventAssistList.size() == 0)
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"No Event Assist List found.", request))
						.build());
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
}