package interfaces;

import java.awt.Color;
import java.io.Serializable;
import java.rmi.Remote;
import java.util.List;

public interface Team extends Serializable, Remote {

  static final long serialVersionUID = 1L;

  public String getTeamName();

  public Color getColor();

  public int getScore();

  public void addPlayer(Player player);

  public List<Player> getPlayers();

  public boolean removePlayer(Player player);

  public void addToScore(int amount);

  public int getSpawnX();

  public int getSpawnY();

  public Object getBell();
}
