//Server required modules
let app = require('express')();
let server = require('http').Server(app);
let io = require('socket.io')(server);
let connectionHandler = require('./connHandler');


// io.listen(31337);

// console.log("Start");
// io.on('connection', function(socket){
// 	connectionHandler(io, socket);
// });
let socketlist = [];

module.exports = class Server{
	
	start(){
		server.listen(31337);

		io.on('connection', function(socket){
			connectionHandler(io, socket);
			socketlist.push(socket);  
		});
	};

	stop(){
		
		io.httpServer.close();
		socketlist.forEach(function(socket) {        
			socket.disconnect(true);                      
		});   
		//close remote sockets
		server.close(()=>{
			
		});
		// server.close(()=>{
		// 	process.exit(0);
		// });
	}

}
	
