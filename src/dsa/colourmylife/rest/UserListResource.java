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

import dsa.colourmylife.rest.model.User;
import dsa.colourmylife.rest.util.APIErrorBuilder;
import dsa.colourmylife.rest.util.DataSourceSAP;

@Path("/users")
public class UserListResource {

	@Context
	protected HttpServletRequest request;
	
	@Context
	private SecurityContext security;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getUsersJSON() {
		return getUsers();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUserJSON(User user) {

		insertUser(user);
		Response response = null;
		try {
			response = Response.status(204)
					.location(new URI("/users/" + user.getName())).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
	}

	private void insertUser(User user) {
		
		// obtenemos la conexion
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

			// Bad request: si el usuario no ha puesto uno de los campos.

			if (user.getName() == null || user.getPassword() == null
					|| user.getEmail() == null || user.getUsername()==null) {
				throw new WebApplicationException(Response
						.status(Response.Status.BAD_REQUEST)
						.entity(APIErrorBuilder.buildError(
								Response.Status.BAD_REQUEST.getStatusCode(),
								"Bad request.", request)).build());

			}

			// si el nombre que el usuario para registrarse ya existe le
			// enviamos un error.
			if (userExists(user.getUsername())) {
				throw new WebApplicationException(Response
						.status(Response.Status.CONFLICT)
						.entity(APIErrorBuilder.buildError(
								Response.Status.CONFLICT.getStatusCode(),
								"username used by other user.", request))
						.build());

			}

			// CONSULTA: INSERT INTO user VALUES (NULL,"Rog5", MD5("test"),
			// "rog@rog.com", "Roger", 1);

			// Insertamos los datos al usuario de la tabla user.

			StringBuilder sb = new StringBuilder(
					"INSERT INTO user (username,password,email,name) VALUES ('"
							+ user.getUsername() + "',");
			sb.append("MD5('" + user.getPassword() + "'),'" + user.getEmail()
					+ "','" + user.getName() + "');");

			Statement stmt = connection.createStatement();
			int rc = stmt.executeUpdate(sb.toString());
			if (rc == 0)
				throw new WebApplicationException(
						Response.status(Response.Status.CONFLICT)
								.entity(APIErrorBuilder.buildError(
										Response.Status.CONFLICT
												.getStatusCode(),
										"Error inserting values to database.",
										request)).build());

			// insertamos los datos a la tabla user-role

			/*
			 * "INSERT INTO user-roles(userid,roleid,username,user-role) VALUES
			 * (LAST-INSERT-ID(),1,1,'username','registered
			 */

			StringBuilder sb1 = new StringBuilder(
					"INSERT INTO user_roles VALUES (LAST_INSERT_ID(),1,'"
							+ user.getUsername() + "','registered');");

			Statement stmt1 = connection.createStatement();
			int rc1 = stmt1.executeUpdate(sb1.toString());

			if (rc1 == 0)
				throw new WebApplicationException(
						Response.status(Response.Status.CONFLICT)
								.entity(APIErrorBuilder.buildError(
										Response.Status.CONFLICT
												.getStatusCode(),
										"Error inserting values to database.",
										request)).build());

			connection.setAutoCommit(false);

		} catch (SQLException e) {
			throw new WebApplicationException(Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(APIErrorBuilder.buildError(
							Response.Status.INTERNAL_SERVER_ERROR
									.getStatusCode(), "Internal server error.",
							request)).build());
		} finally {
			try {
				connection.setAutoCommit(true);
				connection.close();
			} catch (SQLException e) {
			}

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
			ResultSet rs = stmt // terminar query
					.executeQuery("select * from user where username = '"
							+ username + "';");

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

	private List<User> getUsers() {
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
			ResultSet rs = stmt.executeQuery("select * from user;");
			List<User> users = new ArrayList<>();
			while (rs.next()) {
				User user = new User();
				user.setUserid(rs.getInt("id"));
				user.setName(rs.getString("name"));
				user.setUsername(rs.getString("username"));
				user.setPassword(rs.getString("password"));
				user.setEmail(rs.getString("email"));
				users.add(user);
			}

			stmt.close();
			connection.close();
			return users;

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
