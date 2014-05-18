
//AÑADIR ARTISTA
$('#list-Artista').submit(function(e) {
	e.preventDefault();
	var artistname = $('#nombreArtista').val();
	var genreArtistId = $('#genero1Artista').val();
	var genre2ArtistId = $('#genero2Artista').val();
	var infoartist= $('#infoArtista').val();

	
	var artist = new Object();
	artist.name = artistname;
	artist.genreId =genreArtistId;
	artist.genre2Id = genre2ArtistId;
	artist.info=infoartist;
	insertArtist(JSON.stringify(artist),$.cookie('username'), $.cookie('password'),insertArtistSuccess, insertArtistError);
	
});

function insertArtistSuccess(data, status, jqxhr){
	if(jqxhr.status==204){
		bootbox.alert("Artista añadido correctamente.");
	}

}

function insertArtistError(jqXHR, options, error){

	if(jqXHR.status==403){
		bootbox.alert("No esas autorizado para añadir un artista.");
	}
	
	if(jqXHR.status==400){
		bootbox.alert("El campo nombre de artista y genero no pueden estar vacios.");
	}
	
	if(jqXHR.status==409){
		bootbox.alert("El artista que quieres añadir ya existe.");
	}
	
	if(jqXHR.status==404){
		bootbox.alert("Error al añadir el artista.");
	}
}

//ELIMINAR USUARIO
$('#eliminar-Usuario').submit(function(e) {
	e.preventDefault();
	var username= $.cookie('username');
	var password= $.cookie('password');
	var username1=$('#nombreUsuario').val();
	deleteUser(username1,username,password,deleteUserSuccess,deleteUserError);
});


function deleteUserSuccess(data, status, jqxhr){
	if(jqxhr.status==204){
		bootbox.alert("Usuario eliminado correctamente.");
	}
}

function deleteUserError(jqXHR, options, error){
	
	if(jqXHR.status==403){
		bootbox.alert("No esas autorizado para eliminar el usuario.");
	}
	
	if(jqXHR.status==404){
		bootbox.alert("Este usuario no existe.");
	}
}


//ELIMINAR ARTISTA

$('#eliminar-Artista').submit(function(e) {
	e.preventDefault();
	var artistname1= $('#nombre-Artista').val();
	var username= $.cookie('username');
	var password= $.cookie('password');
	deleteArtist(artistname1,username,password,deleteArtistSuccess,deleteArtistError);
});

function deleteArtistSuccess(data, status, jqxhr){
	if(jqxhr.status==204){
		bootbox.alert("Artista eliminado correctamente.");
		
	}
}

function deleteArtistError(jqXHR, options, error){

	if(jqXHR.status==403){
		bootbox.alert("No esas autorizado para eliminar el artista.");
	}
	
	if(jqXHR.status==404){
		bootbox.alert("Este Artista no existe.");
	}
}

//ELIMINAR EVENTO:

$('#eliminar-Evento').submit(function(e) {
	e.preventDefault();
	var artistname2= $('#nombreartista').val();
	var idevent= $('#idevento').val();
	var username= $.cookie('username');
	var password= $.cookie('password');
	
	deleteEvent(artistname2,idevent,username,password,deleteEventSuccess,deleteEventError);
});


function deleteEventSuccess(data, status, jqxhr){
	if(jqxhr.status==204){
		bootbox.alert("Evento eliminado correctamente.");
	}
}

function deleteEventError(jqXHR, options, error){

	if(jqXHR.status==403){
		bootbox.alert("No esas autorizado para eliminar.");
	}
	
	if(jqXHR.status==404){
		bootbox.alert("Este Evento no existe, comprueba que el evento es del artista correspondiente.");
	}
}

//AÑADIR EVENTO:
$('#insert-Evento').submit(function(e) {
	
	e.preventDefault();
	//alert("entra en añadir evento");
	var idkind = $('#tipo-Evento').val();
	var artistname3 = $('#nombre-Artista1').val();
	var date = $('#fecha-Evento').val();
	var place= $('#lugar-Evento').val();
	var city=$('#ciudad-Evento').val();
	var country=$('#pais-Evento').val();
	var info=$('#info-Evento').val();
	
	var event = new Object();
	event.kindId= idkind;
	event.date= date;
	event.artist=artistname3;
	event.place = place;
	event.info=info;
	event.city=city;
	event.country=country;
	//alert(JSON.stringify(event));
	insertEvent(JSON.stringify(event),artistname3, insertEventSuccess, insertEventError);
	
});

function insertEventSuccess(data, status, jqxhr){
 	bootbox.alert("Evento creado correctamente. ");

}

function insertEventError(jqXHR, options, error){

	if(jqXHR.status==403){
		bootbox.alert("No esas autorizado para añadir el evento.");
	}
	if(jqXHR.status==404){
		bootbox.alert("Error al añadir el evento.");
	}
}

//MODIFICAR EVENTO:

$('#modificar-Evento').submit(function(e) {
	e.preventDefault();
	
	var username= $.cookie('username');
	var password= $.cookie('password');
	var id = $('#idEvento').val();
	var artistname3=$('#nombreArtista1').val();
	var date = $('#fechaEvento').val();
	var place= $('#lugarEvento').val();
	var city=$('#ciudadEvento').val();
	var country=$('#paisEvento').val();
	var info=$('#infoEvento').val();

	
	var event1 = new Object();
	event1.date = date;
	event1.pace = place;
	event1.info=info;
	event1.city=city;
	event1.country=country;
	alert(JSON.stringify(event1));
	updateEvent(JSON.stringify(event1),artistname3,id,username,password,updateEventSuccess, updateEventError);
	
});


function updateEventSuccess(data, status, jqxhr){
 	bootbox.alert("Evento modificado correctamente. ");
}

function updateEventError(jqXHR, options, error){

	if(jqXHR.status==403){
		bootbox.alert("No esas autorizado para modificar el evento.");
	}
	
	if(jqXHR.status==404){
		bootbox.alert("El evento que quieres modificar no existe.");
		
	}
	
	if(jqXHR.status==400){
		bootbox.alert("El campo de la ciudad no puede estar en blanco.");
		
	}
}



