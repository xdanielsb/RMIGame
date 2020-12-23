package server.model;

import interfaces.Board;
import interfaces.Food;
import interfaces.Player;
import interfaces.SpikeCell;
import interfaces.Team;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Server implementation of the Board Interface */
public class BoardImpl implements Board {

  private static final long serialVersionUID = 1L;

  private List<Food> foods;
  private List<SpikeCell> spikes;

  private Map<Integer, Player> players;
  private List<Team> teams;

  private int boardHeight, boardWidth;

  private Team winners;

  /**
   * Board main constructor
   *
   * @param boardHeight : board width (X coordinate)
   * @param boardWidth : board height (Y coordinate)
   * @param nbFood : number of food on the board
   * @param nbSpike : initial number of SpikeCell on the board
   */
  public BoardImpl(int boardWidth, int boardHeight, int nbFood, int nbSpike) {
    foods = new ArrayList<>();
    spikes = new ArrayList<>();
    players = new HashMap<>();
    teams = new ArrayList<>();

    this.boardHeight = boardHeight;
    this.boardWidth = boardWidth;

    players = new HashMap<>();

    for (int i = 0; i < nbFood; i++) {
      foods.add(new FoodImpl(this));
    }

    for (int i = 0; i < nbSpike; i++) {
      spikes.add(new SpikeCellImpl(this));
    }

    winners = null;
  }

  /**
   * Method to get Board Width (X coordinate)
   *
   * @return board width
   */
  public int getBoardWidth() {
    return boardWidth;
  }

  /**
   * Method to get Board Height (Y coordinate)
   *
   * @return board height
   */
  public int getBoardHeight() {
    return boardHeight;
  }

  /**
   * Method to get team list of the board
   *
   * @return list of team
   */
  public List<Team> getTeams() {
    return teams;
  }

  /**
   * Method to add a Team to the team list
   *
   * @param team : TEam to add
   */
  public void addTeam(Team team) {
    teams.add(team);
  }

  /**
   * Method to remove a Player to the Board
   *
   * @param player : Player to remove
   */
  public void removePlayer(Player player) {
    player.getTeam().removePlayer(player);
    players.remove(player.getId());
  }

  /**
   * Method to get a player from the Board
   *
   * @param id : int id of the player to return
   * @return the player with this specific id
   */
  public Player getPlayer(int id) {
    return players.get(id);
  }

  /**
   * Method to get all the Players of the Board
   *
   * @return a Collection with all the players
   */
  public Collection<Player> getPlayers() {
    return players.values();
  }

  /**
   * Method to add a Player to the Board
   *
   * @param player : Player to add
   */
  public void addPlayer(Player player) {
    players.put(player.getId(), player);
    if (teams.size() <= 0) {
      return;
    }
    Team minTeam = null;
    int min = Integer.MAX_VALUE;
    for (Team t : teams) {
      if (t.getPlayers().size() < min) {
        min = t.getPlayers().size();
        minTeam = t;
      }
    }
    minTeam.addPlayer(player);
    player.setTeam(minTeam);
  }

  /**
   * Method to get the winner of the party
   *
   * @return the winning Team if the game is over, null if not
   */
  public Team getWinners() {
    return winners;
  }

  /** Method to ask to the board to chose is winning Team */
  public void setWinner() {
    Team winning = null;
    int higherScore = -1;
    for (Team team : teams) {
      if (team.getScore() > higherScore) {
        winning = team;
        higherScore = winning.getScore();
      }
    }
    this.winners = winning;
  }

  /**
   * Method to get all the foods on the Board
   *
   * @return the food list
   */
  public List<Food> getFoods() {
    return foods;
  }

  /**
   * Method to add some Foods on the Board
   *
   * @param foods : food list to add
   */
  public void addFoods(List<Food> foods) {
    this.foods.addAll(foods);
  }

  /**
   * Method to remove a Food from the Board
   *
   * @param food : the Food to remove
   * @return true if the remove work, false if not
   */
  public boolean removeFood(Food food) {
    return foods.remove(food);
  }

  /**
   * Method to add a SpikeCell to the Board
   *
   * @param spike : SpikeCell to add
   */
  public void addSpike(SpikeCell spike) {
    this.spikes.add(spike);
  }

  /**
   * Method to remove a SpikeCell to the Board
   *
   * @param spike : SpikeCell to remove
   * @return true if the remove work, false if not
   */
  public boolean removeSpike(SpikeCell spike) {
    return spikes.remove(spike);
  }

  /**
   * Method to get all Spike cells from the Board
   *
   * @return the SpikeCell list
   */
  public List<SpikeCell> getSpikeCells() {
    return spikes;
  }
}
