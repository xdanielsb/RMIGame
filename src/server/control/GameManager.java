package server.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import interfaces.Board;
import interfaces.CoordinateObject;
import interfaces.FeedableObject;
import interfaces.Food;
import interfaces.Player;
import interfaces.PlayerCell;
import interfaces.SpikeCell;
import server.model.BoardImpl;
import server.model.TeamImpl;
import server.remote.PlayerRemote;
import server.view.ServerGUI;

public class GameManager implements ActionListener {

	private PlayerRemote remoteManager;
	private PlayerManager playerManager;
	private Monitor monitor;
	private ServerGUI gui;

	private List<CoordinateObject> movingObjects;
	private List<Food> foodsToAdd;
	private List<Food> foodsToRemove;
	private List<SpikeCell> spikeToAdd;

	private Timer tm;
	private int gameTimer = 1000000;

	private Board board;

	public GameManager() {
		board = new BoardImpl(500, 500, 300, 20);
		board.addTeam(new TeamImpl(new Color(255, 0, 0), "Rouge", 50, 400));
		board.addTeam(new TeamImpl(new Color(0, 0, 255), "Bleu", 750, 400));

		movingObjects = new ArrayList<>();
		foodsToAdd = new ArrayList<>();
		foodsToRemove = new ArrayList<>();
		spikeToAdd = new ArrayList<>();

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
			updateFoodList();
			updateSpikeList();
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

	private void updateFoodList() {
		for(Food food : foodsToRemove) {
			board.removeFood(food);
			movingObjects.remove(food);
		}
		foodsToRemove.clear();

		if(foodsToAdd.size() > 0) {
			movingObjects.addAll(foodsToAdd);
			board.addFoods(foodsToAdd);
			foodsToAdd.clear();
		}
	}

	private void updateSpikeList() {
		for(SpikeCell spike : spikeToAdd) {
			board.addSpike(spike);
		}
		spikeToAdd.clear();
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

	public void addSpike(SpikeCell spike) {
		spikeToAdd.add(spike);
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
		List<FeedableObject> cells = new ArrayList<>();
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
		for(SpikeCell spike : board.getSpikeCells()) {
			checkBoardCollisionForFeedableObject(spike);
			checkFoodCollision(spike);
			cells.add(spike);
		}
		for(int i = 0; i < cells.size() - 1; i++) {
			FeedableObject cellA = cells.get(i);
			for(int j = i+1; j < cells.size(); j++) {
				FeedableObject cellB = cells.get(j);

				if(cellA.collideWith(cellB)) {
					checkCellRepulsion(cellA, cellB);
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

	private void checkCellRepulsion(FeedableObject cellA, FeedableObject cellB){
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

	private void checkCellEating(FeedableObject cellA, FeedableObject cellB) {
		FeedableObject bigger;
		FeedableObject smaller;
		if(cellA.getSize() > cellB.getSize()) {
			bigger = cellA;
			smaller = cellB;
		} else {
			bigger = cellB;
			smaller = cellA;
		}
		double dist = Math.hypot(
			bigger.getX() - smaller.getX(),
			bigger.getY() - smaller.getY()
		);
		if(dist < bigger.getRadius()) {
			playerManager.tryEat(bigger, smaller, this);
		}
	}

	private void checkFoodCollision(FeedableObject cell){
		for(Food food : board.getFoods()) {
			double dist = Math.hypot(
				cell.getX() - food.getX(),
				cell.getY() - food.getY()
			);
			if(dist < cell.getRadius()) {
				playerManager.tryEat(cell, food, this);
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
		for(SpikeCell spike : board.getSpikeCells()) {
			spike.applyMouvement();
		}
	}

	public void removePlayerCell(PlayerCell cell) {
		Player player = cell.getPlayer();
		player.removeCell(cell);
		playerManager.addScore(player.getTeam(), -cell.getSize());
		if(player.getCells().size() <= 0) {
			playerManager.removePlayer(player);
		}
	}

	public void removeSpikeCell(SpikeCell cell) {
		board.removeSpike(cell);
	}

	public void removeFood(Food food) {
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