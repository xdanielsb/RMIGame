package interfaces;

import java.awt.Color;
import java.io.Serializable;
import java.rmi.Remote;

public interface CoordinateObject extends Comparable<CoordinateObject>, Serializable, Remote {

  static final long serialVersionUID = 1L;

  public float getX();

  public void setX(float x);

  public float getY();

  public void setY(float y);

  public int getSize();

  public void setSize(int size);

  public float getRadius();

  public float getInertiaX();

  public void setInertiaX(float inertiaX);

  public float getInertiaY();

  public void setInertiaY(float inertiaY);

  public Color getColor();

  public float getSpeedX();

  public float getSpeedY();

  public void applyMouvement();
}
