package metier;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {
	private List<Player> players;
	 
	
	public PlayerManager()
	{
		players = new ArrayList<>();
	}
	
	public void addPlayer(Player p)
	{
		players.add(p);
	}
	
	public List<Player> listAllPlayers()
	{
		return players;
	}
	
	public int getPlayerNumber()
	{
		return players.size();
	}
	
	/*public Player getPlayer(int id)
	{
		
	}*/
	
	public void Move(int id, double x, double y)
	{
		for(Player p:players)
		{
			if(p.getPlayerID() == id)
			{
				p.setX(x);
				p.setY(y);
				return;
			}
		}
	}
}
