package client;

import client.control.ClientManager;
import client.view.ClientConnectGUI;

public class LauncherClient {

  public static void main(String[] args) {
    ClientManager manager = new ClientManager();
    new ClientConnectGUI(manager);
  }
}
