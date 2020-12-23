package interfaces;

public interface Food extends CoordinateObject {

  static final long serialVersionUID = 1L;

  public static final int FOOD_SIZE = 2;

  public boolean isAlive();

  public boolean isPersistent();

  public void killFood();
}
