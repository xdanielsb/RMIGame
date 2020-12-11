package model;

public class PlayerCell extends FeedableObject {

	private static final long serialVersionUID = 1L;

	public static final int CELL_MIN_SIZE = 50;
	public static final int MIN_THROWING_FOOD_SIZE = 400;
	public static final int MIN_SPLITTING_SIZE = 100;
	public static final float MIN_SPEED = 0.5f;
	public static final int COOLDOWN_INITIAL = 625;

	private float movementX;
	private float movementY;
	
	private int cooldown;

	private Player player;

	public PlayerCell(Player player, int size) {
		super(
				player.getTeam().getSpawnX(),
				player.getTeam().getSpawnY(),
				size,
				player.getTeam().getColor()
		);
		movementX = 0;
		movementY = 0;
		cooldown = 0;
		this.player = player;
	}
	
	public PlayerCell(PlayerCell cell, float ratio, float directionX, float directionY) {
		super(
				cell.getX(),
				cell.getY(),
				(int)(cell.getSize()*ratio),
				cell.getPlayer().getTeam().getColor()
		);
		cell.setSize(cell.getSize() - this.getSize());
		this.setInertiaX(directionX*4);
		this.setInertiaY(directionY*4);
		movementX = 0;
		movementY = 0;
		cooldown = COOLDOWN_INITIAL;
		cell.resetCooldown();
		player = cell.getPlayer();
	}

	public Player getPlayer() {
		return player;
	}

	public void setMovementX(float movementX) {
		this.movementX = movementX;
	}

	public void setMovementY(float movementY) {
		this.movementY = movementY;
	}

	@Override
	public float getSpeedX() {
		return super.getSpeedX() + movementX * (MIN_SPEED + (1-MIN_SPEED)*((float)CELL_MIN_SIZE/(float)getSize()));
	}

	@Override
	public float getSpeedY() {
		return super.getSpeedY() + movementY * (MIN_SPEED + (1-MIN_SPEED)*((float)CELL_MIN_SIZE/(float)getSize()));
	}
	
	public void updateCooldown() {
		if(cooldown > 0) {
			cooldown -= 1;
		}
	}
	
	public int getCooldown() {
		return cooldown;
	}
	
	public void resetCooldown() {
		cooldown = COOLDOWN_INITIAL;
	}
	
}
