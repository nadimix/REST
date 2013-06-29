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

import dsa.colourmylife.rest.model.User;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/users/{username}")
public class UserResource {
	@Context
	private HttpServletRequest request;
	@Context
	private SecurityContext security;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserJSON(@PathParam("username") String username,
			@QueryParam("password") String password,
			@QueryParam("option") int option) {
		if (option == 1) {
			return getUser(username);
		} 
		if (password != null){			
			return login(username, password);
		}
		else {
			throw new WebApplicationException(Response
					.status(Response.Status.FORBIDDEN)
					.entity(APIErrorBuilder.buildError(
							Response.Status.FORBIDDEN.getStatusCode(),
							"FORBIDDEN you must enter option or password", request)).build());
		}
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUserJSON(@PathParam("username") String username) {
		deleteUser(username);
		return Response.status(204).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateUserJSON(@PathParam("username") String username,
			User user) {
		updateUser(username, user);
		Response response = null;
		try {
			response = Response.status(204)
					.location(new URI("/users/" + username)).build();

		} catch (URISyntaxException e) {
			e.printStackTrace();

		}
		return response;
	}

	private void updateUser(String username, User user) {
		if (security.isUserInRole("registered")) {
			if (security.isUserInRole("registered")
					&& !security.getUserPrincipal().getName().equals(username)) {
				throw new WebApplicationException(Response
						.status(Response.Status.FORBIDDEN)
						.entity(APIErrorBuilder.buildError(
								Response.Status.FORBIDDEN.getStatusCode(),
								"FORBIDDEN", request)).build());
			}
			if (!userExists(username)) {
				throw new WebApplicationException(Response
						.status(Response.Status.CONFLICT)
						.entity(APIErrorBuilder.buildError(
								Response.Status.CONFLICT.getStatusCode(),
								"username don't exist", request)).build());
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
				if (user.getName() != null && user.getEmail() != null
						&& user.getPassword() != null) {
					// UPDATE user SET password=MD5('test2'),
					// email='otro2@mail.com', name='roger4' WHERE
					// username='Rog6';
					StringBuilder sb = new StringBuilder("UPDATE user SET ");
					sb.append("password=MD5('" + user.getPassword() + "'), ");
					sb.append("email='" + user.getEmail() + "', ");
					sb.append("name='" + user.getName() + "' ");
					sb.append("WHERE username='" + username + "';");
					System.out.println(sb.toString());

					Statement stmt = connection.createStatement();
					int rc = stmt.executeUpdate(sb.toString());
					if (rc == 0)
						throw new WebApplicationException(Response
								.status(Response.Status.NOT_FOUND)
								.entity(APIErrorBuilder.buildError(
										Response.Status.NOT_FOUND
												.getStatusCode(),
										"User not found.", request)).build());
				}
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

	public boolean userExists(String username) {
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
			ResultSet rs = stmt
					.executeQuery("select * from user where username = '"
							+ username + "'");
			return rs.next();
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());
		}
	}

	private void deleteUser(String username) {
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
				int rc = stmt
						.executeUpdate("DELETE from user where username = '"
								+ username + "'");
				if (rc == 0)
					throw new WebApplicationException(Response
							.status(Response.Status.NOT_FOUND)
							.entity(APIErrorBuilder.buildError(
									Response.Status.NOT_FOUND.getStatusCode(),
									"User not found.", request)).build());
				Statement stmt1 = connection.createStatement();
				stmt1.executeUpdate("DELETE from user_roles where username = '"
						+ username + "'");

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

	private User getUser(String username) {
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
			ResultSet rs = stmt
					.executeQuery("select * from user where username = '"
							+ username + "'");
			if (!rs.next())
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"User not found.", request)).build());

			User user = new User();
			user.setUserid(rs.getInt("id"));
			user.setUsername(rs.getString("username"));
			user.setPassword(rs.getString("password"));
			user.setEmail(rs.getString("email"));
			user.setName(rs.getString("name"));
			stmt.close();
			connection.close();
			return user;
		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(),
							"Error accessing to database.", request)).build());

		}
	}

	private User login(String username, String password) {
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
			ResultSet rs = stmt
					.executeQuery("select * from user where username = '"
							+ username + "'");
			if (rs.next()) {
				User user = new User();
				user.setUserid(rs.getInt("id"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setEmail(rs.getString("email"));
				user.setName(rs.getString("name"));
				Statement stmt1 = connection.createStatement();
				StringBuilder sb = new StringBuilder(
						"select username from user where username='"
								+ user.getUsername() + "' and password =MD5('"
								+ password + "')");
				ResultSet rs1 = stmt1.executeQuery(sb.toString());
				if (!rs1.next()) {
					throw new WebApplicationException(Response
							.status(Response.Status.CONFLICT)
							.entity(APIErrorBuilder.buildError(
									Response.Status.CONFLICT.getStatusCode(),
									"Password or username not correct.",
									request)).build());
				}
				String username1 = rs1.getString("username");
				if (!username.equals(username1)) {
					throw new WebApplicationException(Response
							.status(Response.Status.CONFLICT)
							.entity(APIErrorBuilder.buildError(
									Response.Status.CONFLICT.getStatusCode(),
									"Password or username not correct.",
									request)).build());
				}
				if (username.equals("admin")) {
					throw new WebApplicationException(Response
							.status(Response.Status.CREATED)
							.entity(APIErrorBuilder.buildError(
									Response.Status.CREATED.getStatusCode(),
									"user login as admin.", request)).build());

				}
				Statement stmt4 = connection.createStatement();
				ResultSet rs3 = stmt4
						.executeQuery("select id from user where username = '"
								+ username + "'");
				rs3.next();
				int iduser = rs3.getInt("id");
				Statement stmt2 = connection.createStatement();
				ResultSet rs2 = stmt2
						.executeQuery("select idartist from follow where iduser= "
								+ iduser + "");
				if (rs2.next()) {
					throw new WebApplicationException(Response
							.status(Response.Status.OK)
							.entity(APIErrorBuilder.buildError(
									Response.Status.OK.getStatusCode(),
									"The user is following artists.", request))
							.build());
				}
				if (!rs2.next()) {

					throw new WebApplicationException(Response
							.status(Response.Status.ACCEPTED)
							.entity(APIErrorBuilder.buildError(
									Response.Status.ACCEPTED.getStatusCode(),
									"The user is not following any artist.",
									request)).build());
				}
				stmt.close();
				stmt1.close();
				stmt2.close();
				stmt4.close();
				return user;
			} else {
				throw new WebApplicationException(Response
						.status(Response.Status.NOT_FOUND)
						.entity(APIErrorBuilder.buildError(
								Response.Status.NOT_FOUND.getStatusCode(),
								"User not found.", request)).build());
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