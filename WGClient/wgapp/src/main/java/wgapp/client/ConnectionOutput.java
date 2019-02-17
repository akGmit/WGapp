package wgapp.client;

import com.google.gson.Gson;
import io.socket.client.Socket;
import wgapp.inter.Observer;
import wgapp.client.User;
/**
 * Class responsible for output to socket bound to server.
 * Various user events are sent to server.
 * @author ak
 *
 */
public class ConnectionOutput implements Runnable, Observer {
	private Socket socket = ConnectionSocket.getSocket();
	private String idSocket;
	private Gson gson;

	public ConnectionOutput(User user) {
		while (!this.socket.connected());
		this.idSocket = this.socket.id();
		user.setSocketID(idSocket);
		gson = new Gson();
	}

	@Override
	public void run() {

	}
	/**
	 * Method to create group.
	 * Sending "newgroup" event to server with JSON string as user.
	 * @param user
	 */
	public void createGroup(User user) {
		System.out.println(user.toJSON());
		this.socket.emit("newgroup", user.toJSON());
	}

	public void joinGroup(User user) {
		this.socket.emit("joing_group", user.toJSON());
	}

	public void getUserList(User user) {
		this.socket.emit("getuserlist", user.toJSON());
	}

	public void sendMessage(String msg) {
		this.socket.emit("message", msg);
	}

	public void getWorkGroupList() {
		this.socket.emit("get_workgroup_list");
	}

	@Override
	public void update(String event, Object obj) {
		// TODO Auto-generated method stub

	}

	public void joinGroup(String group) {
		// TODO Auto-generated method stub

	}
}
