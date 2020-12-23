package interfaces;

public interface PlayerCell extends FeedableObject {

  static final long serialVersionUID = 1L;

  public static final int CELL_MIN_SIZE = 50;
  public static final int MIN_THROWING_FOOD_SIZE = 400;
  public static final int MIN_SPLITTING_SIZE = 100;
  public static final float MIN_SPEED = 0.5f;
  public static final int COOLDOWN_INITIAL = 625;

  public Player getPlayer();

  public void setMovementX(float movementX);

  public void setMovementY(float movementY);

  public int getCooldown();

  public void updateCooldown();

  public void resetCooldown();
}
