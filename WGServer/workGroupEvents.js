let WG = require("./WorkGroup");
let Client = require("./Client");

module.exports = {
    newgroup: (data, workGroups, socket) => {
        if (!workGroups.has(data.workGroup)) {
            let client = new Client(data.name, data.workGroup, true, data.password, socket.id);

            let newGroup = new WorkGroup(data.workGroup, data.password, data.name);
            newGroup.admin = client;
            newGroup.users.push(client);

            workGroups.set(client.workGroup, newGroup);
            return true;
        } else {
            io.to(socket.id).emit("group_error", JSON.stringify("Group name exists!"));
        }
    }
}