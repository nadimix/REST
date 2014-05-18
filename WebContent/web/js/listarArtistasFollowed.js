
$(document).ready(function() {
		var username=$.cookie('username');
		var password=$.cookie('password');
		getArtistsFollowed( username, password ,getArtistsFollowedSuccess,getArtistsFollowedError);
});
function getArtistsFollowedSuccess(data, status, jqXHR){
	
	var response = $.parseJSON(jqXHR.responseText);
	$('#res-Artista-followed > tbody').empty();
	$.each(response, function (index, value) {
		var artistname = encodeURIComponent(value.name);// codificamos la Url
	$('#res-Artista-followed > tbody').append('<tr><td><a href=artista.html?artist='
			+ artistname + '>'+value.name+'</td><td>'+ value.genre +'</td><td>'+ value.info +'</a></td></tr>');
});
}	 	 
function getArtistsFollowedError(jqXHR, options, error){
	if (jqXHR.status == 404) {
		bootbox.alert("No sigues a ningun artista.");
	}		
	
	if (jqXHR.status == 403) {
		bootbox.alert("No tienes permiso.");
	}	
}

//LOG OUT
$('#salir1').click(function(e)  {
	e.preventDefault();
	//eliminamos toda la información de la cookie.

	$.cookie('username', 'undefined', -1);
	$.cookie('password', 'undefined',-1);
	window.location.href = "login.html";
	
});