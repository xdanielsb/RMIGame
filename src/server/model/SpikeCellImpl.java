package server.model;

import interfaces.Board;
import interfaces.FeedableObject;
import interfaces.PlayerCell;
import interfaces.SpikeCell;

/** Implementation of the SpikeCell Interface */
public class SpikeCellImpl extends FeedableObjectImpl implements SpikeCell {

  private static final long serialVersionUID = 1L;

  /**
   * SpikeCell construct, use at the board creation
   *
   * @param board
   */
  public SpikeCellImpl(Board board) {
    super(
        ((float) Math.random() * (board.getBoardWidth())),
        ((float) Math.random() * (board.getBoardHeight())),
        MIN_SPIKE_SIZE,
        SPIKE_COLOR);
  }

  /**
   * SpikeCell constructor, use when a SpikeCell split itself
   *
   * @param cell : the origin SpikeCell who split itself
   * @param size : size of the new SpikeCell
   * @param directionX : direction vector for the spit propulsion, on X coordinate
   * @param directionY : direction vector for the spit propulsion, on Y coordinate
   */
  public SpikeCellImpl(SpikeCell cell, int size, float directionX, float directionY) {
    super(cell.getX(), cell.getY(), size, SPIKE_COLOR);
    cell.setSize(cell.getSize() - this.getSize());
    this.setInertiaX(directionX * 5);
    this.setInertiaY(directionY * 5);
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
   * Specific collide condition with a PlayerCell
   *
   * @param cell : the PlayerCell to collide with
   * @return false, in any case a SpikeCell and a Player will not collide
   */
  public boolean collideWithPlayer(PlayerCell cell) {
    return false;
  }

  /**
   * Specific collide condition with an other SpikeCell
   *
   * @param cell : the SpikeCell to collide with
   * @return true, in any case a SpikeCell will always collide with the other SpikeCells
   */
  public boolean collideWithSpike(SpikeCell cell) {
    return true;
  }
}
