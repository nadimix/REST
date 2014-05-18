var API_BASE_URL = "http://localhost:8080/colourmylife/api/";

//LOG IN
function loginUser(username, password,opcion,callback, callbackError) {
		var url = API_BASE_URL + 'users/' +username+'?password='+password+'&opcion='+opcion;
		
		
		$.ajax({
			url : url,
			type : 'GET',
			username : username,
			password : password,
			headers : {
				
				"Accept" : "application/json"
			},
			crossDomain : true,

			success : function(data, status, jqxhr) {
				callback(data, status, jqxhr);
			},
			error : function(jqXHR, options, error) {
				callbackError(jqXHR, options, error);
			}
		});
	}
	
//OBTENER ARTISTA
function getArtist(artistname,username,callback, callbackError) {
	
		var url = API_BASE_URL + 'artists/'+artistname+'?user='+username;

		$.ajax({
			url : url,
			type : 'GET',
			headers : {
				"Accept" : "application/json"
			// "Access-Control-Allow-Origin" : "*"
			},
			crossDomain : true,

			success : function(data, status, jqxhr) {
				callback(data, status, jqxhr);
			},
			error : function(jqXHR, options, error) {
				callbackError(jqXHR, options, error);
			}
		});
	}

//AÑADIR ARTISTA
function insertArtist(artist,username,password, callback, callbackError) {
		
	var url = API_BASE_URL + 'artists';

	$.ajax({
		crossDomain : true,
		url : url,
		type : 'POST',
		data : artist,
		dataType : 'json',
		username : username,
		password : password,
		headers : {
			"Content-Type" : "application/json",
			"Accept" : "application/json"
				
		},

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			
			callbackError(jqXHR, options, error);
		}
	});
}

//ELIMINAR ARTISTA
function deleteArtist(artistname1,username,password, callback, callbackError) {
	
	var url = API_BASE_URL + 'artists/'+artistname1;
	$.ajax({
		url : url,
		type : 'DELETE',
		username : username,
		password : password,
		headers : {
			"Accept" : "application/json"
		},
		crossDomain : true,

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			callbackError(jqXHR, options, error);
		}
	});
}
//ELIMINAR USUARIO
function deleteUser(username1,username,password, callback, callbackError) {
	
	var url = API_BASE_URL + 'users/'+username1;
	$.ajax({
		url : url,
		type : 'DELETE',
		username : username,
		password : password,
		headers : {
			"Accept" : "application/json",
		// "Access-Control-Allow-Origin" : "*"
		},
		crossDomain : true,

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			callbackError(jqXHR, options, error);
		}
	});

}
//ELIMINAR EVENTO
function deleteEvent(artistname2,idevent,username,password, callback, callbackError) {
	///artists/{artist}/events/{eventid}
	var url = API_BASE_URL + 'artists/'+artistname2+'/events/'+idevent;
	$.ajax({
		url : url,
		type : 'DELETE',
		username : username,
		password : password,
		headers : {
			"Accept" : "application/json"
		},
		crossDomain : true,

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			callbackError(jqXHR, options, error);
		}
	});
}

//AÑADIR EVENTO
function insertEvent(event,artistname3, callback, callbackError) {
	///artists/{artist}/events
	var url = API_BASE_URL + 'artists/'+artistname3+'/events';


	$.ajax({
		crossDomain : true,
		url : url,
		type : 'POST',
		data : event,
		dataType : 'json',
		headers : {
			"Content-Type" : "application/json",
			"Accept" : "application/json",
			"Authorization" : "Basic YWRtaW46YWRtaW4=",
				
		},

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			
			callbackError(jqXHR, options, error);
		}
	});
}

//MODIFICAR EVENTO
function updateEvent(event1,artistname3,id,username,password, callback, callbackError) {
	////artists/{artist}/events/{eventid}
	var url = API_BASE_URL + 'artists/'+artistname3+'/events/'+id;

	$.ajax({
		crossDomain : true,
		url : url,
		username : username,
		password : password,
		type : 'PUT',
		data : event1,
		dataType : 'json',
		headers : {
			"Content-Type" : "application/json",
			"Accept" : "application/json"
		},
	
		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			
			callbackError(jqXHR, options, error);
		}
	});
}
//OBTENER ARTISTAS
function getArtists(username,password,callback, callbackError) {

	var url = API_BASE_URL + 'artists';
	$.ajax({
		url : url,
		type : 'GET',
		username : username,
		password : password,
		headers : {
			"Accept" : "application/json",
			"Authorization" : "Basic YWRtaW46YWRtaW4="
		// "Access-Control-Allow-Origin" : "*"
		},
		crossDomain : true,

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			callbackError(jqXHR, options, error);
		}
	});
}
//OBTENER USUARIOS
function getUsers(username,password,callback, callbackError) {

	var url = API_BASE_URL + 'users';
	$.ajax({
		url : url,
		type : 'GET',
		username : username,
		password : password,
		headers : {
			"Accept" : "application/json"
		// "Access-Control-Allow-Origin" : "*"
		},
		crossDomain : true,

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			callbackError(jqXHR, options, error);
		}
	});
}

//DEJAR DE SEGUIR AL ARTISTA

function unfollowArtist(idartist,username,password,callback, callbackError) {

	var url = API_BASE_URL + 'users/'+username+'/following?idartist='+idartist;
	$.ajax({
		crossDomain : true,
		url : url,
		type : 'DELETE',
		username : username,
		password : password,
		//data : event,
		//dataType : 'json',
		headers : {
			//"Content-Type" : "application/json",
			"Accept" : "application/json",
			//"Authorization" : "Basic YWRtaW46YWRtaW4=",
				
		},

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			
			callbackError(jqXHR, options, error);
		}
	});
}

//EVENTOS DE LOS ARTISTAS SEGUIDOS POR EL UN USUARIO:

function getEventsArtists(username,password,callback, callbackError) {
///users/{username}/events
	console.log("getEventsArtists " +username);
	var url = API_BASE_URL + 'users/'+username+'/events';
	$.ajax({
		url : url,
		type : 'GET',
		//username : username,
		//password : password,
		headers : {
			"Accept" : "application/json",
		// "Access-Control-Allow-Origin" : "*"
		},
		crossDomain : true,

		success : function(data, status, jqxhr) {
			console.log("getEventsArtists success");
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			console.log("getEventsArtists error");
			callbackError(jqXHR, options, error);
		}
	});
}
//BUSCAR ARTISTA:
function SearchArtist(artistname,username,password,callback, callbackError) {
	//artists/{artist}
		var url = API_BASE_URL + 'artists/'+artistname;
		$.ajax({
			url : url,
			type : 'GET',
			username : username,
			password : password,
			headers : {
				"Accept" : "application/json",
			// "Access-Control-Allow-Origin" : "*"
			},
			crossDomain : true,

			success : function(data, status, jqxhr) {
				callback(data, status, jqxhr);
			},
			error : function(jqXHR, options, error) {
				callbackError(jqXHR, options, error);
			}
		});
	}

//NARCAR ARTISTA COMO SEGUIDO
function followArtist(idartist,username,callback, callbackError) {

	var url = API_BASE_URL + 'users/'+username+'/following?idartist='+idartist;
	
	
	$.ajax({
		crossDomain : true,
		url : url,
		type : 'POST',
		
		headers : {
			
			"Accept" : "application/json",
			
				
		},

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			
			callbackError(jqXHR, options, error);
		}
	});
}

//MARCAR EVENTO FAVORITO.

function favEvent(username,idevent,password,callback, callbackError) {
	//users/{username}/events/fav?idevent=1

	var url = API_BASE_URL + 'users/'+username+'/events/fav?idevent='+idevent;
	
	$.ajax({
		crossDomain : true,
		url : url,
		type : 'POST',
		username : username,
		password : password,
		//data : event,
		//dataType : 'json',
		headers : {
			//"Content-Type" : "application/json",
			"Accept" : "application/json",
			//"Authorization" : "Basic YWRtaW46YWRtaW4=",
				
		},

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			
			callbackError(jqXHR, options, error);
		}
	});
}

//DESMARCAR EVENTO FAVORITO.

function unfavEvent(username,idevent,password,callback, callbackError) {
	//users/{username}/events/fav?idevent=1

	var url = API_BASE_URL + 'users/'+username+'/events/fav?idevent='+idevent;
	
	$.ajax({
		crossDomain : true,
		url : url,
		type : 'DELETE',
		username : username,
		password : password,
		//data : event,
		//dataType : 'json',
		headers : {
			//"Content-Type" : "application/json",
			"Accept" : "application/json",
			//"Authorization" : "Basic YWRtaW46YWRtaW4=",
				
		},

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			
			callbackError(jqXHR, options, error);
		}
	});
}

//MOSTRAR ARTISTAS SEGUIDOS
function getArtistsFollowed(username,password,callback, callbackError) {
	//users/{username}/following
		var url = API_BASE_URL + 'users/'+username+'/following';
		$.ajax({
			url : url,
			type : 'GET',
			//username : username,
			//password : password,
			headers : {
				"Accept" : "application/json",
				// "Access-Control-Allow-Origin" : "*"
			},
			crossDomain : true,

			success : function(data, status, jqxhr) {
				//console.log("getEventsArtists success");
				callback(data, status, jqxhr);
			},
			error : function(jqXHR, options, error) {
				//console.log("getEventsArtists error");
				callbackError(jqXHR, options, error);
			}
		});
	}


//MOSTRAR EVENTOS FAVORITOS
function getEventsFav(username,password,callback, callbackError) {
	///users/{username}/events/fav
		var url = API_BASE_URL + 'users/'+username+'/events/fav';
		$.ajax({
			url : url,
			type : 'GET',
			//username : username,
			//password : password,
			headers : {
				"Accept" : "application/json",
			// "Access-Control-Allow-Origin" : "*"
			},
			crossDomain : true,

			success : function(data, status, jqxhr) {
				//console.log("getEventsArtists success");
				callback(data, status, jqxhr);
			},
			error : function(jqXHR, options, error) {
				//console.log("getEventsArtists error");
				callbackError(jqXHR, options, error);
			}
		});
	}

//FILTRAR EVENTOS DE LOS ARTISTAS SEGUIDOS POR EL TIPO DE EVENTO
function getEventsArtistsKind(username,password,kind,callback, callbackError) {
	///users/{username}/events?kind=
	
		var url = API_BASE_URL + 'users/'+username+'/events?kind='+kind;
		$.ajax({
			url : url,
			type : 'GET',
			username : username,
			password : password,
			headers : {
				"Accept" : "application/json",
			// "Access-Control-Allow-Origin" : "*"
			},
			crossDomain : true,

			success : function(data, status, jqxhr) {
				console.log("getEventsArtists success");
				callback(data, status, jqxhr);
			},
			error : function(jqXHR, options, error) {
				console.log("getEventsArtists error");
				callbackError(jqXHR, options, error);
			}
		});
	}

//OBTENER USUARIO
function getUser(username, password, callback, callbackError) {

	var url = API_BASE_URL + 'users/'+username+'?option=1';
	$.ajax({
		url : url,
		type : 'GET',
		username : username,
		password : password,
		headers : {
			"Accept" : "application/json"
		// "Access-Control-Allow-Origin" : "*"
		},
		crossDomain : true,
		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			callbackError(jqXHR, options, error);
		}
	});
}

//MODIFICAR USUARIO
function updateUser(updatedUser, username, password, callback, callbackError) {
	var url = API_BASE_URL + 'users/' + username;

	$.ajax({
		crossDomain : true,
		url : url,
		type : 'PUT',
		data : updatedUser,
		dataType : 'json',
		//username : username,
		//password : password,
		headers : {
			"Content-Type" : "application/json",
			"Accept" : "application/json"
		},

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {

			callbackError(jqXHR, options, error);
		}
	});
}

//REGISTRAR USUARIO
function registerUser(user, callback, callbackError) {
	var url = API_BASE_URL + 'users';

	$.ajax({
		url : url,
		type : 'POST',
		data : user,
		dataType : 'json',
		headers : {
			"Content-Type" : "application/json",
			"Accept" : "application/json"
		// "Access-Control-Allow-Origin" : "*"
		},
		crossDomain : true,
		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			callbackError(jqXHR, options, error);
		}
	});
}
//EVENTOS DEL ARTISTA
function getEventsArtist(artistname,username,password,callback, callbackError) {
//artists/{artist}/events
	console.log("getEventsArtists " +username);
	var url = API_BASE_URL + 'artists/'+artistname+'/events';
	$.ajax({
		url : url,
		type : 'GET',
		username : username,
		password : password,
		headers : {
			"Accept" : "application/json",
		// "Access-Control-Allow-Origin" : "*"
		},
		crossDomain : true,

		success : function(data, status, jqxhr) {
			console.log("getEventsArtists success");
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			console.log("getEventsArtists error");
			callbackError(jqXHR, options, error);
		}
	});
}

//ELIMINAR CUENTA
function deleteAccount(username, password, callback, callbackError) {

	var url = API_BASE_URL + 'users/' + username;
	$.ajax({
		url : url,
		type : 'DELETE',
		username : username,
		password : password,
		headers : {
			"Accept" : "application/json",
		// "Access-Control-Allow-Origin" : "*"
		},
		crossDomain : true,

		success : function(data, status, jqxhr) {
			callback(data, status, jqxhr);
		},
		error : function(jqXHR, options, error) {
			callbackError(jqXHR, options, error);
		}
	});
}


