/**
 * Class representing Work Group.
 */
module.exports = class WorkGroup {
    constructor(name, password, admin) {
        this.name = name;
        this.admin = admin;
        this.moderators = new Array();
        this.password = password;
        this.users = new Array();
    }
    addUser(user) {
        this.users.push(user);
    }
    addModerator(mod) {
        this.moderators.push(mod);
    }
};
//module.exports = WorkGroup;