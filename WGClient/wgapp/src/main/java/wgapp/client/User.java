/**
 * Class representing current user using app.
 * Possibly class will be used as Singleton.
 * 
 * @author Andrius Korsakas
 */
package wgapp.client;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import wgapp.inter.Sendable;

public class User implements Sendable{
	private static User user = null;
	private String name;
	private String workGroup;
	private boolean isAdmin;
	private String password;
	private String socketID;
	private static Map<String, String> userData = new HashMap<>();

	private User() {
		super();
		userData.put("name", "");
		userData.put("workGroup", "");
		userData.put("password", "");
		userData.put("socketID", "");
		setIsAdmin(false);
	}

	public void setUser(String name, String workGroup, Boolean isAdmin, String password, String socketID) {
		this.name = name;
		this.workGroup = workGroup;
		this.isAdmin = isAdmin;
		this.password = password;
		this.socketID = socketID;
	}

	public static User getUser() {
		if(User.user == null) {
			User.user = new User();
		}
		return User.user;
	}

	public Map<String, String> getUserData(){
		return userData;
	}

	public void setUserData(Map<String, String> data) {
		userData.putAll(data);
		setName(userData.get("name"));
		setWorkGroup(userData.get("workGroup"));
		setPassword(userData.get("password"));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		userData.put("name", name);
		this.name = name;
	}

	public String getWorkGroup() {
		return workGroup;
	}

	public void setWorkGroup(String workGroup) {
		userData.put("workGroup", workGroup);
		this.workGroup = workGroup;
	}

	public boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		userData.put("password", password);
		this.password = password;
	}

	public String getSocketID() {
		return socketID;
	}

	public void setSocketID(String socketID) {
		userData.put("socketID", socketID);
		this.socketID = socketID;
	}

	@Override
	public String toJSON() {
		return new Gson().toJson(this);
	}

}