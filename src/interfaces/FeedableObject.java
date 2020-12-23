package interfaces;

public interface FeedableObject extends CoordinateObject {

  static final long serialVersionUID = 1L;

  public void addRepulsionX(float repulsionX);

  public void addRepulsionY(float repulsionY);

  public void eat(CoordinateObject coordObj);

  public boolean collideWith(FeedableObject fd);
}
