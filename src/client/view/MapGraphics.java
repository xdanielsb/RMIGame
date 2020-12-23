package client.view;

import client.displayer.BoardDisplayer;
import client.displayer.HeaderHandlerDisplayer;
import client.displayer.VictoryDisplayer;
import interfaces.Board;
import interfaces.IPlayerRemote;
import interfaces.Player;
import interfaces.PlayerCell;
import java.rmi.RemoteException;
import processing.core.PApplet;

public class MapGraphics extends PApplet {

  private IPlayerRemote rm;
  private Board board;
  private final int id;
  private float centerX;
  private float centerY;
  private HeaderHandler header;
  private Player player;
  private boolean gameOver;

  private boolean askForThrow;
  private boolean askForSplit;

  private BoardDisplayer boardDisplayer;

  public MapGraphics(IPlayerRemote distant, String username) throws RemoteException {
    header = new HeaderHandler();

    this.rm = distant;
    this.id = rm.registerPlayer(username);
    this.board = rm.getBoard();
    this.player = board.getPlayer(id);

    askForThrow = false;
    askForSplit = false;

    Runtime runtime = Runtime.getRuntime();
    Runnable runnable =
        () -> {
          try {
            rm.removePlayer(id);
          } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        };
    runtime.addShutdownHook(new Thread(runnable));
  }

  public void settings() {
    size(1280, 720);
    centerX = width / 2;
    centerY = height / 2;
    this.boardDisplayer =
        new BoardDisplayer(Math.min(width, height), 0.1f, PlayerCell.CELL_MIN_SIZE, 0.5f, 20000);
  }

  public void setup() {
    surface.setTitle("Agar IO");
  }

  public void draw() {
    background(230);
    cursor(CROSS);

    try {
      update();
      if (!this.gameOver) {

        boardDisplayer.draw(board, this.player, this.centerX, this.centerY, this);

        HeaderHandlerDisplayer.draw(this.header, this);
      } else {
        VictoryDisplayer.draw(this.board.getWinners(), this);
      }
    } catch (RemoteException e1) {
      e1.printStackTrace();
    }
  }

  /**
   * Updates the differents objects needed via the interface IPlayerRemote to make the client works.
   */
  public void update() throws RemoteException {
    this.gameOver = rm.gameOver();

    try {
      this.board = rm.getBoard();
    } catch (RemoteException e) {
      e.printStackTrace();
    }

    if (this.board.getPlayer(id).isAlive()) this.player = this.board.getPlayer(this.id);

    this.header.update(rm.getTimer(), this.board.getTeams());

    float modifyMouseX = mouseX - (centerX) + player.getX();
    float modifyMouseY = mouseY - (centerY) + player.getY();

    try {
      rm.sendMousePosition(id, modifyMouseX, modifyMouseY);
    } catch (RemoteException e) {
      e.printStackTrace();
    }

    if (askForThrow) {
      try {
        rm.throwFood(this.id, modifyMouseX, modifyMouseY);
        askForThrow = false;
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }

    if (askForSplit) {
      try {
        rm.split(this.id, modifyMouseX, modifyMouseY);
        askForSplit = false;
      } catch (RemoteException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Override of the function mousePressed of Processing. It trigger on the click of any mouth
   * button. We handle the click by checking the last button clicked.
   */
  @Override
  public void mousePressed() {
    if (mouseButton == LEFT) {
      askForSplit = true;
    } else if (mouseButton == RIGHT) {
      askForThrow = true;
    }
  }
}
