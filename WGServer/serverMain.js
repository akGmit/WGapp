//Server required modules
let app = require('express')();
let server = require('http').Server(app);
let io = require('socket.io')(server);
let connectionHandler = require('./connHandler');
//let java = require('java');
//#region java import test
/**java.classpath.push("./ftplet-api-1.1.1.jar");
java.classpath.push("./ftpserver-core-1.1.1.jar");
java.classpath.push("./mina-core-2.0.16.jar");
java.classpath.push("./slf4j-api-1.7.21.jar");
java.classpath.push("./slf4j-simple-1.7.25.jar");
let serFactory = java.newInstanceSync("org.apache.ftpserver.FtpServerFactory");
let factory = java.newInstanceSync("org.apache.ftpserver.listener.ListenerFactory");
//let fserv = java.newInstanceSync("org.apache.ftpserver.FtpServer");


factory.setPortSync(22122);
//java.callMethodSync(serFactory, "addListener", "default", factory.createListenerSync());
serFactory.addListenerSync("default", factory.createListenerSync());
let fserv = serFactory.createServerSync();
try {
	fserv.startSync();
} catch (error) {
	
}
factory.setServerAddressSync("127.0.0.1");
console.log(factory.getServerAddressSync());*/

/**java.classpath.push("./ftp.jar");
java.classpath.push('ff')
let f = java.newInstanceSync("ff.FTPserver");
try {
	f.startServer();
} catch (error) {
	
}
c
while(f.isSync() === "running"){
	console.log("Running");
}*/
//#endregion

let WorkGroup = require("./WorkGroup");
let Client = require("./Client");

//Server listening on port 31337
server.listen(31337);

console.log("Start");
io.on('connection', function(socket){
	connectionHandler(io, socket);
});
