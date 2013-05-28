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
import javax.ws.rs.core.UriInfo;

import dsa.musicalevents.rest.model.Artist;
import dsa.musicalevents.rest.model.Event;
import dsa.musicalevents.rest.util.APIErrorBuilder;
import dsa.musicalevents.rest.util.DataSourceSAP;

@Path("/artists/{artist}/events/{eventId}")
public class ArtistEventResource {
	// Recurso Event: ./artists/{artist}/events/{eventId}
	// GET → Obtener evento.
	// PUT → Actualizar evento.
	// DELETE → Eliminar evento.
	//	id int(11) NOT NULL AUTO_INCREMENT,
	//	idkind int(11) NOT NULL,
	//	idartist int(11) NOT NULL,
	//	date datetime default NULL,
	//	place varchar(128) NOT NULL,
	//	city varchar(50) NOT NULL,

	@Context
	private UriInfo uri;

	@Context
	protected HttpServletRequest request;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Event getEventJSON(@PathParam("eventId") int eventId)
	{
		// TODO hacer método getEvent
		return getEvent(eventId);
		
	}
}
