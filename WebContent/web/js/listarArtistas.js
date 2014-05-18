$(document).ready(
		function() {
			getArtists($.cookie('username'), $.cookie('password'),
					getArtistsSuccess, getArtistsError);
		});

function getArtistsSuccess(data, status, jqXHR) {
	var response = $.parseJSON(jqXHR.responseText);
	$('#resArtista > tbody').empty();
	$.each(response, function(index, value) {
		var artistname = encodeURIComponent(value.name);
		if (value.genre2 == null) {
			value.genre2 = '';
		}
		$('#resArtista > tbody').append(
				'<tr><td> <a href=artista.html?artist=' + artistname + '>'
						+ value.name + '</a></td><td>' + value.genre
						+ '<a>, </a>' + value.genre2 + '</td><td>' + value.info
						+ '</td><td><a class="btn btn-inverse" onclick="deleteArtista2(\''
						+ value.name
						+ '\')"><i class="icon-trash icon-white"></i></a></tr>');
	});
}

function getArtistsError(jqXHR, options, error) {
	bootbox.alert("Error al listar los artistas");
}

function deleteArtista2(value) {
	//alert(value);
	var username = $.cookie('username');
	var password = $.cookie('password');
	var artistname2 = value;
	//alert(artistname2);

	deleteArtist(artistname2, username, password, deleteArtistSuccess,
			deleteArtistError);
}

// ELIMINAR ARTISTA
$('#eliminar-Artista').submit(
		function(e) {
			e.preventDefault();
			var artistname1 = $('#nombre-Artista1').val();
			var username = $.cookie('username');
			var password = $.cookie('password');

			deleteArtist(artistname1, username, password, deleteArtistSuccess,
					deleteArtistError);
		});

function deleteArtistSuccess(data, status, jqxhr) {
	if (jqxhr.status == 204) {
		
		getArtists($.cookie('username'), $.cookie('password'),
				getArtistsSuccess, getArtistsError);
		bootbox.alert("Artista eliminado correctamente!");
	}
}

function deleteArtistError(jqXHR, options, error) {

	if (jqXHR.status == 403) {
		bootbox.alert("No esas autorizado para eliminar el artista.");
	}
	if (jqXHR.status == 404) {
		bootbox.alert("Este Artista no existe.");
	}
}

// AÑADIR ARTISTA
$('#añadirArtista').submit(
		function(e) {
			e.preventDefault();
			var sel = document.getElementById("genero");
			var generos = [];
			for ( var i = 0; i < sel.options.length; ++i) {
				if (sel.options[i].selected) {
					generos.push(sel.options[i].value);
				}
			}
			var artistname = $('#nombre-Artista').val();
			var infoartist = $('#textarea').val();
			var artist = new Object();
			artist.name = artistname;
			if (generos[0] != null) {
				artist.genreId = generos[0].toString();
				if (generos[1] != null)
					artist.genre2Id = generos[1].toString();
				artist.info = infoartist;
				insertArtist(JSON.stringify(artist), $.cookie('username'), $
						.cookie('password'), insertArtistSuccess,
						insertArtistError);
			}
		});

function insertArtistSuccess(data, status, jqxhr) {
	if (jqxhr.status == 204) {
		bootbox.alert("Artista añadido correctamente.");
		getArtists($.cookie('username'), $.cookie('password'),
				getArtistsSuccess, getArtistsError);
	}
}

function insertArtistError(jqXHR, options, error) {

	if (jqXHR.status == 403) {
		bootbox.alert("No esas autorizado para añadir un artista.");
	}

	if (jqXHR.status == 400) {
		bootbox
				.alert("El campo nombre de artista y genero no pueden estar vacios.");
	}

	if (jqXHR.status == 409) {
		bootbox.alert("El artista que quieres añadir ya existe.");
	}

	if (jqXHR.status == 404) {
		bootbox.alert("Error al añadir el artista.");
	}
}

$('select')
		.change(
				function(e) {
					var sel = document.getElementById("genero");
					var maxOptions = 2;
					var optionCount = 0;
					for ( var i = 0; i < sel.options.length; ++i) {
						if (sel.options[i].selected) {
							optionCount++;
							if (optionCount > maxOptions) {
								bootbox
										.alert("Solo se mandaran los 2 primeros, por favor deselecciona alguno.");
								return false;
							}
						}
					}
					return true;
				});

//LOG OUT
$('#salirArtistas').click(function(e)  {
	e.preventDefault();
	//eliminamos toda la informaci�n de la cookie.
	 $.cookie('username', 'undefined', -1);
	$.cookie('password', 'undefined',-1);
	window.location.href = "login.html";
});