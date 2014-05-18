
$('#login-form').submit(function(e) {
	e.preventDefault();

	$.cookie('username', $('#username').val());
	$.cookie('password', $('#password').val());

	var username = $('#username').val();
	var password = $('#password').val();

	var opcion=0;
	loginUser(username,password,opcion, loginUserSuccess, loginUserError);
});

function loginUserError(jqXHR, options, error){

	if(jqXHR.status==404){
		bootbox.alert("No estas registrado.");
	}
	
	if(jqXHR.status==409){
		bootbox.alert("Usuario o contraseña incorrectas.");
	}
	
	if(jqXHR.status==403){
		bootbox.alert("No tienes permiso.");
	}
}

function loginUserSuccess(data, status, jqxhr){

	if(jqxhr.status==200){// no se loguea por primera vez, ya esta siguiendo a Artistas
		
		window.location.href = "home.html";
	}
	
	if(jqxhr.status==202){//se loguea por primera vez
		
		window.location.href = "buscador.html";
	}
	
	if(jqxhr.status==201){// se loguea como administrador
		
		window.location.href = "gestionArtistas.html";
	}
	
}

