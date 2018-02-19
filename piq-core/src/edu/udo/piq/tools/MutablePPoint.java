package edu.udo.piq.tools;

import edu.udo.piq.PPoint;

public class MutablePPoint extends AbstractPPoint {
	
	protected double x;
	protected double y;
	
	public MutablePPoint() {
		this(0, 0);
	}
	
	public MutablePPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public MutablePPoint(PPoint other) {
		this(other.getX(), other.getY());
	}
	
	public MutablePPoint(PPoint origin, PPoint offset) {
		this(origin.getX() + offset.getX(), origin.getY() + offset.getY());
	}
	
	public void setTo(PPoint other) {
		setX(other.getX());
		setY(other.getY());
	}
	
	public void set(double x, double y) {
		setX(x);
		setY(y);
	}
	
	public void setX(double value) {
		x = value;
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	public void setY(double value) {
		y = value;
	}
	
	@Override
	public double getY() {
		return y;
	}
	
	public void moveBy(double offsetX, double offsetY) {
		setX(getX() + offsetX);
		setY(getY() + offsetY);
	}
	
	public void moveByAngleInDeg(double angleInDeg, double distance) {
		moveByAngleInRad(Math.toRadians(angleInDeg), distance);
	}
	
	public void moveByAngleInRad(double angleInRad, double distance) {
		setX(getOffsetInRadX(angleInRad, distance));
		setY(getOffsetInRadY(angleInRad, distance));
	}
}