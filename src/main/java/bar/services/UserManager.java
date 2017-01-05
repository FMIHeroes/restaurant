package bar.services;

import java.net.HttpURLConnection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import bar.dao.UserDAO;
import bar.model.Role;
import bar.model.User;

@Stateless
@Path("user")
public class UserManager {

	private static final Response RESPONSE_OK = Response.ok().build();
	private static final Response RESPONSE_UNAUTHORIZED = Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).build();
	private static final Response RESPONSE_BAD_REQUEST = Response.status(HttpURLConnection.HTTP_BAD_REQUEST).build();
	private static final Response RESPONSE_CONFLICT = Response.status(HttpURLConnection.HTTP_CONFLICT).build();
	private static final Response RESPONSE_NOT_FOUND = Response.status(HttpURLConnection.HTTP_NOT_FOUND).build();
	
	@Inject
	private UserDAO userDAO;

	@Inject
	private UserContext context;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(User newUser) {
		if(!context.isUserInRole(Role.Manager)){
			return RESPONSE_UNAUTHORIZED;
		}
		boolean userNameExists = userDAO.userNameExists(newUser.getUserName());
		boolean emailExists = userDAO.emailExists(newUser.getEmail());

		if (userNameExists) {
			return RESPONSE_BAD_REQUEST;
		}
		if (emailExists) {
			return RESPONSE_CONFLICT;
		}

		userDAO.addUser(newUser);
		return RESPONSE_OK;
	}

	@Path("login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginUser(User user) {
		User validUser = userDAO.validateUserCredentials(user.getUserName(), user.getPassword());
		if (validUser == null) {
			return RESPONSE_UNAUTHORIZED;
		}
		context.setCurrentUser(validUser);
		return RESPONSE_OK;
	}

	@Path("authenticated")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	public Response isAuthenticated() {
		if (context.getCurrentUser() == null) {
			return RESPONSE_NOT_FOUND;
		}
		return RESPONSE_OK;
	}

	@Path("current")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	public String getUser() {
		if (context.getCurrentUser() == null) {
			return null;
		}
		return context.getCurrentUser().getUserName();
	}

	@Path("logout")
	@GET
	@Consumes(MediaType.TEXT_PLAIN)
	public void logoutUser() {
		System.out.println("user logging out");
		context.setCurrentUser(null);
	}

}
