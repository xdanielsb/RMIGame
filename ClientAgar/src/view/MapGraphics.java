package view;

import java.rmi.RemoteException;
import displayer.BoardDisplayer;
import displayer.HeaderHandlerDisplayer;
import displayer.VictoryDisplayer;
import model.Player;
import model.PlayerCell;
import model.Board;
import processing.core.PApplet;
import remote.IPlayerRemote;

public class MapGraphics extends PApplet {

	private IPlayerRemote rm;
	private Board board;
	private final int id;
	private float centerX;
	private float centerY;
	private HeaderHandler header;
	private Player player;
	private float zoomRatio;
	private boolean gameOver;

	public MapGraphics(IPlayerRemote distant, String username) throws RemoteException {		
		header = new HeaderHandler();
		zoomRatio = 1.6f;

		this.rm = distant;
		this.id = rm.registerPlayer(username);
		this.board = rm.getBoard();
		this.player = board.getPlayer(id);

		Runtime runtime = Runtime.getRuntime();
		Runnable runnable = () -> {
			try {
				rm.removePlayer(id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		runtime.addShutdownHook(new Thread(runnable));
	}

	// method for setting the size of the window
	public void settings() {
		size(1280, 720);
		centerX = width/2;
		centerY = height/2;
	}

	// identical use to setup in Processing IDE except for size()
	public void setup() {
		surface.setTitle("Agar IO");
	}

	public void draw() {
		background(230);
		cursor(CROSS);

		try {
			update();
			if (!this.gameOver) {

				BoardDisplayer.draw(board, this.player, this.zoomRatio, this.centerX, this.centerY, this);

				HeaderHandlerDisplayer.draw(this.header, this);
			} else {
				VictoryDisplayer.draw(this.board.getWinners(), this);
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

	}

	public void update() throws RemoteException
	{		this.gameOver = rm.gameOver();
		this.board = rm.getBoard();
		
		if(this.board.getPlayer(id).isAlive())
			this.player = this.board.getPlayer(this.id);
		
		this.header.update(
				rm.getTimer(),
				this.board.getTeams().get(0).getScore(),
				this.board.getTeams().get(1).getScore()
		);
		
		this.zoomRatio = calculateScale(
				Math.min(width, height),
				0.05f,
				PlayerCell.CELL_MIN_SIZE,
				0.6f,
				1000000,
				player
		);
	
		updateMousePosition();
	}

	public void updateMousePosition() throws RemoteException {
		this.rm.sendMousePosition(id, mouseX - (centerX) + player.getX(), mouseY - (centerY) + player.getY());
	}
	
	public float calculateScale(
		double screenSize,
		double initialPlayerScreenProportion,
		double initialPlayerSize,
		double maximumPlayerScreenProportion,
		double maximumPlayerSize,
		Player player
	) {
		double playerScreenProportion = 
			initialPlayerScreenProportion +
			(maximumPlayerScreenProportion-initialPlayerScreenProportion)*
			(Math.sqrt(player.getSize()-initialPlayerSize)/
			Math.sqrt(maximumPlayerSize - initialPlayerSize));
		double newScreenSize = player.getRadius()/playerScreenProportion;
		return (float)(screenSize/newScreenSize);
	}

}
