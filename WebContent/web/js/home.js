$(document).ready(
		function() {

			var username = $.cookie('username');
			var password = $.cookie('password');

			getEventsArtists(username, password, getEventsArtistsSuccess,
					getEventsArtistsError);

			map = new GMaps({
				div : '#pony',
				lat : -12.043333,
				lng : -77.028333
			});
		});
function getEventsArtistsSuccess(data, status, jqXHR) {
	console.log(data);
	var response = $.parseJSON(jqXHR.responseText);
	$('#tresultados > tbody').empty();

	$.each(response, function(index, value) {

		var artistname = encodeURIComponent(value.artist);// codificamos el
															// nombre del
															// artista

		if (value.city == null) {
			value.city = '';
		}

		$('#tresultados > tbody').append(
				'<tr><td><a href="' + value.link + '" data-toggle="modal">'
						+ value.kind + '</td><td><a href=artista.html?artist='
						+ artistname + '>' + value.artist + '</td><td>'
						+ value.city + '</a></td><td>' + value.country
						+ '</a></td><td>' + value.date + '</td></tr>');
		
		GMaps.geocode({
			address : value.place + ", " + value.city
					+ ", " + value.country,
			callback : function(results, status) {
				 //alert(results.toString());
				if (status == 'OK') {
					var latlng = results[0].geometry.location;
					map.setCenter(latlng.lat(), latlng.lng());
					map.addMarker({
						lat : latlng.lat(),
						lng : latlng.lng(),
						click : function(e) {
							alert("Lugar del evento: " + value.place);
						}
					});
				}
			}
		});
	});



	$('[data-toggle="modal"]').click(function(e) {
		e.preventDefault();
		var username = $.cookie('username');
		var url = $(this).attr('href') + '?user=' + username;

		$.ajax({
			url : url,
			type : 'GET',
			headers : {
				"Accept" : "application/json"
			},
			crossDomain : true,

			success : function(data, status, jqxhr) {

				$.cookie('idevent', data.eventId);// creamos la cookie con el
													// id del evento

				// comprobar que ese evento este seguido no para poner los
				// botones.

				$('#unfav-event').hide();
				$('#fav-event').hide();

				if (data.fav == false) {// si no ha marcado el evento como
										// favorito.
					$('#fav-event').show();
				} else {
					$('#unfav-event').show();

				}

				$('#tdartist').text(data.artist);
				$('#tdplace').text(data.place);
				$('#tddate').text(data.date);
				$('#tdinfo').text(data.info);
				$('#tdcity').text(data.city);
				$('#myModal').modal();

			},
			error : function(jqXHR, options, error) {
			}
		});
	});
}

function getEventsArtistsError(jqXHR, options, error) {

	if (jqXHR.status == 403) {
		bootbox.alert("No estas autorizado.");
	}
	if (jqXHR.status == 404) {
		bootbox.alert("No hay eventos de los artistas a los que sigues.");
		$('#event-Kind').hide();
	}
}

// MARCAR EVENTO COMO FAVORITO
$('#fav-event').submit(function(e) {
	e.preventDefault();

	var idevent = $.cookie('idevent');
	var username = $.cookie('username');
	var password = $.cookie('password');

	favEvent(username, idevent, password, favEventSuccess, favEventError);

});

function favEventSuccess(data, status, jqXHR) {
	if (jqXHR.status == 204) {
		bootbox.alert("Has marcado el evento como favorito.");
	}
	$('#unfav-event').show();
	$('#fav-event').hide();
}

function favEventError(jqXHR, options, error) {

	if (jqXHR.status == 409) {
		bootbox.alert("Ya has marcado este evento como favorito.");
	}

	if (jqXHR.status == 403) {
		bootbox.alert("No estas autorizado.");
	}

}

// DESMARCAR EVENTO COMO FAVORITO
$('#unfav-event').submit(
		function(e) {
			e.preventDefault();

			var idevent = $.cookie('idevent');
			var username = $.cookie('username');
			var password = $.cookie('password');

			unfavEvent(username, idevent, password, unfavEventSuccess,
					unfavEventError);

		});

function unfavEventSuccess(data, status, jqXHR) {
	if (jqXHR.status == 204) {
		bootbox.alert("Has desmarcado el evento como favorito.");
	}

	$('#unfav-event').hide();
	$('#fav-event').show();
}

function unfavEventError(jqXHR, options, error) {

	if (jqXHR.status == 404) {
		bootbox.alert("No has marcado este evento como favorito.");
	}

	if (jqXHR.status == 403) {
		bootbox.alert("No estas autorizado.");
	}

}

// FILTRAR EVENTO POR KIND
$('#event-Kind').submit(
		function(e) {
			e.preventDefault();

			var username = $.cookie('username');
			var password = $.cookie('password');
			var kind = $('#tags').val();

			getEventsArtistsKind(username, password, kind,
					getEventsArtistsKindSuccess, getEventsArtistsKindError);

		});

function getEventsArtistsKindSuccess(data, status, jqXHR) {

	var response = $.parseJSON(jqXHR.responseText);
	$('#tresultados > tbody').empty();
	if (response == "")// si no hay ningun evento
	{
		bootbox.alert("No hay eventos.");
	}

	$.each(response, function(index, value) {

		var artistname = encodeURIComponent(value.artist);// codificamos el
															// nombre del
															// artista

		$('#tresultados > tbody').append(
				'<tr><td><a href="' + value.link + '" data-toggle="modal">'
						+ value.kind + '</td><td><a href=artista.html?artist='
						+ artistname + '>' + value.artist + '</td><td>'
						+ value.city + '</a></td><td>' + value.country
						+ '</a></td><td>' + value.date + '</td></tr>');

	});

	$('[data-toggle="modal"]').click(function(e) {
		e.preventDefault();
		var username = $.cookie('username');
		var url = $(this).attr('href') + '?user=' + username;

		$.ajax({
			url : url,
			type : 'GET',
			headers : {
				"Accept" : "application/json"
			},
			crossDomain : true,

			success : function(data, status, jqxhr) {

				$.cookie('idevent', data.eventId);// creamos la cookie con el
													// id del evento

				// comprobar que ese evento este seguido no para poner los
				// botones.

				$('#unfav-event').hide();
				$('#fav-event').hide();

				// alert(data.fav);
				if (data.fav == false) {// si no ha marcado el evento como
										// favorito.
					$('#fav-event').show();
				} else {
					$('#unfav-event').show();

				}

				$('#tdartist').text(data.artist);
				$('#tdplace').text(data.place);
				$('#tddate').text(data.date);
				$('#tdinfo').text(data.info);
				$('#tdcity').text(data.city);
				$('#myModal').modal();

			},
			error : function(jqXHR, options, error) {
			}
		});

	});

}

function getEventsArtistsKindError(jqXHR, options, error) {

	if (jqXHR.status == 403) {
		bootbox.alert("No estas autorizado.");
	}

	if (jqXHR.status == 404) {
		bootbox.alert("No hay ningun evento con ese tipo de evento.");
	}

}

// LOG OUT
$('#salir').click(function(e) {
	e.preventDefault();
	// eliminamos toda la información de la cookie.
	$.cookie('username', 'undefined', -1);
	$.cookie('password', 'undefined', -1);
	window.location.href = "login.html";
});

// function localizar() {
// // alert("hola");
// GMaps.geocode({
// address : $('#lugarEvento').val() + ", " + $('#ciudadEvento2').val()
// + ", " + $('#paisEvento').val(),
// callback : function(results, status) {
// // alert("callback");
// if (status == 'OK') {
// var latlng = results[0].geometry.location;
// map.setCenter(latlng.lat(), latlng.lng());
// map.addMarker({
// lat : latlng.lat(),
// lng : latlng.lng(),
// click : function(e) {
// alert('Lugar del evento');
// }
// });
// }
// }
// });
// }

