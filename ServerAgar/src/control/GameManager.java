package control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import model.Board;
import model.Food;
import model.Player;
import model.Team;
import remote.PlayerRemote;
import view.ServerGUI;

public class GameManager implements ActionListener {
	
	private PlayerRemote remoteManager;
	private PlayerManager playerManager;
	private ServerGUI gui;
	
	private Timer tm;
	float gameTimer = 180;
	
	private Board board;

	public GameManager() {
		board = new Board(1500, 1500, 150);
		board.addTeam(new Team(0, new Color(255, 0, 0), 50, 400));
		board.addTeam(new Team(1, new Color(0, 0, 255), 750, 400));
		
		playerManager = new PlayerManager(board);
		
		tm = new Timer(25, this);
		tm.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		checkFoodCollision();
		checkPlayerCollision();
		gameTimer -= 0.025;
		// Reset timer for next Tick
		tm.start();
	}

	public float getTimer() throws RemoteException {
		return gameTimer <= 0 ? 0 : gameTimer;
	}

	public boolean gameOver() {
		return gameTimer <= 0;
	}
	
	public void addPlayer(Player player) {
		board.addPlayer(player);
	}
	
	public void removePlayer(int id) {
		board.removePlayer(board.getPlayer(id));
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void sendMousePosition(int id, double mouseX, double mouseY) {
		playerManager.move(board.getPlayer(id), mouseX, mouseY);
	}

	public boolean initServer(ServerGUI gui) {
		try {
			this.gui = gui;
			LocateRegistry.createRegistry(1099);
			this.remoteManager = new PlayerRemote(this);
			Naming.rebind("rmi://localhost:1099/PLM", remoteManager);
		} catch (Exception e) {
			System.out.println("E01: Error initializing the server.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public ServerGUI getGUI() {
		return this.gui;
	}
	
	private void checkPlayerCollision() {
		List<Player> team1, team2;
		team1 = playerManager.getPlayersTeam(0);
		team2 = playerManager.getPlayersTeam(1);
		for( Player pteam1: team1) {
			if(!pteam1.isAlive()) continue;
			for(Player pteam2: team2) {
				if(!pteam2.isAlive()) continue;
				if( (pteam1.dist(pteam2) < Math.max(pteam1.getSize()/4, pteam2.getSize()/4)) && Math.abs(pteam1.getSize()-pteam2.getSize()) >= 10) {
					if (pteam1.getSize() < pteam2.getSize()) {
						//pteam1.setAlive(false);
						pteam2.setSize(Math.sqrt((pteam2.getSize() / 2) * (pteam2.getSize() / 2)
								+ (pteam1.getSize() / 2) * (pteam1.getSize() / 2)) * 2);
						updateScore(pteam2, (int) pteam2.getSize());
						playerManager.removePlayer(pteam1);
						break;
					}else {
						//pteam2.setAlive(false);
						pteam1.setSize(Math.sqrt((pteam2.getSize() / 2) * (pteam2.getSize() / 2)
								+ (pteam1.getSize() / 2) * (pteam1.getSize() / 2)) * 2);
						updateScore(pteam1, (int) pteam1.getSize());
						playerManager.removePlayer(pteam2);
					}
				}
			}
		}
	}


	private void updateScore(Player p, int amount) {
		p.getTeam().addToScore(amount);
	}

	private void checkFoodCollision() {
		List<Food> eatenFood = new ArrayList<>();
		for (Player p : board.getPlayers()) {
			double size = p.getSize() / 2;
			for (Food f : board.getFoods()) {
				if(f.isAlive()) {
					double dx = p.getX() - f.getX();
					double dy = p.getY() - f.getY();
					double length = Math.sqrt((dx * dx) + (dy * dy));
					if (length < size) {
						p.setSize(Math.sqrt(
								(p.getSize() / 2) * (p.getSize() / 2) + 
								(f.getSize() / 2) * (f.getSize() / 2)) * 2
								);
						updateScore(p, (int) f.getSize());
						eatenFood.add(f);						
					}					
				}
			}
			board.removeFood(eatenFood);
			eatenFood.clear();
		}
	}
	
}
