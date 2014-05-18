var usernameOK = false;

var passwordOK = false;

var emailOK = false;

var usernameExists = false;



$('#username').blur(function() {

	var username = $('#username').val();

	if(username === '') {

		$('#cgusername').addClass('error');

		$('#messUsername').text("No puedes dejar este campo en blanco.");

		$('#messUsername').show();

		usernameOK = false;

	} else {

		$('#cgusername').removeClass('error');

		$('#messUsername').hide();

		usernameOK = true;

		usernameExists = false;

	}

});

$('#username').focus(function() {

	if(!usernameExists){

		$('cgusername').removeClass('error');

		$('#messUsername').hide();

	}

});

$('#password').blur(function() {

	var password = $('#password').val();

	if(password === '') {

		$('#cgpassw').addClass('error');

		$('#messPassw').text("No puedes dejar este campo en blanco.");

		$('#messPassw').show();

	} else {

		$('#cgpassw').removeClass('error');

		$('#messPassw').hide();

		passwordOK = true;

	}

});

$('#password').focus(function() {

	$('cgpassw').removeClass('error');

	$('#messPassw').hide();

});

$('#email').blur(function() {

	var email = $('#email').val();

	if(email === '') {

		$('#cgemail').addClass('error');

		$('#messEmail').text("No puedes dejar este campo en blanco.");

		$('#messEmail').show();

		emailOK = false;

	} else {

		$('#cgemail').removeClass('error');

		$('#messEmail').hide();

		emailOK = true;

	}

});

$('#email').focus(function() {

	$('#cgemail').removeClass('error');

	$('#messEmail').hide();

});

$('#loginForm').submit(function(e) {

	e.preventDefault();

	var username = $('#username').val();

	var password = $('#password').val();

	var email = $('#email').val();

	var name = $('#name').val();



	if(usernameOK && passwordOK && emailOK) {

		var data = new Object();

		data.username = username;

		data.password = password;

		data.email = email;

		data.name = name;

		var user = JSON.stringify(data);

		registerUser(user, registerUserSucces, registerUserError);

	} else alert("No submit");

});



function registerUserSucces(data, status, jqxhr) {

	bootbox.alert("Cuenta creada correctamente.", function(){

		window.location.href = "login.html";

	});

}



function registerUserError(jqxhr, options, error) {

	if(jqxhr.status == 409) {

		usernameExists = true;

		$('#loginForm').addClass('error');

		$('#messUsername').text("El usuario ya existe.");

		$('#messUsername').show();

		$('#username').focus();

	}

	else {

		var response = $.parseJSON(jqxhr.responseText);

		bootbox.alert("Error." + response.errorMessage);

	}

}