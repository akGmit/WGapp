package wgapp.client;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter.Listener;
import wgapp.inter.Observer;
import wgapp.inter.Subject;

/**
 * Class responsible for initializing connection to server and dealing with server responses.
 * 
 * @author ak
 *
 */
public class ConnectionSocket implements Runnable, Subject {
	private List<Observer> observers;
	private static Socket socket;
	private String servAddress = "http://localhost:31337";
	private Gson gson = new Gson();
	
	/**
	 * ConnectionSocket constructor. 
	 * Takes no parameters. Initializes observers list and binds socket to server address.
	 */
	public ConnectionSocket() {
		observers = new ArrayList<>();
		try {
			ConnectionSocket.socket = IO.socket(servAddress);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		registerServerEvents();
	}
	/**
	 * Method to register various server response events to socket.
	 * Each 
	 */
	private void registerServerEvents() {
		newUser();
		recieveMessage();
		getUserList();
		onDisconnect();
		failedToCreateGroup();
		loginError();
		groupNameError();
		getWorkGroupList();
	}
	/**
	 * Gets socket which is bound to server.
	 * @return Socket which is bound to server.
	 */
	public static Socket getSocket() {
		return ConnectionSocket.socket;
	}
	/**
	 * Method to connect socket to server.
	 */
	public static void connect() {
		System.out.println("COnnneect");
		ConnectionSocket.socket.connect();
	}

	private void groupNameError() {
		ConnectionSocket.socket.on("group_erorr", new Listener() {

			@Override
			public void call(Object... args) {
				String groupNameError = gson.fromJson((String)args[0], String.class);
				notifyObserver("group_erorr", groupNameError);
			}
		});
	}
	
	private void loginError() {
		ConnectionSocket.socket.on("login_error", new Listener() {

			@Override
			public void call(Object... args) {
				String loginError = gson.fromJson((String)args[0], String.class);
				notifyObserver("login_error", loginError);
			}
		});
	}
	
	private void failedToCreateGroup() {
		ConnectionSocket.socket.on("group_error", new Listener() {

			@Override
			public void call(Object... args) {
				String errorGroup = gson.fromJson((String)args[0], String.class);
				notifyObserver("group_error", errorGroup);
			}
		});
	}

	private void onDisconnect() {
		ConnectionSocket.socket.on(Socket.EVENT_DISCONNECT, new Listener() {

			@Override
			public void call(Object... args) {

				ArrayList<User> userList = gson.fromJson((String) args[0], new TypeToken<ArrayList<User>>(){}.getType());
				notifyObserver(Socket.EVENT_DISCONNECT, userList);
				/*
				 * User userDiisconnected = gson.fromJson((String) args[0], User.class);
				 * notifyObserver(userDiisconnected);
				 */
			}
		});
	}
	
	private void getUserList() {
		ConnectionSocket.socket.on("getuserlist", new Listener() {

			@Override
			public void call(Object... args) {
				ArrayList<User> userList = gson.fromJson((String)args[0], new TypeToken<ArrayList<User>>(){}.getType());
				notifyObserver("getuserlist", userList);
			}
		});
	}
	
	private void recieveMessage() {
		ConnectionSocket.socket.on(Socket.EVENT_MESSAGE, new Listener() {

			@Override
			public void call(Object... args) {
				String message = gson.fromJson((String)args[0], String.class);
				notifyObserver(Socket.EVENT_MESSAGE, message);
			}
		});
	}
	
	private void newUser() {
		ConnectionSocket.socket.on("newuser", new Listener() {

			@Override
			public void call(Object... args) {
				User user = gson.fromJson((String) args[0], User.class);
				System.out.println(user.getName());
				notifyObserver("newuser", user);
			}
		});
	}
	
	private void getWorkGroupList() {
		ConnectionSocket.socket.on("get_workgroup_list", new Listener() {
			
			@Override
			public void call(Object... args) {
				ArrayList<String> workGroupList = gson.fromJson((String)args[0], new TypeToken<ArrayList<String>>(){}.getType());
				notifyObserver("get_workgroup_list", workGroupList);
			}
		});
	}


	@Override
	public void notifyObserver(String event, Object obj) {
		for(Observer o : observers) {
			o.update(event, obj);
		}
	}

	@Override
	public void addObserver(Observer observer) {
		this.observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		this.observers.remove(observer);
	}

}
