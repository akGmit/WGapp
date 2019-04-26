let WorkGroup = require("./WorkGroup");
let Client = require("./Client");
let workGroups = new Map();
let UserModel = require("./userDbModel");

module.exports = function(io, socket){
	let sessionUser = new Client();
	console.log("Client connected");
	workGroups.set("Group1", null);
		workGroups.set("Group2", null);
		workGroups.set("Group3", null);
	//On user log in
	socket.on("log_in", (usr) => {
		let user = JSON.parse(usr);
		let queryLogIn = UserModel.findOne({name: user.name, password: user.password});
		queryLogIn.exec(function(err, result){
			if(result !== null){
				sessionUser.name = user.name;
				sessionUser.password = user.password;
				sessionUser.socketID = socket.id;
				io.to(socket.id).emit("log_in", true);	
			}else if(result === null){
				io.to(socket.id).emit("log_in", false);	
				io.to(socket.id).emit("error_msg", JSON.stringify("Wrong log in details!"));
			}
		});
	});

	socket.on("new_user", function(usr){
		let newUser = JSON.parse(usr);

		var usernameTakenQuery = UserModel.findOne({name : newUser.name});

		usernameTakenQuery.exec(function(err, user){
			if(user === null){
				let user = new UserModel({name: newUser.name, password: newUser.password});
				let newUserPromise = UserModel.create(user);

				newUserPromise.then(()=>{
					sessionUser.name = user.name;
					sessionUser.password = user.password;
					sessionUser.socketID = socket.id;
					io.to(socket.id).emit("new_user", true);	
				});
			}else{
				io.to(socket.id).emit("new_user", false);
				io.to(socket.id).emit("error_msg", JSON.stringify("Username already exists!"));
			}
		});

	});
	
	//#region comments
	/*
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
	//#endregion
	

	socket.on('new_group', function (data) {

		let req = JSON.parse(data);

		if (!workGroups.has(req.workGroup)) {
			let client = new Client(req.name, req.workGroup, true, req.password, socket.id);

			let newGroup = new WorkGroup(req.workGroup, req.password, req.name);
			newGroup.admin = client;
			newGroup.users.push(client);

			workGroups.set(client.workGroup, newGroup);
			socket.join(req.workGroup);
			io.to(client.workGroup).emit("user_join", JSON.stringify(newGroup.users));
			io.to(socket.id).emit("set_admin", JSON.stringify(client));

			let workGroupList = Array.from(workGroups.keys());
			io.emit("workgroup_list", JSON.stringify(workGroupList));

			sessionUser = client;

		} else {
			io.to(socket.id).emit("error_msg", JSON.stringify("Group name exists!"));
		}
	});
	//#region comments
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
	//#endregion
	socket.on('join_group', function (user) {
		let usr = JSON.parse(user);
		console.log(usr);
		if (workGroups.has(usr.workGroup)) {
			let client = new Client(usr.name, usr.workGroup, false, usr.password, socket.id);
			if (workGroups.get(client.workGroup).password === client.password) {

				socket.join(client.workGroup);
				workGroups.get(client.workGroup).users.push(client);

				let msg = client.name + " connected to the group!";
				let userList = workGroups.get(client.workGroup).users;
				io.to(client.workGroup).emit("user_join", JSON.stringify(userList));
				io.to(client.workGroup).emit("message", msg);

				sessionUser = client;

			} else {
				io.to(socket.id).emit("error_msg", JSON.stringify("Wrong work group password!"));
			}
		} else {
			io.to(socket.id).emit("error_msg", JSON.stringify("Group doesnt exists!"));
		}
	});

	/**
	 * Event - "leave_group". Deals with event sent from client which requests client to leave group.
	 * 
	 */
	socket.on('leave_group', function(){
		console.log(sessionUser.name);
		if (sessionUser.workGroup !== undefined) {
			workGroups.get(sessionUser.workGroup).users = workGroups.get(sessionUser.workGroup).users.filter(user => {
				return user.name !== sessionUser.name;
			});
		}
		let msg = sessionUser.name + " has left the group.";
		socket.to(sessionUser.workGroup).broadcast.emit("user_disconnect", JSON.stringify(sessionUser));
		socket.to(sessionUser.workGroup).broadcast.emit("message", msg);
		io.to(socket.id).emit("leave_group", true);
		sessionUser.workGroup = undefined;
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
	socket.on("disconnect", function () {
		io.to(socket.id).emit("disconnect", true);
		//console.log(sessionUser.workGroup);
		if (sessionUser.workGroup !== undefined) {
			workGroups.get(sessionUser.workGroup).users = workGroups.get(sessionUser.workGroup).users.filter(user => {
				return user.name !== sessionUser.name;
			});
		}
		let msg = sessionUser.name + " has left the group.";
		socket.to(sessionUser.workGroup).broadcast.emit("user_disconnect", JSON.stringify(sessionUser));
		socket.to(sessionUser.workGroup).broadcast.emit("message", msg);
	});
	/**
	 * Event - "get_workgroup_list". Returns list of all work groups of server.
	 */
	socket.on("workgroup_list", function () {
		// workGroups.set("Group1", null);
		// workGroups.set("Group2", null);
		// workGroups.set("Group3", null);
		let workGroupList = Array.from(workGroups.keys());
		console.log(JSON.stringify(workGroupList));
		io.to(socket.id).emit("workgroup_list", JSON.stringify(workGroupList));
	});
};