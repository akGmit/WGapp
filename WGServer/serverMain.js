//Server required modules
let app = require('express')();
let server = require('http').Server(app);
let io = require('socket.io')(server);
let WorkGroup = require("./WorkGroup").default;
let Client = require("./Client");

//Server listening on port 31337
server.listen(31337);

//Maps for:<WorkGroup name, WorkGroup> and <Socket ID, WorkGroup name>
let workGroups = new Map();
let socketToGroup = new Map();

console.log("Start");

//On event "connection", get the socket and implement responses to various client sent events
io.on('connection', function(socket) {
	console.log("Client connected");

    /**
     * Event - "newgroup". This event recieves request from client app to create new group. 
     *  Client sends workgroup name, password and user information.
     * 
     * Parse JSON data to object.
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
		let client = new Client(req.name, req.workGroup, req.password);

		if (!workGroups.has(req.workGroup)) {
			client.isAdmin = true;
			client.workGroup = req.workGroup;
			client.socketID = socket.id;

			let newGroup = new WorkGroup(req.workGroup, req.password, req.name);
			newGroup.admin = client;
			newGroup.users.push(client);

			workGroups.set(req.workGroup, newGroup);
			socket.join(req.workGroup);
			io.to(req.workGroup).emit("newuser", JSON.stringify(client));
			console.log(JSON.stringify(client));
			console.log(client.name + " created group " + client.workGroup);
			
		} else {
			io.to(socket.id).emit("group_error", JSON.stringify("Group name exists!"));
			console.log(JSON.stringify("GROUP ERRPR"));
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
	socket.on('joinGroup', function(user) {

		let usr = JSON.parse(user);
		if (workGroups.has(usr.workGroup)) {
			if (workGroups.get(usr.workGroup).password === usr.password) {

				let client = new Client(usr.name, usr.workGroup, usr.isAdmin, req.password, socket.id);

				console.log("CLIENT");
				console.log(client);

				socket.join(usr.workGroup);
				workGroups.get(usr.workGroup).users.push(client);

				let msg = client.name + " connected to the group!";
				let userList = JSON.stringify(workGroups.get(usr.workGroup).users);
				socket.broadcast.to(usr.workGroup).emit("newuser", JSON.stringify(client));
				io.to(usr.workGroup).emit("message", msg);
				console.log(client.name + " joined group " + client.workGroup);
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
		console.log(user.workGroup);
		console.log(workGroups.get(user.groupname));
		let userList = JSON.stringify(workGroups.get(user.groupname).users);
		io.to(user.workGroup).emit("getuserlist", userList);
	});
	/**
	 * Event - "message". When request "message" event recieved,
	 * message displayed to all group users.
 	 */
	socket.on("message", function (msg) {
		let msg = name + "> " + text;
		io.to(group).emit("message", msg);
	});
	/**
	 * Event - "exit", user exiting group event.
	 */
	socket.on("exit", function(group, name) {
		workGroups.get(group).users = workGroups.get(group).users.filter(function(client){
			return client.name !== name;
		});
		console.log(workGroups.get(group).users);
		let msg = name + " has left the group.";
		io.to(group).emit("userLeft", name);
		//io.to(group).emit("message". msg);
	});

});