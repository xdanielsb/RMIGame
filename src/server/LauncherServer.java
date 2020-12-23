package server;

import server.control.GameManager;
import server.control.LogManager;

public class LauncherServer {
  public static void main(String[] args) {
    LogManager.writeLog("Server Starting");
    GameManager ga = new GameManager();
    ga.initServer();
  }
}
