package edu.udo.piq.tools;

import edu.udo.piq.PPoint;

public class ImmutablePPoint extends AbstractPPoint {
	
	protected final double x;
	protected final double y;
	
	public ImmutablePPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public ImmutablePPoint(PPoint other) {
		this(other.getX(), other.getY());
	}
	
	public ImmutablePPoint(PPoint origin, PPoint offset) {
		this(origin.getX() + offset.getX(), origin.getY() + offset.getY());
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	@Override
	public double getY() {
		return y;
	}
}