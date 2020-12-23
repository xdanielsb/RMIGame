package server.model;

import interfaces.CoordinateObject;
import interfaces.FeedableObject;
import java.awt.Color;

/** Implementation of the FeedableObject Interface */
public abstract class FeedableObjectImpl extends CoordinateObjectImpl implements FeedableObject {

  private static final long serialVersionUID = 1L;

  private float repulsionX;
  private float repulsionY;

  /**
   * FeedableObject main constructor
   *
   * @param x : X coordinate of the cell
   * @param y : Y coordinate of the cell
   * @param size : size of the cell
   * @param color : color of the cell
   */
  public FeedableObjectImpl(float x, float y, int size, Color color) {
    super(x, y, size, color);
    repulsionX = 0;
    repulsionY = 0;
  }

  /**
   * Method to modify the repulsion vector on X coordinate (remember that the repulsion vector is
   * set to 0 at the end of each server tick)
   *
   * @param repulsionX : repulsion vector to add on X coordinate (positive or negative)
   */
  public void addRepulsionX(float repulsionX) {
    this.repulsionX += repulsionX;
  }

  /**
   * Method to modify the repulsion vector on Y coordinate (remember that the repulsion vector is
   * set to 0 at the end of each server tick)
   *
   * @param repulsionY : repulsion vector to add on Y coordinate (positive or negative)
   */
  public void addRepulsionY(float repulsionY) {
    this.repulsionY += repulsionY;
  }

  @Override
  public float getSpeedX() {
    return super.getSpeedX() + repulsionX;
  }

  @Override
  public float getSpeedY() {
    return super.getSpeedY() + repulsionY;
  }

  /**
   * Method to eat a cell
   *
   * @param coordObj : the cell to eat
   */
  public void eat(CoordinateObject coordObj) {
    setSize(getSize() + coordObj.getSize());
  }

  /**
   * Method to know if this feedableObjcet collide with an other one
   *
   * @param fd : FeedableObject to possibly collide with
   * @return true in any case, this method need to be Override to have a different behavior
   */
  public boolean collideWith(FeedableObject fd) {
    return true;
  }

  @Override
  public void applyMouvement() {
    super.applyMouvement();
    repulsionX = 0;
    repulsionY = 0;
  }
}
