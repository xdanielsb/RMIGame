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
import model.CoordinateObject;
import model.FeedableObject;
import model.Food;
import model.Player;
import model.PlayerCell;
import model.Team;
import remote.PlayerRemote;
import view.ServerGUI;

public class GameManager implements ActionListener {

	private PlayerRemote remoteManager;
	private PlayerManager playerManager;
	private Monitor monitor;
	private ServerGUI gui;

	private List<CoordinateObject> movingObjects;
	private List<Food> foodsToAdd;
	private List<Food> foodsToRemove;

	private Timer tm;
	private int gameTimer = 1000000;

	private Board board;

	public GameManager() {
		board = new Board(500, 500, 300);
		board.addTeam(new Team(new Color(255, 0, 0), "Rouge", 50, 400));
		board.addTeam(new Team(new Color(0, 0, 255), "Bleu", 750, 400));

		movingObjects = new ArrayList<>();
		foodsToAdd = new ArrayList<>();
		foodsToRemove = new ArrayList<>();

		monitor = new Monitor(board);
		playerManager = new PlayerManager(monitor);

		tm = new Timer(16, this);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(!this.gameOver()) {
			applyMovePhysic();
			checkCollision();
			playerManager.addWaitingPlayerCells();
			mergeNewFoods();
			removeFoods();
		}
		if(gameTimer > 0) {
			gameTimer -= 16;
			if(gameTimer < 0) {
				gameTimer = 0;
			}
		}
		// Reset timer for next Tick
		tm.start();
	}

	private void removeFoods() {
		for(Food food : foodsToRemove) {
			board.removeFood(food);
			movingObjects.remove(food);
		}
		foodsToRemove.clear();
	}

	private void mergeNewFoods() {
		if(foodsToAdd.size() > 0) {			
			movingObjects.addAll(foodsToAdd);
			board.addFoods(foodsToAdd);
			foodsToAdd.clear();
		}
		
	}

	public float getTimer() throws RemoteException {
		return gameTimer;
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

	public void sendMousePosition(int id, float mouseX, float mouseY) {
		playerManager.sendMousePosition(board.getPlayer(id), mouseX, mouseY);
	}

	public boolean initServer(ServerGUI gui) {
		try {
			this.gui = gui;
			LocateRegistry.createRegistry(1099);
			this.remoteManager = new PlayerRemote(this);
			Naming.rebind("rmi://localhost:1099/PLM", remoteManager);
			tm.start();
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

	private void checkCollision() {
		List<PlayerCell> cells = new ArrayList<>();
		for(Player player : board.getPlayers()) {
			if(player.isAlive()) {
				
				for(PlayerCell cell : player.getCells()) {
					checkBoardCollisionForFeedableObject(cell);
					checkFoodCollision(cell);
					cell.updateCooldown();
					cells.add(cell);
				}
				
			}
		}
		for(int i = 0; i < cells.size() - 1; i++) {
			PlayerCell cellA = cells.get(i);
			for(int j = i+1; j < cells.size(); j++) {
				PlayerCell cellB = cells.get(j);
				
				if(cellA.getPlayer().getTeam() == cellB.getPlayer().getTeam()) {
					if(cellA.getPlayer() == cellB.getPlayer()) {
						if(cellA.getCooldown() <= 0 && cellB.getCooldown() <= 0) {							
							checkCellEating(cellA, cellB);
						} else {
							checkCellRepulsion(cellA, cellB);												
						}
					} else {						
						checkCellRepulsion(cellA, cellB);					
					}
				} else {
					checkCellEating(cellA, cellB);
				}
				
			}
		}
		for(CoordinateObject coordObj : movingObjects) {
			checkBoardCollisionForCoordinateObject(coordObj);
		}
	}
	
	private void checkBoardCollisionForCoordinateObject(CoordinateObject coordObj) {
		float radius = coordObj.getRadius();
		if(coordObj.getX() < 0) {
			coordObj.setX(-coordObj.getX());
			coordObj.setInertiaX(-coordObj.getInertiaX());
		}else if(coordObj.getX() > board.getBoardWidth()) {
			coordObj.setX(coordObj.getX() - (coordObj.getX() - board.getBoardWidth()));
			coordObj.setInertiaX(-coordObj.getInertiaX());
		}
		if(coordObj.getY() < 0) {
			coordObj.setY(radius);
			coordObj.setInertiaY(-coordObj.getInertiaY());
		}else if(coordObj.getY() > board.getBoardHeight()) {
			coordObj.setY(coordObj.getY() - (coordObj.getY() - board.getBoardHeight()));
			coordObj.setInertiaY(-coordObj.getInertiaY());
		}
	}

	private void checkBoardCollisionForFeedableObject(FeedableObject cell) {
		float radius = cell.getRadius();
		if(cell.getX() < radius) {
			cell.addRepulsionX((1-(cell.getX()/radius))*1.01f);
			cell.setInertiaX(-cell.getInertiaX());
		}
		if(cell.getX() > board.getBoardWidth() - radius) {
			cell.addRepulsionX((((board.getBoardWidth() - cell.getX())/radius)-1)*1.01f);
			cell.setInertiaX(-cell.getInertiaX());
		}
		if(cell.getY() < radius) {
			cell.addRepulsionY((1-(cell.getY()/radius))*1.01f);
			cell.setInertiaY(-cell.getInertiaY());
		}
		if(cell.getY() > board.getBoardHeight() - radius) {
			cell.addRepulsionY((((board.getBoardHeight() - cell.getY())/radius)-1)*1.01f);
			cell.setInertiaY(-cell.getInertiaY());
		}
	}

	private void checkCellRepulsion(PlayerCell cellA, PlayerCell cellB){			
		float distX = cellA.getX() - cellB.getX();
		float distY = cellA.getY() - cellB.getY();
		float dist = (float)Math.hypot(distX, distY);
		float radiusA = cellA.getRadius();
		float radiusB = cellB.getRadius();
		if(dist < radiusA+radiusB) {				
			float superposition = radiusA+radiusB - dist;
			float proportionA = superposition/radiusA;
			float proportionB = superposition/radiusB;
			cellA.addRepulsionX(distX/dist*proportionA);
			cellA.addRepulsionY(distY/dist*proportionA);
			cellB.addRepulsionX(-distX/dist*proportionB);
			cellB.addRepulsionY(-distY/dist*proportionB);
		}
	}

	private void checkCellEating(PlayerCell cellA, PlayerCell cellB) {
		PlayerCell bigger;
		PlayerCell smaller;
		if(cellA.getSize() > cellB.getSize()) {
			bigger = cellA;
			smaller = cellB;
		} else {
			bigger = cellB;
			smaller = cellA;
		}
		if(smaller.getSize() < bigger.getSize()*0.98) {
			double dist = Math.hypot(
				bigger.getX() - smaller.getX(),
				bigger.getY() - smaller.getY()
			);
			if(dist < bigger.getRadius()) {
				bigger.eat(smaller);
				playerManager.addScore(bigger.getPlayer().getTeam(), smaller.getSize());
				removePlayerCell(smaller);
			}
		}
	}

	private void checkFoodCollision(PlayerCell cell){
		for(Food food : board.getFoods()) {
			double dist = Math.hypot(
				cell.getX() - food.getX(),
				cell.getY() - food.getY()
			);
			if(dist < cell.getRadius()) {
				cell.eat(food);
				playerManager.addScore(
					cell.getPlayer().getTeam(),
					food.getSize()
				);
				this.removeFood(food);
			}
		}
	}

	private void applyMovePhysic() {
		List<CoordinateObject> toRemove = new ArrayList<>();
		for(CoordinateObject moveObj : movingObjects) {
			moveObj.applyMouvement();
			if(moveObj.getSpeedX() == 0 && moveObj.getSpeedY() == 0) {
				toRemove.add(moveObj);
			}
		}
		movingObjects.removeAll(toRemove);
		for(Player player : board.getPlayers()) {
			if(player.isAlive()) {
				for(PlayerCell cell : player.getCells()) {
					cell.applyMouvement();
				}
			}
		}
	}

	private void removePlayerCell(PlayerCell cell) {
		Player player = cell.getPlayer();
		player.removeCell(cell);
		playerManager.addScore(player.getTeam(), -cell.getSize());
		if(player.getCells().size() <= 0) {
			playerManager.removePlayer(player);
		}
	}

	private void removeFood(Food food) {
		food.killFood();
		if(!food.isPersistent()) {
			foodsToRemove.add(food);
		}
	}
	
	public void throwFood(int playerId, float mouseX, float mouseY) {
		Player player = board.getPlayer(playerId);
		List<Food> foods = playerManager.throwFood(player, mouseX, mouseY);
		foodsToAdd.addAll(foods);
	}
	
	public void split(int playerId, float mouseX, float mouseY) {
		Player player = board.getPlayer(playerId);
		playerManager.split(player, mouseX, mouseY);
	}

}
