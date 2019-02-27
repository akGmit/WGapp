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
 * Requests and events are sent from client app suing ConnectionOutput class methods, server catches the event
 * and sends response or performs some other actions.
 * Server responses are dealt with using this class instance.
 * Using private instance methods and Socket class methods, each event response is registered with a Listener
 * which listens for server responses and deals with them.
 * ConnectionSocket class implements Subject interface, which Observer objects can observe and be 
 * notified about any state changes.
 * 
 * @author ak
 *
 */
public class ConnectionSocket implements Runnable, Subject {
	private List<Observer> observers;
	private static Socket socket;
	private final String servAddress = "http://localhost:31337";
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
		}finally {

		}
	}

	@Override
	public void run() {
		registerServerEvents();
	}
	/**
	 * Single method to register Listeners on server event responses.
	 * 
	 */
	private void registerServerEvents() {
		newUser();
		logIn();
		userJoin();
		recieveMessage();
		adminSet();
		getWorkGroupList();
		userDisconnect();
		errorResponse();
	}
	/**
	 * Gets socket which is bound to server.
	 * @return Socket which is bound to server.
	 */
	public static Socket getSocket() {
		return ConnectionSocket.socket;
	}
	/**
	 * Static method initiating connection to server.
	 */
	public static void connect() {
		System.out.println("COnnneect");
		ConnectionSocket.socket.connect();
	}

	public static void disconnect() {
		ConnectionSocket.socket.disconnect();
	}

	private void adminSet() {
		ConnectionSocket.socket.on(Events.SET_ADMIN, new Listener() {

			@Override
			public void call(Object... args) {
				User.getUser().setIsAdmin(true);
			}
		});
	}

	private void errorResponse() {
		ConnectionSocket.socket.on(Events.ERROR_MSG, new Listener() {

			@Override
			public void call(Object... args) {
				String errorMsg = (String)args[0];
				notifyObserver(Events.ERROR_MSG, errorMsg);
			}
		});
	}

	private void recieveMessage() {
		ConnectionSocket.socket.on(Socket.EVENT_MESSAGE, new Listener() {

			@Override
			public void call(Object... args) {
				String message = (String)args[0];
				notifyObserver(Socket.EVENT_MESSAGE, message);
			}
		});
	}

	private void logIn() {
		ConnectionSocket.socket.on(Events.LOG_IN, new Listener() {

			@Override
			public void call(Object... args) {
				notifyObserver(Events.LOG_IN, args[0]);
			}
		});
	}

	private void newUser() {
		ConnectionSocket.socket.on(Events.NEW_USER, new Listener() {

			@Override
			public void call(Object... args) {
				notifyObserver(Events.NEW_USER, args[0]);
			}
		});
	}

	private void userJoin() {
		ConnectionSocket.socket.on(Events.USER_JOIN, new Listener() {

			@Override
			public void call(Object... args) {
				ArrayList<User> userList = gson.fromJson((String)args[0], new TypeToken<ArrayList<User>>(){}.getType());
				notifyObserver(Events.USER_JOIN, userList);
			}
		});
	}

	private void getWorkGroupList() {
		ConnectionSocket.socket.on(Events.WORKGROUP_LIST, new Listener() {

			@Override
			public void call(Object... args) {
				ArrayList<String> workGroupList = gson.fromJson((String)args[0], new TypeToken<ArrayList<String>>(){}.getType());
				notifyObserver(Events.WORKGROUP_LIST, workGroupList);
			}
		});
	}

	private void userDisconnect() {
		ConnectionSocket.socket.on(Events.USER_DISCONNECT, new Listener() {

			@Override
			public void call(Object... args) {
				User userDisconnected = gson.fromJson((String)args[0], User.class);
				notifyObserver(Events.USER_DISCONNECT, userDisconnected);
			}
		});
	}

	/**
	 * Implementation of Subject interface method.
	 * @param event String representing event type.
	 * @param obj Object type object sent to observers.
	 */
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
