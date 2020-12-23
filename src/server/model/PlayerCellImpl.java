package server.model;

import interfaces.FeedableObject;
import interfaces.Player;
import interfaces.PlayerCell;
import interfaces.SpikeCell;

/** Implementation of the PlayerCell Interface */
public class PlayerCellImpl extends FeedableObjectImpl implements PlayerCell {

  private static final long serialVersionUID = 1L;

  private float movementX;
  private float movementY;

  private int cooldown;

  private Player player;

  /**
   * PlayerCell constructor use at the player creation or recreation
   *
   * @param player
   * @param size : size of the cell
   */
  public PlayerCellImpl(Player player, int size) {
    super(
        player.getTeam().getSpawnX(),
        player.getTeam().getSpawnY(),
        size,
        player.getTeam().getColor());
    movementX = 0;
    movementY = 0;
    cooldown = 0;
    this.player = player;
  }

  /**
   * PlayerCell constructor use when a placer cell spit itself
   *
   * @param cell : the origin cell who split itself
   * @param size : size of the new cell
   * @param directionX : propulsion vector for the split, on X vector
   * @param directionY : propulsion vector for the split, on Y vector
   */
  public PlayerCellImpl(PlayerCell cell, int size, float directionX, float directionY) {
    super(cell.getX(), cell.getY(), size, cell.getPlayer().getTeam().getColor());
    cell.setSize(cell.getSize() - this.getSize());
    this.setInertiaX(directionX * 4);
    this.setInertiaY(directionY * 4);
    movementX = 0;
    movementY = 0;
    cooldown = COOLDOWN_INITIAL;
    cell.resetCooldown();
    player = cell.getPlayer();
  }

  /**
   * Method to get the Player of this PlayerCell
   *
   * @return player
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Method to define the movement of the cell (ask by the client)
   *
   * @param movementX : movement vector on X coordinate
   */
  public void setMovementX(float movementX) {
    this.movementX = movementX;
  }

  /**
   * Method to define the movement of the cell (ask by the client)
   *
   * @param movementY : movement vector on Y coordinate
   */
  public void setMovementY(float movementY) {
    this.movementY = movementY;
  }

  @Override
  public float getSpeedX() {
    return super.getSpeedX()
        + movementX * (MIN_SPEED + (1 - MIN_SPEED) * ((float) CELL_MIN_SIZE / (float) getSize()));
  }

  @Override
  public float getSpeedY() {
    return super.getSpeedY()
        + movementY * (MIN_SPEED + (1 - MIN_SPEED) * ((float) CELL_MIN_SIZE / (float) getSize()));
  }

  /**
   * Method to update the time to wait before this cell can merge again with the others cells of the
   * same player (after a split)
   */
  public void updateCooldown() {
    if (cooldown > 0) {
      cooldown -= 1;
    }
  }

  /**
   * Method to get the merge cooldown
   *
   * @return cooldown
   */
  public int getCooldown() {
    return cooldown;
  }

  /** Method to activate the cooldown before the next possible merge of this cell */
  public void resetCooldown() {
    cooldown = COOLDOWN_INITIAL;
  }

  @Override
  public boolean collideWith(FeedableObject fo) {
    if (fo instanceof PlayerCell) {
      return collideWithPlayer((PlayerCell) fo);
    } else if (fo instanceof SpikeCell) {
      return collideWithSpike((SpikeCell) fo);
    }
    return false;
  }

  /**
   * Method to define the specific condition of colliding with an other PlayerCell
   *
   * @param cell
   * @return true if the two PlayerCell are from the same team, if they are from the same Player :
   *     true if the cooldown of one of those cells is positive, false in all other cases
   */
  public boolean collideWithPlayer(PlayerCell cell) {
    return getPlayer().getTeam() == cell.getPlayer().getTeam()
        && !(getPlayer() == getPlayer() && getCooldown() == 0 && cell.getCooldown() == 0);
  }

  /**
   * Method to define the specific condition of colliding with a SpikeCell
   *
   * @param cell
   * @return a PlayerCell and a SpikeCell will never collide
   */
  public boolean collideWithSpike(SpikeCell cell) {
    return false;
  }
}
