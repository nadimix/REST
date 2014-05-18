$(document).ready(function() {
	map = new GMaps({
		div : '#pony',
		lat : -12.043333,
		lng : -77.028333
	});
});
// ELIMINAR EVENTO:
$('#eliminar-Evento').submit(
		function(e) {
			e.preventDefault();

			var artistname2 = $('#nombreartista').val();
			var idevent = $('#idevento').val();
			var username = $.cookie('username');
			var password = $.cookie('password');

//			alert(idevent);
//			alert(artistname2);
			deleteEvent(artistname2, idevent, username, password,
					deleteEventSuccess, deleteEventError);
		});

function deleteEventSuccess(data, status, jqxhr) {
	if (jqxhr.status == 204) {
		bootbox.alert("Evento eliminado correctamente.");
	}
}

function deleteEventError(jqXHR, options, error) {
	if (jqXHR.status == 403) {
		bootbox.alert("No esas autorizado para eliminar.");
	}

	if (jqXHR.status == 404) {
		bootbox
				.alert("Este Evento no existe, comprueba que el evento es del artista correspondiente.");
	}
}

// MODIFICAR EVENTO:
$('#modificar-Evento').submit(
		function(e) {
			e.preventDefault();

//			alert("submit");
			var date = $('#data2').val();
//			alert(date.toString());
			var date2 = date.substr(6, 4) + '-' + date.substr(3, 2) + '-'
					+ date.substr(0, 2) + ' ' + date.substr(11, 2) + ':'
					+ date.substr(14, 2);
//			alert(date2);
			var username = $.cookie('username');
			var password = $.cookie('password');
			var id = $('#idevento2').val();
			var artistname3 = $('#nombreartista1').val();
			var place = $('#lugarEvento').val();
			var city = $('#ciudadEvento2').val();
			var country = $('#paisEvento').val();
			var info = $('#infoEvento').val();

			var event1 = new Object();
			event1.date = date2;
			event1.place = place;
			event1.info = info;
			event1.city = city;
			event1.country = country;

//			alert("ID:" + id);
//			alert("Ciudad:" + city);
//			alert("Artista:" + artistname3);

			updateEvent(JSON.stringify(event1), artistname3, id, username,
					password, updateEventSuccess, updateEventError);
		});

function localizar() {
//	alert("hola");
	GMaps.geocode({
		address : $('#lugarEvento').val() + ", " + $('#ciudadEvento2').val()
				+ ", " + $('#paisEvento').val(),
		callback : function(results, status) {
			if (status == 'OK') {
				var latlng = results[0].geometry.location;
				map.setCenter(latlng.lat(), latlng.lng());
				map.addMarker({
					lat : latlng.lat(),
					lng : latlng.lng(),
					click : function(e) {
						alert('Lugar del evento');
					}
				});
			}
		}
	});
}

function localizar2() {
	GMaps.geocode({
		address : $('#lugar-Evento1').val() + ", " + $('#ciudad-Evento').val()
				+ ", " + $('#pais-Evento').val(),
		callback : function(results, status) {
			if (status == 'OK') {
				var latlng = results[0].geometry.location;
				map.setCenter(latlng.lat(), latlng.lng());
				map.addMarker({
					lat : latlng.lat(),
					lng : latlng.lng(),
					click : function(e) {
						alert('Lugar del evento');
					}
				});
			}
		}
	});
}

function updateEventSuccess(data, status, jqxhr) {
	bootbox.alert("Evento modificado correctamente. ");
}

function updateEventError(jqXHR, options, error) {

	if (jqXHR.status == 403) {
		bootbox.alert("No esas autorizado para modificar el evento.");
	}

	if (jqXHR.status == 404) {
		bootbox.alert("El evento que quieres modificar no existe.");

	}

	if (jqXHR.status == 400) {
		bootbox.alert("El campo de la ciudad no puede estar en blanco.");

	}
}

// AÑADIR EVENTO:
$('#insert-Evento').submit(
		function(e) {
			e.preventDefault();
//			alert("entra en añadir evento");

			var idkind = document.getElementById("multiSelect").value;
			var artistname3 = $('#nombre-Artista1').val();
			var date = $('#data').val();
//			alert(date.toString());
			var date2 = date.substr(6, 4) + '-' + date.substr(3, 2) + '-'
					+ date.substr(0, 2) + ' ' + date.substr(11, 2) + ':'
					+ date.substr(14, 2);
//			alert(date2);
			var place = $('#lugar-Evento1').val();
			var city = $('#ciudad-Evento').val();
			var country = $('#pais-Evento').val();
			var info = $('#info-Evento').val();

			var event = new Object();
			event.kindId = idkind;
			event.date = date2;
			event.artist = artistname3;
			event.place = place;
			event.info = info;
			event.city = city;
			event.country = country;
//			alert(event);

			insertEvent(JSON.stringify(event), artistname3, insertEventSuccess,
					insertEventError);

		});

function insertEventSuccess(data, status, jqxhr) {
	bootbox.alert("Evento creado correctamente.");

}

function insertEventError(jqXHR, options, error) {

	if (jqXHR.status == 403) {
		bootbox.alert("No esas autorizado para añadir el evento.");
	}
	if (jqXHR.status == 404) {
		bootbox.alert("Error al añadir el evento.");
	}
}

//LOG OUT
$('#salirEventos').click(function(e)  {
	e.preventDefault();
	//eliminamos toda la informaci�n de la cookie.
	 $.cookie('username', 'undefined', -1);
	$.cookie('password', 'undefined',-1);
	window.location.href = "login.html";
});