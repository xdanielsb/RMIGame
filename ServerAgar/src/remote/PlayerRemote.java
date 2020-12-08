package remote;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import control.GameManager;
import model.Board;
import model.Player;

public class PlayerRemote extends UnicastRemoteObject implements IPlayerRemote {

	private static final long serialVersionUID = 1L;

	private GameManager gameManager;
	private final Object mutex = new Object();
	private int playerIdIncrement = 0;

	public PlayerRemote(GameManager gameManager) throws RemoteException {
		this.gameManager = gameManager;
	}

	@Override
	public int registerPlayer(String p) throws RemoteException {
		synchronized (mutex) {
			Player newPlayer = new Player(playerIdIncrement, p);
			gameManager.addPlayer(newPlayer);
			this.gameManager.getGUI().addLog("New player connected " + p + " with PID : " + playerIdIncrement);
			return playerIdIncrement++;
		}
	}

	@Override
	public void sendMousePosition(int id, float x, float y) throws RemoteException {
		gameManager.sendMousePosition(id, x, y);
	}

	@Override
	public Board getBoard() throws RemoteException {
		return gameManager.getBoard();
	}

	@Override
	public void removePlayer(int id) throws RemoteException {
		gameManager.removePlayer(id);
	}

	@Override
	public float getTimer() throws RemoteException {
		return gameManager.getTimer();
	}

	@Override
	public boolean gameOver() throws RemoteException {
		boolean isGameOver = gameManager.gameOver();
		if(isGameOver && this.gameManager.getBoard().getWinners() == null)
		{
			this.gameManager.getBoard().setWinner();
		}
		return isGameOver;
	}

}
