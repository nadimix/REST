package dsa.colourmylife.rest.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

//No serializa el objeto si tiene un NULL
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Event {

	private int eventId;
	private int artistId;
	private int userId;
	private int kindId;
	private String kind;
	private String artist;
	private String place;
	private String date;
	private String sameArtistLink;
	private String samePlaceLink;

	// POJO
	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}

	public int getArtistId() {
		return artistId;
	}

	public void setArtistId(int artistId) {
		this.artistId = artistId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSameArtistLink() {
		return sameArtistLink;
	}

	public void setSameArtistLink(String sameArtistLink) {
		this.sameArtistLink = sameArtistLink;
	}

	public String getSamePlaceLink() {
		return samePlaceLink;
	}

	public void setSamePlaceLink(String samePlaceLink) {
		this.samePlaceLink = samePlaceLink;
	}

	public int getKindId() {
		return kindId;
	}

	public void setKindId(int kindId) {
		this.kindId = kindId;
	}

}
