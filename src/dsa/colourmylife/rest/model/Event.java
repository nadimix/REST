package dsa.colourmylife.rest.model;

import org.codehaus.jackson.map.annotate.JsonSerialize;

//No serializa el objeto si tiene un NULL
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Event {

	private int eventId;
	private int kindId;
	private String artist;
	private String date;
	private String place;
	private String city;
	private String country;
	private String info;
	private String insertdate;

	// Hacer métodos que permitan obtener kind y artist a partir de sus Id aquí
	// dentro?
	private String kind;
	private String link;
	private String sameCountryLink;
	private String sameKindLink;

	// POJO
	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
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

	public int getKindId() {
		return kindId;
	}

	public void setKindId(int kindId) {
		this.kindId = kindId;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getSameKindLink() {
		return sameKindLink;
	}

	public void setSameKindLink(String sameKindLink) {
		this.sameKindLink = sameKindLink;
	}

	public String getInsertdate() {
		return insertdate;
	}

	public void setInsertdate(String insertdate) {
		this.insertdate = insertdate;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getSameCountryLink() {
		return sameCountryLink;
	}

	public void setSameCountryLink(String sameCountryLink) {
		this.sameCountryLink = sameCountryLink;
	}

}
