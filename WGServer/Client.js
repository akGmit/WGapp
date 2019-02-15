/**
 * Class representing client.
 */
class Client{
    constructor(name, workGroup, isAdmin, password, socketID){
        this.name = name;
        this.workGroup = workGroup;
        this.isAdmin = isAdmin;
        this.password = password;
        this.socketID = socketID;
    }
}

module.exports = Client;