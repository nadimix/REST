package dsa.musicalevents.rest;

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
import javax.ws.rs.POST;
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

@Path("/artists/{artist}/events/{eventid}")
public class ArtistEventResource {
	// Recurso Event: ./artists/{artist}/events/{eventid}
	// GET → Obtener evento. *
	// PUT → Actualizar evento.
	// DELETE → Eliminar evento.
	// id int(11) NOT NULL AUTO_INCREMENT,
	// idkind int(11) NOT NULL,
	// idartist int(11) NOT NULL,
	// date datetime default NULL,
	// place varchar(128) NOT NULL,
	// city varchar(50) NOT NULL,

	@Context
	private UriInfo uri;

	@Context
	protected HttpServletRequest request;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Event getEventJSON(@PathParam("eventid") int eventid,
			@PathParam("artist") String artistname) {
		// TODO hacer método getEvent
		return getEvent(eventid, artistname);
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateEventJson(@PathParam("eventid") int eventid,
			Event event, @PathParam("artist") String artistname) {
		// TODO método updateEvent
		updateEvent(eventid, event, artistname);
		// Le paso un evento y un artista (string) que al introducirlo en un
		// atributo del evento luego puedo obtenerlo invocándolo a partir del
		// event.getArtist
		Response response = null;
		try {
			response = Response
					.status(204)
					.location(
							new URI("/artists/" + event.getArtist() + "/events/"
									+ event.getEventId())).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return response;
	}
	
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response createEventJSON(Event event, String artistname) {
	// // TODO Hacer método insertEvent
	// insertEvent(event, artistname);
	// // Le paso un evento y un artista (string) que al introducirlo en un
	// // atributo del evento luego puedo obtenerlo invocándolo a partir del
	// // event.getArtist
	// Response response = null;
	// try {
	// response = Response
	// .status(204)
	// .location(
	// new URI("/artists/" + event.getArtist()
	// + "/events/" + event.getEventId())).build();
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// }
	// return response;
	// }
}
