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
  private User user;
  private String name;
  private String workGroup;
  private boolean isAdmin;
  private String password;
  private String socketID;

  /**
   * @param name specified name of a user.
   * @param workGroup name of a work group user is trying to connect or belongs to.
   * @param isAdmin true - if user is admin of work group, false - if simple user
   * @param password password of a work group trying to connect to or to which user belongs.
   * @param socketID a unique server provided connection ID, probably will be used to identify user for server.
   */
  private User(String name, String workGroup, Boolean isAdmin, String password, String socketID) {
    super();
    this.name = name;
    this.workGroup = workGroup;
    this.isAdmin = isAdmin;
    this.password = password;
    this.socketID = socketID;
  }
  
  public User() {
  }
  
  public void setUser(String name, String workGroup, Boolean isAdmin, String password, String socketID) {
    this.setUser(name, workGroup, isAdmin, password, socketID);
  }
  
  public void setUser() {
    this.user = new User();
  }
  
  public User getUser() {
    if(this.user == null) {
      this.user = new User();
    }
    return this.user;
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