package server.model;

import java.awt.Color;

import interfaces.CoordinateObject;
import interfaces.FeedableObject;

public abstract class FeedableObjectImpl extends CoordinateObjectImpl implements FeedableObject {

	private static final long serialVersionUID = 1L;

	private float repulsionX;
	private float repulsionY;
	
	public FeedableObjectImpl(float x, float y, int size, Color color) {
		super(x, y, size, color);
		repulsionX = 0;
		repulsionY = 0;
	}

	public void addRepulsionX(float repulsionX) {
		this.repulsionX += repulsionX;
	}

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
	
	public void eat(CoordinateObject coordObj) {
		setSize(getSize() + coordObj.getSize());
	}
	
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