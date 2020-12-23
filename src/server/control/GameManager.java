package server.control;

import interfaces.Board;
import interfaces.CoordinateObject;
import interfaces.FeedableObject;
import interfaces.Food;
import interfaces.Player;
import interfaces.PlayerCell;
import interfaces.SpikeCell;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;
import server.model.BoardImpl;
import server.model.TeamImpl;
import server.remote.PlayerRemote;

/**
 * This class contains all the game's mechanics - treatment of client call from remote - game's
 * ticks - objects movements - objects collision - score update
 */
public class GameManager implements ActionListener {

  private PlayerRemote remoteManager;
  private PlayerManager playerManager;
  private Monitor monitor;

  private List<CoordinateObject> movingObjects;
  private List<Food> foodsToAdd;
  private List<Food> foodsToRemove;
  private List<SpikeCell> spikeToAdd;

  private Timer tm;
  private int gameTimer = 1000000;

  private Board board;

  /**
   * Main constructor - creation of the board with two teams - creation of all the lists for the
   * objects to update (from game mechanics or from events) - creation of the player manager and the
   * monitor
   */
  public GameManager() {
    board = new BoardImpl(500, 500, 300, 20);
    board.addTeam(new TeamImpl(new Color(255, 0, 0), "Rouge", 50, 400));
    board.addTeam(new TeamImpl(new Color(0, 0, 255), "Bleu", 750, 400));

    movingObjects = new ArrayList<>();
    foodsToAdd = new ArrayList<>();
    foodsToRemove = new ArrayList<>();
    spikeToAdd = new ArrayList<>();

    monitor = new Monitor();
    playerManager = new PlayerManager(monitor);

    tm = new Timer(16, this);
  }

  /**
   * Function call by the Timer every 16 milliseconds This function execute 1 server tick and apply
   * all game mechanics
   */
  @Override
  public void actionPerformed(ActionEvent arg0) {
    if (!this.gameOver()) {
      applyMovePhysic();
      checkCollision();
      playerManager.addWaitingPlayerCells();
      updateFoodList();
      updateSpikeList();
    }
    if (gameTimer > 0) {
      gameTimer -= 16;
      if (gameTimer < 0) {
        gameTimer = 0;
      }
    }
    // Reset timer for next Tick
    tm.start();
  }

  /**
   * Method call during a server's tick Allow to update the food list after the game mechanics, to
   * avoid to modify the list when we browse it (by adding a food with a client call or with a game
   * mechanic)
   */
  private void updateFoodList() {
    for (Food food : foodsToRemove) {
      board.removeFood(food);
      movingObjects.remove(food);
    }
    foodsToRemove.clear();

    if (foodsToAdd.size() > 0) {
      movingObjects.addAll(foodsToAdd);
      board.addFoods(foodsToAdd);
      foodsToAdd.clear();
    }
  }

  /**
   * Method call during a server's tick Allow to update the spike cell list without modifying the
   * list when we brows it
   */
  private void updateSpikeList() {
    for (SpikeCell spike : spikeToAdd) {
      board.addSpike(spike);
    }
    spikeToAdd.clear();
  }

  /**
   * Method call by the client to know the time left in the game
   *
   * @return The time left for the game
   */
  public int getTimer() {
    return gameTimer;
  }

  /**
   * Method to know if the game is over
   *
   * @return true if the game is over, false if not
   */
  public boolean gameOver() {
    return gameTimer <= 0;
  }

  /**
   * Method to add a player
   *
   * @param player : Player to add
   */
  public void addPlayer(Player player) {
    board.addPlayer(player);
  }

  /**
   * Method to add a spike, this spike will be add to the board at the and of a tick execution
   *
   * @param spike : Spike to add
   */
  public void addSpike(SpikeCell spike) {
    spikeToAdd.add(spike);
  }

  /**
   * Method to remove a player with a specific id from the board
   *
   * @param id : int id of the player to remove
   */
  public void removePlayer(int id) {
    board.removePlayer(board.getPlayer(id));
  }

  /**
   * Method to get the board
   *
   * @return the Board
   */
  public Board getBoard() {
    return board;
  }

  /**
   * Method to ask to move a player in a specific direction
   *
   * @param id : int id of the player to move
   * @param mouseX : float X position of the direction to take
   * @param mouseY : float Y position of the direction to take
   */
  public void sendMousePosition(int id, float mouseX, float mouseY) {
    playerManager.sendMousePosition(board.getPlayer(id), mouseX, mouseY);
  }

  /**
   * Method initialization of the server
   *
   * @param gui : graphical interface use to run the server
   * @return true if the server is correctly initiated
   */
  public boolean initServer() {
    try {
      LocateRegistry.createRegistry(1099);
      this.remoteManager = new PlayerRemote(this);
      Naming.rebind("rmi://localhost:1099/PLM", remoteManager);
      tm.start();
      LogManager.writeLog("Server Initialized");
    } catch (Exception e) {
      LogManager.writeLog("E01: Error initializing the server.");
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /** Method call during a server tick to check all the board collisions */
  private void checkCollision() {
    List<FeedableObject> cells = new ArrayList<>();
    for (Player player : board.getPlayers()) {
      if (player.isAlive()) {

        for (PlayerCell cell : player.getCells()) {
          checkBoardCollisionForFeedableObject(cell);
          checkFoodCollision(cell);
          cell.updateCooldown();
          cells.add(cell);
        }
      }
    }
    for (SpikeCell spike : board.getSpikeCells()) {
      checkBoardCollisionForFeedableObject(spike);
      checkFoodCollision(spike);
      cells.add(spike);
    }
    for (int i = 0; i < cells.size() - 1; i++) {
      FeedableObject cellA = cells.get(i);
      for (int j = i + 1; j < cells.size(); j++) {
        FeedableObject cellB = cells.get(j);

        if (cellA.collideWith(cellB)) {
          checkCellRepulsion(cellA, cellB);
        } else {
          checkCellEating(cellA, cellB);
        }
      }
    }
    for (CoordinateObject coordObj : movingObjects) {
      checkBoardCollisionForCoordinateObject(coordObj);
    }
  }

  /**
   * Method call during the general collision check This method is specific to collision with the
   * board bounds for CoordinateObjects
   *
   * @param coordObj : CoordinateObject who possibly collide with board's bounds
   */
  private void checkBoardCollisionForCoordinateObject(CoordinateObject coordObj) {
    float radius = coordObj.getRadius();
    if (coordObj.getX() < 0) {
      coordObj.setX(-coordObj.getX());
      coordObj.setInertiaX(-coordObj.getInertiaX());
    } else if (coordObj.getX() > board.getBoardWidth()) {
      coordObj.setX(coordObj.getX() - (coordObj.getX() - board.getBoardWidth()));
      coordObj.setInertiaX(-coordObj.getInertiaX());
    }
    if (coordObj.getY() < 0) {
      coordObj.setY(radius);
      coordObj.setInertiaY(-coordObj.getInertiaY());
    } else if (coordObj.getY() > board.getBoardHeight()) {
      coordObj.setY(coordObj.getY() - (coordObj.getY() - board.getBoardHeight()));
      coordObj.setInertiaY(-coordObj.getInertiaY());
    }
  }

  /**
   * Method call during the general collision check This method is specific to collision with the
   * board bounds for FeedableObjects
   *
   * @param cell : FeedableObject who possibly collide with board's bounds
   */
  private void checkBoardCollisionForFeedableObject(FeedableObject cell) {
    float radius = cell.getRadius();
    if (cell.getX() < radius) {
      cell.addRepulsionX((1 - (cell.getX() / radius)) * 1.01f);
      cell.setInertiaX(-cell.getInertiaX());
    }
    if (cell.getX() > board.getBoardWidth() - radius) {
      cell.addRepulsionX((((board.getBoardWidth() - cell.getX()) / radius) - 1) * 1.01f);
      cell.setInertiaX(-cell.getInertiaX());
    }
    if (cell.getY() < radius) {
      cell.addRepulsionY((1 - (cell.getY() / radius)) * 1.01f);
      cell.setInertiaY(-cell.getInertiaY());
    }
    if (cell.getY() > board.getBoardHeight() - radius) {
      cell.addRepulsionY((((board.getBoardHeight() - cell.getY()) / radius) - 1) * 1.01f);
      cell.setInertiaY(-cell.getInertiaY());
    }
  }

  /**
   * Method call during the general collision check when two cell collide If two FeedableObjects
   * collide then they repel each other
   *
   * @param cellA : first FeedableObject to repulse
   * @param cellB : second FeedableObject to repulse
   */
  private void checkCellRepulsion(FeedableObject cellA, FeedableObject cellB) {
    float distX = cellA.getX() - cellB.getX();
    float distY = cellA.getY() - cellB.getY();
    float dist = (float) Math.hypot(distX, distY);
    float radiusA = cellA.getRadius();
    float radiusB = cellB.getRadius();
    if (dist < radiusA + radiusB) {
      float superposition = radiusA + radiusB - dist;
      float proportionA = superposition / radiusA;
      float proportionB = superposition / radiusB;
      cellA.addRepulsionX(distX / dist * proportionA);
      cellA.addRepulsionY(distY / dist * proportionA);
      cellB.addRepulsionX(-distX / dist * proportionB);
      cellB.addRepulsionY(-distY / dist * proportionB);
    }
  }

  /**
   * Method call during the general collision check when two FeedableObject overlap
   *
   * @param cellA : first FeedableObject who will overlap
   * @param cellB : second FeedableObject who will overlap
   */
  private void checkCellEating(FeedableObject cellA, FeedableObject cellB) {
    FeedableObject bigger;
    FeedableObject smaller;
    if (cellA.getSize() > cellB.getSize()) {
      bigger = cellA;
      smaller = cellB;
    } else {
      bigger = cellB;
      smaller = cellA;
    }
    double dist = Math.hypot(bigger.getX() - smaller.getX(), bigger.getY() - smaller.getY());
    if (dist < bigger.getRadius()) {
      playerManager.tryEat(bigger, smaller, this);
    }
  }

  /**
   * Method call during the general collision This method check if the FeedableObject can eat a food
   *
   * @param cell : FeedableObject to feed
   */
  private void checkFoodCollision(FeedableObject cell) {
    for (Food food : board.getFoods()) {
      double dist = Math.hypot(cell.getX() - food.getX(), cell.getY() - food.getY());
      if (dist < cell.getRadius()) {
        playerManager.tryEat(cell, food, this);
      }
    }
  }

  /** Method call during a server's tick to apply move physics on all board objects */
  private void applyMovePhysic() {
    List<CoordinateObject> toRemove = new ArrayList<>();
    for (CoordinateObject moveObj : movingObjects) {
      moveObj.applyMouvement();
      if (moveObj.getSpeedX() == 0 && moveObj.getSpeedY() == 0) {
        toRemove.add(moveObj);
      }
    }
    movingObjects.removeAll(toRemove);
    for (Player player : board.getPlayers()) {
      if (player.isAlive()) {
        for (PlayerCell cell : player.getCells()) {
          cell.applyMouvement();
        }
      }
    }
    for (SpikeCell spike : board.getSpikeCells()) {
      spike.applyMouvement();
    }
  }

  /**
   * Method to remove a player cell from the board
   *
   * @param cell : PlayerCell to remove
   */
  public void removePlayerCell(PlayerCell cell) {
    Player player = cell.getPlayer();
    player.removeCell(cell);
    playerManager.addScore(player.getTeam(), -cell.getSize());
    if (player.getCells().size() <= 0) {
      playerManager.removePlayer(player);
    }
  }

  /**
   * Method to remove a SpikeCell from the Board
   *
   * @param cell : SpikeCell to remove
   */
  public void removeSpikeCell(SpikeCell cell) {
    board.removeSpike(cell);
  }

  /**
   * Method to remove a Food from the Board
   *
   * @param food : Food to remove
   */
  public void removeFood(Food food) {
    food.killFood();
    if (!food.isPersistent()) {
      foodsToRemove.add(food);
    }
  }

  /**
   * Method call on a client event, will ask to this player to throw food
   *
   * @param playerId : int id of the player
   * @param mouseX : float X direction to throw food
   * @param mouseY : float Y direction to throw food
   */
  public void throwFood(int playerId, float mouseX, float mouseY) {
    Player player = board.getPlayer(playerId);
    List<Food> foods = playerManager.throwFood(player, mouseX, mouseY);
    foodsToAdd.addAll(foods);
  }

  /**
   * Method call on a client event, will ask to this player to split himself
   *
   * @param playerId : int id of this player
   * @param mouseX : float X direction to split
   * @param mouseY : float Y direction to split
   */
  public void split(int playerId, float mouseX, float mouseY) {
    Player player = board.getPlayer(playerId);
    playerManager.split(player, mouseX, mouseY);
  }
}
