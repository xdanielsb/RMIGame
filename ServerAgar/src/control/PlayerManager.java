package control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Player;

public class PlayerManager {
	// private List<Player> players;
	private Map<Integer, Player> players;
	Monitor monitor;
	int total = 0;

	public PlayerManager() {
		monitor = new Monitor();
		players = new HashMap<Integer, Player>();
	}

	public int getTeamOne() {
		return monitor.getTeamNum(0);
	}

	public int getTeamtwo() {
		return monitor.getTeamNum(1);
	}

	public int getScore(int teamID) {
		return teamID == 0 ? monitor.getScoreOne() : monitor.getScoreTwo();
	}

	public void addScore(int teamID, int amount) {
		// non bloquant pour l'appelant
		Runnable runnable = () -> {
			if (teamID == 0)
				monitor.addScoreOne(amount);
			else
				monitor.addScoreTwo(amount);
		};

		new Thread(runnable).start();

	}

	public void addPlayer(Player p) {
		total++;
		players.put(p.getId(), p);
		if (p.getTeam() == 0) {
			monitor.setTeamNumber(0,1);
		} else {
			monitor.setTeamNumber(1,1);
		}
	}

	public void erasePlayer(int id)
	{
		int team = getPlayer(id).getTeam();
		monitor.setTeamNumber(team,-1);
		players.remove(id);
	}

	public Collection<Player> getPlayers() {
		return players.values();
	}

	public List<Player> getPlayersTeam(int team){
		List<Player> pteam = new ArrayList<Player>();
		for(Player p: players.values()) {
			if(p.getTeam() == team) pteam.add(p);
		}
		return pteam;
	}

	public int getPlayerNumber() {
		return total;
	}

	public Player getPlayer(int id) {
		return players.get(id);
	}

	public void move(int id, double x, double y) {
		Player p = players.get(id);
		if (p.getId() == id && p.isAlive()) {
			p.setX(x);
			p.setY(y);
		}
	}

	private void resetPosition(Player p) {
		p.setX(p.getTeam() == 0 ? 50 : 750);
		p.setY(400);
	}

	public void removePlayer(Player p) {
		/*
		 * if (p.getTeamID() == 0) nbTone--; else nbTtwo--; players.remove(p);
		 */
		// Move(p.getPlayerID(), p.getTeamID()==0? 50:750, 400);
		p.setAlive(false);
		p.setSize(0);
		Runnable runnable = () -> {
			try {
				Thread.sleep(3000);
				// Move(p.getPlayerID(), p.getTeamID()==0? 50:750, 400);
				resetPosition(p);
				p.setSize(50);
				p.setAlive(true);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		new Thread(runnable).start();
	}
}
