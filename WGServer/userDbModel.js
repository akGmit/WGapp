var mongoose = require('mongoose');

mongoose.connect('mongodb://wgapp:wgapp123@ds149885.mlab.com:49885/wgapp');

var Schema = mongoose.Schema;

var UserSchema = new Schema({
    name: String,
    password: String
});

var UserModel = mongoose.model("user", UserSchema);

module.exports = UserModel;
