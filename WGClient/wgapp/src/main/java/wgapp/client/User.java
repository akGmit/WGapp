/**
 * Class representing current user using app.
 * Possibly class will be used as Singleton.
 * 
 * @author Andrius Korsakas
 */
package wgapp.client;

import com.google.gson.Gson;
import wgapp.inter.Sendable;

public class User implements Sendable{
  private static User user = null;
  private String name;
  private String workGroup;
  private boolean isAdmin;
  private String password;
  private String socketID;

  private User() {
    super();
  }
  
  public void setUser(String name, String workGroup, Boolean isAdmin, String password, String socketID) {
    this.setUser(name, workGroup, isAdmin, password, socketID);
  }

  public static User getUser() {
    if(User.user == null) {
      User.user = new User();
    }
    return User.user;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getWorkGroup() {
    return workGroup;
  }

  public void setWorkGroup(String workGroup) {
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
    this.password = password;
  }

  public String getSocketID() {
    return socketID;
  }

  public void setSocketID(String socketID) {
    this.socketID = socketID;
  }

  @Override
  public String toJSON() {
    return new Gson().toJson(this);
  }

}