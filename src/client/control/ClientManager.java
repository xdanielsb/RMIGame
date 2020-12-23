package client.control;

import client.view.MapGraphics;
import interfaces.IPlayerRemote;
import java.rmi.Naming;
import processing.core.PApplet;

public class ClientManager {
  private IPlayerRemote stub;

  public ClientManager() {}

  /**
   * Connect the client to the server
   *
   * @param username Username of the player
   */
  public void connectToServer(String username) {
    try {
      this.stub = (IPlayerRemote) Naming.lookup("rmi://localhost:1099/PLM");
      MapGraphics map = new MapGraphics(stub, username);
      PApplet.runSketch(new String[] {"MapGraphics"}, map);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
