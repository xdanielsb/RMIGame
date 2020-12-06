package view;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import displayer.CoordinateObjectDisplayer;
import displayer.HeaderHandlerDisplayer;
import displayer.OuterBoundsDisplayer;
import displayer.PlayerDisplayer;
import displayer.VictoryDisplayer;
import model.Player;
import model.Board;
import model.CoordinateObject;
import model.Food;
import processing.core.PApplet;
import remote.IPlayerRemote;

public class MapGraphics extends PApplet {

	private IPlayerRemote rm;
	private List<Food> foods;
	private List<Player> players;
	private Board board;
	private final int id;
	private double x;
	private double y;
	private int centerX;
	private int centerY;
	private HeaderHandler header;
	private Player player;
	private float zoomRatio = 1;

	public MapGraphics(IPlayerRemote distant, String username) throws RemoteException {
		foods = new ArrayList<>();
		players = new ArrayList<>();
		
		header = new HeaderHandler();
		
		this.rm = distant;
		this.id = rm.registerPlayer(username);
		this.board = rm.getBoard();
		this.player = board.getPlayer(id);

		x = player.getX();
		y = player.getY();
		
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
			boolean gameOver = rm.gameOver();
			this.board = rm.getBoard();
			this.player = this.board.getPlayer(this.id);
			double mySize = this.player.getSize();
			
			pushMatrix();
			translate((float)(centerX - player.getX()), (float)(centerY - player.getY()));

			if (!gameOver) {
				actionPerformed();
				
				//this.zoomRatio = (float)(1+0.6 * (2500/(mySize*mySize)));
				
				PlayerDisplayer.draw(this.player, zoomRatio, this);
				
				for(Food food : foods) {
					if(food.isAlive()) {
						CoordinateObjectDisplayer.draw(food, this.player, zoomRatio, this);		
					}
				}
				
				for(Player player : players) {
					if(player != this.player) {
						CoordinateObjectDisplayer.draw(player.getCell(), this.player, zoomRatio, this);
					}
				}
				
				OuterBoundsDisplayer.draw(this.board, this.player.getX(), this.player.getY(), zoomRatio, this);

				popMatrix();
				
				header.update(rm.getTimer(),
							  this.board.getTeam(0).getScore(),
							  this.board.getTeam(1).getScore()
				);
				
				HeaderHandlerDisplayer.draw(header, this);
			} else {
				VictoryDisplayer.draw(this.board.getWinners(), this);
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

	}

	public void actionPerformed() throws RemoteException {
		if (focused && this.player.isAlive()) {
			x = this.player.getX();
			y = this.player.getY();
			double dx = mouseX - centerX;
			double dy = mouseY - centerY;
			double length = Math.sqrt((dx * dx) + (dy * dy));
			if (length != 0) {
				dx = dx / length;
				dy = dy / length;
			}
			if (length > 10) {
				x += dx * 1;
				y += dy * 1;
				rm.sendMousePosition(id, x, y);
			}
		}
		foods = this.board.getFoods();
		players = new ArrayList<Player>(this.board.getPlayers());
	}

}
