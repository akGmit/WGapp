package wgapp.client;

import io.socket.client.Socket;
/**
 * Class responsible for output to socket bound to server.
 * Various user events are sent to server. Custom named event is sent to server with some JSON data if required.
 * These events are then captured at server side and in turn server sends response or performs some actions at server side.
 * 
 * @author ak
 *
 */
public class ConnectionOutput implements Runnable{
	private Socket socket = ConnectionSocket.getSocket();

	public ConnectionOutput() {
		while (!this.socket.connected());
	}

	@Override
	public void run() {
		getWorkGroupList();
		User.getUser().setSocketID(ConnectionSocket.getSocket().id());
	}
	/**
	 * Method to create group.
	 * Sending "newgroup" event to server with JSON string as user.
	 * @param user User JSON representation.
	 */
	public void createGroup(User user) {
		this.socket.emit("newgroup", user.toJSON());
	}
	/**
	 * Event - "join_group" sending to server.
	 * @param user User JSON representation.
	 */
	public void joinGroup(User user) {
		this.socket.emit("join_group", user.toJSON());
	}
	
	/**
	 * Event - "message" sending to server.
	 * @param user User JSON representation.
	 */
	public void sendMessage(String msg) {
		this.socket.emit("message", msg);
	}

	/**
	 * Event - ""get_workgroup_list"" sending to server.
	 * @param user User JSON representation.
	 */
	public void getWorkGroupList() {
		this.socket.emit("get_workgroup_list");
	}
}
