package server.control;

import interfaces.CoordinateObject;
import interfaces.FeedableObject;
import interfaces.Food;
import interfaces.Player;
import interfaces.PlayerCell;
import interfaces.SpikeCell;
import interfaces.Team;
import java.util.ArrayList;
import java.util.List;
import server.model.FoodImpl;
import server.model.PlayerCellImpl;
import server.model.SpikeCellImpl;

/** Class who will handle specifically the player's mechanics */
public class PlayerManager {

  private Monitor monitor;

  private List<PlayerCell> cellsToAdd;

  /**
   * Main constructor of the PlayerManager
   *
   * @param monitor : Monitor for synchronized variables
   */
  public PlayerManager(Monitor monitor) {
    this.monitor = monitor;
    cellsToAdd = new ArrayList<>();
  }

  /**
   * Method to modify the score of a team
   *
   * @param team : Team who will have it score modified
   * @param amount : int value to add to the team score (positive or negative)
   */
  public void addScore(Team team, int amount) {
    // non bloquant pour l'appelant
    Runnable runnable =
        () -> {
          monitor.addScore(team, amount);
        };
    new Thread(runnable).start();
  }

  /**
   * Method to ask for a player movement in a specific direction
   *
   * @param player : Player to move
   * @param mouseX : float X direction
   * @param mouseY : float Y direction
   */
  public void sendMousePosition(Player player, float mouseX, float mouseY) {
    if (player.isAlive()) {
      for (PlayerCell cell : player.getCells()) {
        moveToward(cell, mouseX, mouseY);
      }
    }
  }

  /**
   * Method to make a player throw food
   *
   * @param player : Player who will throw some food
   * @param mouseX : float X direction to throw
   * @param mouseY : float Y direction to throw
   * @return List of the throwing Food
   */
  public List<Food> throwFood(Player player, float mouseX, float mouseY) {
    List<Food> foods = new ArrayList<>();
    for (PlayerCell cell : player.getCells()) {
      if (cell.getSize() >= PlayerCell.MIN_THROWING_FOOD_SIZE) {
        double distX = mouseX - cell.getX();
        double distY = mouseY - cell.getY();
        double dist = Math.hypot(distX, distY);

        Food food = new FoodImpl(cell, (float) (distX / dist), (float) (distY / dist));
        foods.add(food);

        cell.setSize(cell.getSize() - food.getSize());
        monitor.addScore(cell.getPlayer().getTeam(), -food.getSize());
      }
    }
    return foods;
  }

  /**
   * Method to split a player
   *
   * @param player : Player to split
   * @param mouseX : float X direction to split
   * @param mouseY : float Y direction to split
   */
  public void split(Player player, float mouseX, float mouseY) {
    for (PlayerCell cell : player.getCells()) {
      if (cell.getSize() >= PlayerCell.MIN_SPLITTING_SIZE) {
        double distX = mouseX - cell.getX();
        double distY = mouseY - cell.getY();
        double dist = Math.hypot(distX, distY);
        PlayerCell newCell =
            new PlayerCellImpl(
                cell, cell.getSize() / 2, (float) (distX / dist), (float) (distY / dist));
        cellsToAdd.add(newCell);
      }
    }
  }

  /**
   * Method to make a cell explosion
   *
   * @param cell : PlayerCell to explode
   */
  public void playerCellExplosion(PlayerCell cell) {
    float ratio = SpikeCell.EXPLOSION_INITIAL_RATIO;
    int initialSize = cell.getSize();

    for (int i = 0; i < SpikeCell.EXPLOSION_NUMBER; i++) {
      if (cell.getSize() < PlayerCell.MIN_SPLITTING_SIZE) {
        break;
      }
      int size = Math.max((int) (initialSize * ratio), PlayerCell.CELL_MIN_SIZE);
      double angle = Math.random() * Math.toRadians(360);
      cell.getPlayer()
          .addCell(
              new PlayerCellImpl(cell, size, (float) Math.cos(angle), (float) Math.sin(angle)));
      ratio /= 2;
    }
  }

  /**
   * Method to update the list of cell of a player This method allow an update of the player's list
   * at the end of the server tick to avoid list modification while a list browse
   */
  public void addWaitingPlayerCells() {
    for (PlayerCell cell : cellsToAdd) {
      cell.getPlayer().addCell(cell);
    }
    cellsToAdd.clear();
  }

  /**
   * Method to apply a move on a specific PlayerCell
   *
   * @param cell : PlayerCell to move
   * @param mouseX : float X direction
   * @param mouseY : float Y direction
   */
  public void moveToward(PlayerCell cell, float mouseX, float mouseY) {
    if (cell != null) {
      float distX = mouseX - cell.getX();
      float distY = mouseY - cell.getY();
      float dist = (float) Math.hypot(distX, distY);
      if (dist > 10) {
        cell.setMovementX(distX / dist);
        cell.setMovementY(distY / dist);
      } else {
        cell.setMovementX(0);
        cell.setMovementY(0);
      }
    }
  }

  /**
   * Method to make a kill a player and make him respawn after 3 seconds
   *
   * @param p : Player to kill
   */
  public void removePlayer(Player p) {
    p.setAlive(false);
    Runnable runnable =
        () -> {
          try {
            Thread.sleep(3000);
            p.addCell(new PlayerCellImpl(p, PlayerCell.CELL_MIN_SIZE));
            p.setAlive(true);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        };
    new Thread(runnable).start();
  }

  /**
   * Method to make a FeedableObject eat a CoordinateObject This method will act differently for
   * each different couple of Feedable and Coordinate objects This method contains all instanceof
   * necessary to redirect to the specific behavior
   *
   * @param eater : FeedableObject to feed
   * @param eated : CoordinateObject to eat
   * @param manager : gameManager for board update
   */
  public void tryEat(FeedableObject eater, CoordinateObject eated, GameManager manager) {
    if (eater instanceof PlayerCell) {
      PlayerCell cell = (PlayerCell) eater;
      if (eated instanceof Food) {
        playerTryToEatFood(cell, (Food) eated, manager);
      } else if (eated instanceof PlayerCell) {
        playerTryToEatPlayer(cell, (PlayerCell) eated, manager);
      } else if (eated instanceof SpikeCell) {
        playerTryToEatSpike(cell, (SpikeCell) eated, manager);
      }
    } else if (eater instanceof SpikeCell) {
      if (eated instanceof Food) {
        spikeTryToEatFood((SpikeCell) eater, (Food) eated, manager);
      }
    }
  }

  /**
   * Method to make a Player eat a Food
   *
   * @param eater : Player to feed
   * @param eated : Food to eat
   * @param manager : gameManager for board update
   */
  private void playerTryToEatFood(PlayerCell eater, Food eated, GameManager manager) {
    eater.eat(eated);
    addScore(eater.getPlayer().getTeam(), eated.getSize());
    manager.removeFood(eated);
  }

  /**
   * Method to make a Player eat a Player
   *
   * @param eater : Player to feed
   * @param eated : player to eat
   * @param manager : gameManager for board update
   */
  private void playerTryToEatPlayer(PlayerCell eater, PlayerCell eated, GameManager manager) {
    if (eater.getPlayer() == eated.getPlayer() || eated.getSize() < eater.getSize() * 0.98) {
      eater.eat(eated);
      addScore(eater.getPlayer().getTeam(), eated.getSize());
      manager.removePlayerCell(eated);
    }
  }

  /**
   * Method to make a Player eat a SpikeCell
   *
   * @param eater : Player to feed
   * @param eated : SpikeCell to eat
   * @param manager : gameManager for board update
   */
  public void playerTryToEatSpike(PlayerCell eater, SpikeCell eated, GameManager manager) {
    if (eated.getSize() < eater.getSize() * 0.98) {
      playerCellExplosion(eater);
      manager.removeSpikeCell(eated);
    }
  }

  /**
   * Method to make a SpikeCell eat a Food
   *
   * @param eater : SpikeCell to feed
   * @param eated : Food to eat
   * @param manager : gameManager for board update
   */
  public void spikeTryToEatFood(SpikeCell eater, Food eated, GameManager manager) {
    eater.eat(eated);
    if (eater.getSize() > SpikeCell.MAX_SPIKE_SIZE) {
      double dist = Math.hypot(eated.getInertiaX(), eated.getInertiaY());
      manager.addSpike(
          new SpikeCellImpl(
              eater,
              eater.getSize() / 2,
              (float) (eated.getInertiaX() / dist),
              (float) (eated.getInertiaY() / dist)));
    }
    manager.removeFood(eated);
  }
}
