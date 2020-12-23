package server.remote;

import interfaces.Board;
import interfaces.IPlayerRemote;
import interfaces.Player;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import server.control.GameManager;
import server.control.LogManager;
import server.model.PlayerImpl;

/** This class will handle all method callable by the client (other than model manipulation) */
public class PlayerRemote extends UnicastRemoteObject implements IPlayerRemote {

  private static final long serialVersionUID = 1L;

  private GameManager gameManager;
  private final Object mutex = new Object();
  private int playerIdIncrement = 0;

  /**
   * Main constructor
   *
   * @param gameManager
   * @throws RemoteException
   */
  public PlayerRemote(GameManager gameManager) throws RemoteException {
    this.gameManager = gameManager;
  }

  /**
   * Method to register a player
   *
   * @param p : String name of the player
   * @return : int id of the new player
   */
  @Override
  public int registerPlayer(String p) throws RemoteException {
    synchronized (mutex) {
      Player newPlayer = new PlayerImpl(playerIdIncrement, p);
      gameManager.addPlayer(newPlayer);
      LogManager.writeLog(p + " is now connected with PID : " + playerIdIncrement);
      return playerIdIncrement++;
    }
  }

  /**
   * Method to ask for a player movement
   *
   * @param id : int id of the player
   * @param mouseX : float X direction of the movement
   * @param mouseY : float Y direction of the movement
   */
  @Override
  public void sendMousePosition(int id, float mouseX, float mouseY) throws RemoteException {
    gameManager.sendMousePosition(id, mouseX, mouseY);
  }

  /**
   * Method to ask to a player to throw Food
   *
   * @param id : int id of the player
   * @param mouseX : float X direction of the throw
   * @param mouseY : float Y direction of the throw
   */
  @Override
  public void throwFood(int idPlayer, float mouseX, float mouseY) throws RemoteException {
    gameManager.throwFood(idPlayer, mouseX, mouseY);
  }

  /**
   * Method to ask for a player to split
   *
   * @param id : int id of the player
   * @param mouseX : float X direction of the split
   * @param mouseY : float Y direction of the split
   */
  @Override
  public void split(int idPlayer, float mouseX, float mouseY) throws RemoteException {
    gameManager.split(idPlayer, mouseX, mouseY);
  }

  /**
   * Method to get the board
   *
   * @return the game Board
   */
  @Override
  public Board getBoard() throws RemoteException {
    return gameManager.getBoard();
  }

  /**
   * Method to remove a player of the game
   *
   * @param id : int id of the player
   */
  @Override
  public void removePlayer(int id) throws RemoteException {
    gameManager.removePlayer(id);
  }

  /** Method to get the time left in the game return int number of milliseconds left in the game */
  @Override
  public int getTimer() throws RemoteException {
    return gameManager.getTimer();
  }

  /**
   * Method to know if the game is over
   *
   * @return true if the game is over, false if not
   */
  @Override
  public boolean gameOver() throws RemoteException {
    boolean isGameOver = gameManager.gameOver();
    if (isGameOver && this.gameManager.getBoard().getWinners() == null) {
      this.gameManager.getBoard().setWinner();
    }
    return isGameOver;
  }
}
