$(document).ready(function() {
	var username = $.cookie('username');// agafem la cookie del usuario
	var password = $.cookie('password');

	getUser(username, password, getUserSuccess, getUserError);
});

function getUserSuccess(data, status, jqXHR) { // si OK, afegeix info a la
	// taula de l'objecte artista

	$('#resUsuario > tbody').empty(); // infoArtista = id de la tabla del html
	$('#resUsuario > tbody').append(
			// afegim info
			'<tr><td> ' + data.username + '</td><td>' + data.name + '</td><td>'
					+ data.email + '</td></tr>');
}

function getUserError(jqXHR, options, error) {
	if (jqXHR.status == 404) {
		bootbox.alert("Usuario no encontrado.");
	}
}

// MODIFICAR USUARI
$('#modificarDatos').submit(
		function(e) {
			e.preventDefault();

			var name = $('#nombre').val();
			var passw = $('#passw').val();
			var email = $('#email').val();
			
			var updated = new Object();
			updated.name = name;
			updated.email = email;
			updated.password = passw;
			var updatedUser = JSON.stringify(updated);

			updateUser(updatedUser, $.cookie('username'), $.cookie('password'),
					updateUserSuccess, updateUserError);
		});

function updateUserSuccess(data, status, jqxhr) {
	if (jqxhr.status == 204) {
		bootbox.alert("Cuenta modificada correctamente");
		var username = $.cookie('username');// agafem la cookie del usuario
		var password = $.cookie('password');
		getUser(username, password, getUserSuccess, getUserError);		
	}
}

function updateUserError(jqXHR, options, error) {

	if (jqXHR.status == 403) {
		bootbox.alert("No est��s autorizado para modificar el usuario.");
	}

	if (jqXHR.status == 404) {
		bootbox.alert("El usuario no existe.");
	}

	if (jqXHR.status == 400) {
		bootbox.alert("No puedes dejar ning��n campo en blanco.");
	}
	if (jqXHR.status == 409) {
		bootbox.alert("No tienes permiso");
	}
}

// ELIMINAR USUARI
$('#eliminarCuenta').submit(function(e) {
	e.preventDefault();

	var username = $.cookie('username');
	var password = $.cookie('password');
	
	
	deleteAccount(username, password, deleteAccountSuccess, deleteAccountError);
});

function deleteAccountSuccess(data, status, jqxhr) {
	if (jqxhr.status == 204) {
		bootbox.alert("Cuenta eliminada correctamente.");
		
		//eliminamos toda la información de la cookie.
		 $.cookie('username', 'undefined', -1);
		$.cookie('password', 'undefined',-1);
		window.location.href = "login.html";
	}
}

function deleteAccountError(jqXHR, options, error) {

	if (jqXHR.status == 403) {
		bootbox.alert("No esas autorizado para eliminar la cuenta.");
	}

	if (jqXHR.status == 404) {
		bootbox.alert("Este usuario no existe.");
	}
}

//LOG OUT
$('#salir').click(function(e)  {
	e.preventDefault();
	//eliminamos toda la información de la cookie.
	 $.cookie('username', 'undefined', -1);
	$.cookie('password', 'undefined',-1);
	window.location.href = "login.html";
	
	
});