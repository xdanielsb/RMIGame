package model;

import java.awt.Color;
import java.io.Serializable;

public abstract class CoordinateObject implements Serializable {


	private static final long serialVersionUID = 1L;

	private float x;
	private float y;
	private int size;
	private Color color;
	private float inertiaX;
	private float inertiaY;

	public CoordinateObject(float x, float y, int size, Color color) {
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = color;
		this.inertiaX = 0;
		this.inertiaY = 0;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public float getRadius() {
		return (float)Math.sqrt(size/Math.PI);
	}

	public float getInertiaX() {
		return inertiaX;
	}

	public void setInertiaX(float inertiaX) {
		this.inertiaX = inertiaX;
	}

	public float getInertiaY() {
		return inertiaY;
	}

	public void setInertiaY(float inertiaY) {
		this.inertiaY = inertiaY;
	}

	public float getSpeedX() {
		return inertiaX;
	}

	public float getSpeedY() {
		return inertiaY;
	}

	public void applyMouvement() {
		x += getSpeedX();
		y += getSpeedY();
		inertiaX *= 0.75;
		if(inertiaX < 0.1) {
			inertiaX = 0;
		}
		inertiaY *= 0.75;
		if(inertiaY < 0.1) {
			inertiaY = 0;
		}
	}

}
