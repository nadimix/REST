
$(document).ready(function() {
	
	$('#resArtista2').hide();
	
});
$('#buscar-Artista2').submit(function(e) {
	e.preventDefault();
	$('#resArtista2').show();
	var artistname = $('#nombre-Artista2').val();
	var username=$.cookie('username');
	var password=$.cookie('password');
	SearchArtist(artistname,username,password,SearchArtistSuccess, SearchArtistError);
	
});

function SearchArtistSuccess(data, status, jqxhr){
	//mostrar en la tabla la info del artista
	$('#resArtista2 > tbody').empty();
	var artistname = encodeURIComponent(data.name);// codificamos la

 	$('#resArtista2 > tbody').append('<tr><td><a href=artista.html?artist='
			+ artistname + '>'+data.name+'</td><td>'+ data.genre +'</a></td></tr>');
}

function SearchArtistError(jqXHR, options, error){

	if(jqXHR.status==404){
		bootbox.alert("El Artista que quieres buscar no existe.");
	}
	
	if(jqXHR.status==403){
		bootbox.alert("No estas autorizado.");
	}
}
//LOG OUT
$('#salir5').click(function(e)  {
	e.preventDefault();
	//eliminamos toda la información de la cookie.
	 $.cookie('username', 'undefined', -1);
	$.cookie('password', 'undefined',-1);
	window.location.href = "login.html";
	
	
});
