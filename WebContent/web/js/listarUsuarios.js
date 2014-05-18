$(document).ready(
		function() {
			getUsers($.cookie('username'), $.cookie('password'),
					getUsersSuccess, getUsersError);
		});
function getUsersSuccess(data, status, jqXHR) {
	var response = $.parseJSON(jqXHR.responseText);
	$('#listaUsuarios > tbody').empty();
	$.each(response, function(index, value) {
		$('#listaUsuarios > tbody').append(
				'<tr><td> ' + value.name + '</td><td>' + value.username
						+ '</td><td>' + value.email
						+ '</td><td><a class="btn btn-inverse" onclick="deleteUser2(\''
						+ value.username
						+ '\')"><i class="icon-trash icon-white" ></i></a></tr>');
	});
} // onclick="deleteUser("' + value.username + '")"

function getUsersError(jqXHR, options, error) {
	if (jqXHR.status == 403) {
		bootbox.alert("No tienes permiso.");
	}
}

function deleteUser2(value) {
	var username = $.cookie('username');
	var password = $.cookie('password');
	var username1 = value;

	deleteUser(username1, username, password, deleteUserSuccess,
			deleteUserError);
}

// ELIMINAR USUARIO
$('#eliminar-Usuario').submit(
		function(e) {
			e.preventDefault();
			alert("entra al botÃ³");
			var username = $.cookie('username');
			var password = $.cookie('password');
			var username1 = $('#nombre-Usuario').val();

			deleteUser(username1, username, password, deleteUserSuccess,
					deleteUserError);
		});

function deleteUserSuccess(data, status, jqxhr) {
	if (jqxhr.status == 204) {
		bootbox.alert("Usuario eliminado correctamente.");
		getUsers($.cookie('username'), $.cookie('password'), getUsersSuccess,
				getUsersError);
	}
}

function deleteUserError(jqXHR, options, error) {

	if (jqXHR.status == 403) {
		bootbox.alert("No esas autorizado para eliminar el usuario.");
	}

	if (jqXHR.status == 404) {
		bootbox.alert("Este usuario no existe.");
	}
}

//LOG OUT
$('#salirUsuario').click(function(e)  {
	e.preventDefault();
	//eliminamos toda la información de la cookie.
	 $.cookie('username', 'undefined', -1);
	$.cookie('password', 'undefined',-1);
	window.location.href = "login.html";
});
