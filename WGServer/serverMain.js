//Server required modules
let app = require('express')();
let server = require('http').Server(app);
let io = require('socket.io')(server);
let java = require('java');
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

let WorkGroup = require("./WorkGroup");
let Client = require("./Client");

//Server listening on port 31337
server.listen(31337);

//Maps for:<WorkGroup name, WorkGroup> and <Socket ID, WorkGroup name>
let workGroups = new Map();
let socketToGroup = new Map();

workGroups.set("1", new WorkGroup("1","1", "a"));
workGroups.set("2", new WorkGroup("2","2", "a"));
workGroups.set("3", new WorkGroup("3","3", "a"));

console.log("Start");



//On event "connection", get the socket and implement responses to various client sent events
io.on('connection', function(socket) {
	let sessionUser = new Client();
	console.log("Client connected");
	 
    /**
     * Event - "newgroup". This event recieves request from client app to create new group. 
     *  Client sends workgroup name, password and user information.
     * 
     * Parse JSON datakt to object.
     * From parsed object create client object.
     * 
     * If/else statement for group creation.
     * If workGroups map doesn't contain work group with same name:
     *  Set this client as admin. Set this client's workgroug to work group name. Set clients socket id.
     *  Create new work group object(work group name, w/g password, admin name).
     *  Add new workgroup to workGroups map.
     *  Join this client(socket) to new w/g.
     *  Send response event "newuser" to client with client parsed to JSON.
	 * Else if w/g already exists
	 * 	Send "group_error" event to this client(socket) with informational message.
     */
    socket.on('newgroup', function(data){

		let req = JSON.parse(data);
		
		if (!workGroups.has(req.workGroup)) {
			let client = new Client(req.name, req.workGroup, true, req.password, socket.id);

			let newGroup = new WorkGroup(req.workGroup, req.password, req.name);
			newGroup.admin = client;
			newGroup.users.push(client);

			workGroups.set(req.workGroup, newGroup);
			socket.join(req.workGroup);
			io.to(req.workGroup).emit("newuser", JSON.stringify(newGroup.users));

			let workGroupList = Array.from(workGroups.keys());
			io.emit("get_workgroup_list", JSON.stringify(workGroupList));

			sessionUser = client;

		} else {
			io.to(socket.id).emit("group_error", JSON.stringify("Group name exists!"));
		}
	});
	/**
	 * Event - "joinGroup". Function dealing with client request to join group.
	 * 	Recives JSON string representing user trying to join.
	 * 
	 * Parse JSON strin to object.
	 * If workgroup exists:
		* 	If recieved workgroup password is correct:
		* 		Create new Client object.
		* 		Join this client(socket) to W/G.
		* 		Send "newuser" event to all workgroup clients with this JSON client string.
		* 		Send informational messsage of new user connected to workgroup. 
		* 	Else:
		* 		Send to this client message that password wasnt correct.
		* 
	 * Else:
	 * 	Work group with this name doesnt exists.
	 */
	socket.on('join_group', function(user) {
		if (workGroups.has(usr.workGroup)) {
			if (workGroups.get(usr.workGroup).password === usr.password) {

				let client = new Client(usr.name, usr.workGroup, false, usr.password, socket.id);

				socket.join(usr.workGroup);
				workGroups.get(usr.workGroup).users.push(client);

				let msg = client.name + " connected to the group!";
				let userList = workGroups.get(client.workGroup).users;
				io.to(client.workGroup).emit("newuser", JSON.stringify(userList));
				io.to(usr.workGroup).emit("message", msg);

				sessionUser = client;
			
			} else {
				io.to(socket.id).emit("login_error", JSON.stringify("Wrong work group password!"));
				return;
			}
		} else {
			io.to(socket.id).emit("group_error", JSON.stringify("Group doesnt exists!"));
			return;
		}
	});
	/**
	 * Event - "getuserlist". Returns user list of a workgroup.
	 */
	socket.on('getuserlist', function(user) {
		let userList = JSON.stringify(workGroups.get(user.groupname).users);
		io.to(user.workGroup).emit("getuserlist", userList);
	});
	/**
	 * Event - "message". When request "message" event recieved,
	 * message displayed to all group users.
	 */
	socket.on("message", function (msg) {
		io.to(sessionUser.workGroup).emit("message", msg);
	});
	/**
	 * Event - "disconnect", user exiting group event.
	 */
	socket.on("disconnect", function(reason) {
		workGroups.get(sessionUser.workGroup).users = workGroups.get(sessionUser.workGroup).users.filter(user => {
			return user.name !== sessionUser.name;
		});
		let msg = sessionUser.name + " has left the group.";
		socket.to(sessionUser.workGroup).broadcast.emit("user_disconnect", JSON.stringify(sessionUser));
		socket.to(sessionUser.workGroup).broadcast.emit("message", msg);
	});
	/**
	 * Event - "get_workgroup_list". Returns list of all work groups of server.
	 */
	socket.on("get_workgroup_list", function(){
		workGroups.set("Group1", null);
		workGroups.set("Group2", null);
		workGroups.set("Group3", null);
		let workGroupList = Array.from(workGroups.keys());
		console.log(JSON.stringify(workGroupList));
		io.to(socket.id).emit("get_workgroup_list", JSON.stringify(workGroupList));
	})

});