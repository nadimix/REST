package dsa.colourmylife.rest.model;

public class Artist {

	private int artistId;
	private String name;
	private int genreId;
	private int genre2Id;
	private String info;
	private String genre;
	private String genre2;

	public int getArtistId() {
		return artistId;
	}

	public void setArtistid(int artistId) {
		this.artistId = artistId;
	}

	public int getGenreId() {
		return genreId;
	}

	public void setGenreId(int genreId) {
		this.genreId = genreId;
	}

	public int getGenre2Id() {
		return genre2Id;
	}

	public void setGenre2Id(int genre2Id) {
		this.genre2Id = genre2Id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getGenre2() {
		return genre2;
	}

	public void setGenre2(String genre2) {
		this.genre2 = genre2;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
}
