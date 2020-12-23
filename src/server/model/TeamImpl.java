package server.model;

import interfaces.Player;
import interfaces.Team;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/** Implementation of the Team Interface */
public class TeamImpl implements Team {

  private static final long serialVersionUID = 1L;

  private List<Player> players;
  private int score;
  private Color color;
  private String teamName;

  private int spawnX, spawnY;

  private final transient Object bell;

  /**
   * Team main constructor
   *
   * @param color : team color
   * @param teamName : team name
   * @param spawnX : team spawn position on X coordinate
   * @param spawnY : team spawn position on Y coordinate
   */
  public TeamImpl(Color color, String teamName, int spawnX, int spawnY) {
    this.color = color;
    this.score = 0;
    this.players = new ArrayList<>();
    this.spawnX = spawnX;
    this.spawnY = spawnY;
    this.teamName = teamName;
    bell = new Object();
  }

  /**
   * Method to get the team color
   *
   * @return color
   */
  public Color getColor() {
    return color;
  }

  /**
   * Method to add a player to the team
   *
   * @param player : new player
   */
  public void addPlayer(Player player) {
    players.add(player);
  }

  /**
   * Method to remove a player to the team
   *
   * @param player : player to remove
   * @return true if the player is remove, false if not
   */
  public boolean removePlayer(Player player) {
    return players.remove(player);
  }

  /** Method to get the player list of the Team return player list */
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Method to get the team score
   *
   * @return score
   */
  public int getScore() {
    return score;
  }

  /**
   * Method to modify the team score
   *
   * @param amount : amount to add to the score (positive or negative)
   */
  public void addToScore(int amount) {
    score += amount;
  }

  /**
   * Method to get the Team spawn position, on X coordinate
   *
   * @return X coordinate
   */
  public int getSpawnX() {
    return spawnX;
  }

  /**
   * Method to get the Team spawn position, on Y coordinate
   *
   * @return Y coordinate
   */
  public int getSpawnY() {
    return spawnY;
  }

  /**
   * Method to get the team name
   *
   * @return name
   */
  public String getTeamName() {
    return this.teamName;
  }

  /**
   * Method to get the Tea "Bell" The bell is an object use to synchronized modifications on Team
   * score
   */
  public Object getBell() {
    return bell;
  }
}
