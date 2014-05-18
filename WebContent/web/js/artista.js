$(document).ready(function() {
	//cojes el parametro del nombre del artista
	
	var sURL = decodeURI(window.document.URL);///decodificamos la url
	//obtenemos el nombre de artista que tiene el urlparam de artista
	var param=sURL.split("?");
	var param1=param[1];
	var param2=param1.split("=");
	var artistname=param2[1];
	var username = $.cookie('username');
	var password= $.cookie('password');
	//no mostramos ninguno de los dos botones.
	$('#unfollow-Artist').hide();
	$('#follow-Artist').hide();
	
	getArtist(artistname,username,getArtistSuccess, getArtistError);
	getEventsArtist(artistname,username,password,getEventsArtistSuccess,getEventsArtistError);
	
});

function getArtistSuccess(data, status, jqXHR){
 	$('#infoArtista > tbody').empty();
 	
 	if(data.followed==false)
 	{
 		$('#unfollow-Artist').hide();
 		$('#follow-Artist').show();
 	}else
 	{
 		$('#unfollow-Artist').show();
 		$('#follow-Artist').hide();
 	}
 	
 	//prueba
	if(data.genre2==null)
	{
		data.genre2='';
	}
 	$('#infoArtista > tbody').append('<tr><td> '+data.name+'</td><td>'+ data.genre + '</td><td>' + data.genre2+ '</td><td>'+data.info+'</td></tr>');
 	$.cookie('idartist', data.artistId);
 
 	
}
function getArtistError(jqXHR, options, error){
	
	if(jqXHR.status==404){
		bootbox.alert("Artista no encontrado.");
	}
}

//BOTON DEJAR DE SEGUIR
$('#unfollow-Artist').submit(function(e) {
	e.preventDefault();
	var idartist=$.cookie('idartist');
	var username=$.cookie('username');
	var password=$.cookie('password');
	
	unfollowArtist(idartist,username,password, unfollowArtistSuccess, unfollowArtistError);
});

function unfollowArtistSuccess(data, status, jqXHR)
{
	if(jqXHR.status==204)
	{
		bootbox.alert("Has dejado de seguir al Artista");
	}
	
	
	$('#unfollow-Artist').hide();
	$('#follow-Artist').show();
}

function unfollowArtistError(jqXHR, options, error)
{
	if(jqXHR.status==409)
	{
		bootbox.alert("No estas siguiendo a este artista");
		
	}
}

$('#follow-Artist').submit(function(e) {
	e.preventDefault();
	var idartist1=$.cookie('idartist');
	var username1=$.cookie('username');
	
	followArtist(idartist1,username1, followArtistSuccess, followArtistError);
});

function followArtistSuccess(data, status, jqXHR)
{
	if(jqXHR.status==204)
	{
		bootbox.alert("Estas siguiendo al artista");
	}
	
	$('#follow-Artist').hide();
	$('#unfollow-Artist').show();
}

function followArtistError(jqXHR, options, error)
{
	if(jqXHR.status==409)
	{
		bootbox.alert("Ya estas siguiendo a este artista");
		
	}
	
	if(jqXHR.status==404)
	{
		bootbox.alert("Error al marcar el artista como seguido");
		
	}
}

function getEventsArtistSuccess(data, status, jqXHR) {
	
	var response = $.parseJSON(jqXHR.responseText);
	$('#listar-Eventos-Artista > tbody').empty();

	$.each(response, function(index, value) {
		
		if(value.city==null)
		{
			value.city='';
		}

		$('#listar-Eventos-Artista > tbody').append('<tr><td> '+value.kind+'</td><td>'+ value.artist + '</td><td>' + value.city+ '</td><td>'+value.country+'</td><td>'+value.date+'</td></tr>');
	});
}


function getEventsArtistError(jqXHR, options, error){
	
	if(jqXHR.status==404){
		bootbox.alert("El artista no tiene ningun evento.");
	}
}