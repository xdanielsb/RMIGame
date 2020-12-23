package interfaces;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.Collection;
import java.util.List;

public interface Board extends Serializable, Remote {

  static final long serialVersionUID = 1L;

  public int getBoardWidth();

  public int getBoardHeight();

  public void addTeam(Team team);

  public Player getPlayer(int id);

  public void addPlayer(Player player);

  public void removePlayer(Player player);

  public Collection<Player> getPlayers();

  public List<Team> getTeams();

  public List<Food> getFoods();

  public List<SpikeCell> getSpikeCells();

  public Team getWinners();

  public void setWinner();

  public void addFoods(List<Food> foods);

  public boolean removeFood(Food food);

  public void addSpike(SpikeCell spike);

  public boolean removeSpike(SpikeCell spike);
}
