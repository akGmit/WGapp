/**
 * ChatServer class representing main chat server.
 * 
 * @author ak
 */
package ie.gmit.sw;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class WorkGroupServer {
  private List<ClientHandler> clients;
  private ServerSocket listener;
  
  /**
   * ChatServer constructor. Initializing instance variables.
   * Infinite while loop for accepting connections. Inside loop listening for new connections to ServerSocket.
   * After connection accepted, creating new thread for connection, starting it and adding to clients list.
   */
  public WorkGroupServer() {
    clients = new ArrayList<>();
    
    try {
      listener = new ServerSocket(2005,10);

      while(true) {
        System.out.println("Server running, listening for connections...");
        Socket newconnection = listener.accept();

        System.out.println("New client connected.");
        ClientHandler client = new ClientHandler(newconnection, clients);

        client.start();
        clients.add(client);
      }
    } 

    catch (IOException e) {
      System.out.println("Socket not opened");
      e.printStackTrace();
    }
  }
}

/**
 * Inner ClientHandler class extends Thread class. 
 * For each new connection(client) - individual instance of this class.
 * 
 * @author ak
 *
 */
class ClientHandler extends Thread {

  private Socket individualconnection;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private String message;
  private List<ClientHandler> clients;
  private String nickName;
  /**
   * Client thread constructor.
   * 
   * @param s Type Socket instance. Individual connection socket, representing client. 
   * @param clients Type List instance containing list of clients. Shared among all ClientHandler threads.
   */
  public ClientHandler(Socket s, List<ClientHandler> clients) {
    individualconnection = s;
    this.clients = clients;
  }
  /**
   * Method writing to ObjectOutputStream instance.
   * 
   * @param msg String type parameter, representing message to be sent.
   */
  private void sendMessage(String msg) {
    try {
      out.writeObject(msg);
      //out.flush();
    }
    catch(IOException ioException) {
      ioException.printStackTrace();
    }
  }
  /**
   * Method required for thread running.
   * Initializing ObjectOutputStream and ObjectInputStream variables to individual socket.
   * Setting thread name as a clients nick name.
   * Do while loop for reading all clients messages and sending them to each client in for each loop.
   * 
   */
  public void run(){
    try {
      out = new ObjectOutputStream(individualconnection.getOutputStream());
      out.flush();
      in = new ObjectInputStream(individualconnection.getInputStream());
      System.out.println("Connection from IP address "+individualconnection.getInetAddress());

      sendMessage("Welcome to chat!");
      nickName = (String)in.readObject();
      Thread.currentThread().setName(nickName);

      do {
        message=(String) in.readObject();
        for(ClientHandler client : clients) {
          client.sendMessage(nickName + ">> " + message);
        }

      } while (!message.equalsIgnoreCase("-12"));
    }

    catch (IOException e) {
      e.printStackTrace();
    } 
    catch (ClassNotFoundException e) {
      e.printStackTrace();
    } 
    finally {
      try 
      {
        out.close();
        in.close();
        individualconnection.close();
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }
}