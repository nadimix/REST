
$(document).ready(function() {
	var username=$.cookie('username');
	var password=$.cookie('password');
	getEventsFav( username,password,getEventsFavSuccess,getEventsFavError);
});
function getEventsFavSuccess(data, status, jqXHR){
	
	//console.log(data);
	var response = $.parseJSON(jqXHR.responseText);
	$('#res-Eventos-fav > tbody').empty();
	$.each(response, function(index, value) {

		var artistname = encodeURIComponent(value.artist);// codificamos el nombre del artista
		if(value.city==null)
		{
			value.city='';
		}
		$('#res-Eventos-fav > tbody').append(
				'<tr><td><a href="' + value.link + '" data-toggle="modal">'
						+ value.kind + '</td><td><a href=artista.html?artist='
						+ artistname + '>' + value.artist + '</td><td>'
						+ value.city + '</a></td><td>' + value.country
						+ '</a></td><td>' + value.date
						+'</td></tr>');

	});
	
	$('[data-toggle="modal"]').click(function(e) {
		e.preventDefault();
		var username = $.cookie('username');
		var url = $(this).attr('href')+'?user='+username;
		
		$.ajax({
			url : url,
			type : 'GET',
			headers : {
				"Accept" : "application/json"
			},
			crossDomain : true,

			success : function(data, status, jqxhr) {
				
				$.cookie('idevent', data.eventId);//creamos la cookie con el id del evento
				
				//comprobar que ese evento este seguido no para poner los botones.
				
				$('#unfav-event1').hide();
				
				if(data.fav==true){// si no ha marcado el evento como favorito.
					$('#unfav-event1').show();
				}else
					{
						bootbox.alert("No has marcado este evento como favorito.");
					}
				
				$('#tdartist1').text(data.artist);
				$('#tdplace1').text(data.place);
				$('#tddate1').text(data.date);
				$('#tdinfo1').text(data.info);
				$('#tdcity1').text(data.city);
				$('#myModal').modal();
				
				
						
			},
			error : function(jqXHR, options, error) {
			}
		});

	});
	
}

function getEventsFavError(jqXHR, options, error){
	
	if (jqXHR.status == 404) {
		bootbox.alert("No tienes ningun evento favorito.");
	}		
	
	if (jqXHR.status == 403) {
		bootbox.alert("No tienes permiso.");
	}
}

//DESMARCAR EVENTO COMO FAVORITO
$('#unfav-event1').submit(function(e)  {
	e.preventDefault();

	var idevent=$.cookie('idevent');
	var username = $.cookie('username');
	var password= $.cookie('password');
	unfavEvent(username,idevent,password, unfavEventSuccess, unfavEventError);

});

function unfavEventSuccess(data, status, jqXHR){
	if (jqXHR.status == 204) {
		bootbox.alert("Has desmarcado el evento como favorito.");
	}
	
	var username = $.cookie('username');
	var password= $.cookie('password');
	
	getEventsFav(username,password,getEventsFavSuccess,getEventsFavError);
		
}

function unfavEventError(jqXHR, options, error) {
	
	if (jqXHR.status == 404) {
		bootbox.alert("No has marcado este evento como favorito.");
	}
	
	if (jqXHR.status == 403) {
		bootbox.alert("No estas autorizado.");
	}
	
}

//LOG OUT
$('#salir4').click(function(e)  {
	e.preventDefault();
	//eliminamos toda la información de la cookie.
	
	$.cookie('username', 'undefined',-1);
	$.cookie('password', 'undefined',-1);
	window.location.href = "login.html";
});
