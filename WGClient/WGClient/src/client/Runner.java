package client;

import java.awt.EventQueue;


public class Runner {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    new Thread(new WorkGroupClient()).start();
  }

}
