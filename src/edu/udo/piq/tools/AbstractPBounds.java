package edu.udo.piq.tools;

import edu.udo.piq.PBounds;

public abstract class AbstractPBounds implements PBounds {
	
	public int getFinalX() {
		return getX() + getWidth();
	}
	
	public int getFinalY() {
		return getY() + getHeight();
	}
	
	public int getCenterX() {
		return getX() + getWidth() / 2;
	}
	
	public int getCenterY() {
		return getY() + getHeight() / 2;
	}
	
	public boolean contains(int x, int y) {
		return x >= getX() && x <= getFinalX() && y >= getY() && y <= getFinalY();
	}
	
	public PBounds getIntersection(PBounds other) {
		int x = Math.max(getX(), other.getX());
		int y = Math.max(getY(), other.getY());
		int fx = Math.min(getFinalX(), other.getFinalX());
		int fy = Math.min(getFinalY(), other.getFinalY());
		int w = fx - x;
		int h = fy - y;
		if (w < 0 || h < 0) {
			return null;
		}
		return new ImmutablePBounds(x, y, w, h);
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getHeight();
		result = prime * result + getWidth();
		result = prime * result + getX();
		result = prime * result + getY();
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof PBounds)) {
			return false;
		}
		PBounds other = (PBounds) obj;
		return getX() == other.getX() 
				&& getY() == other.getY() 
				&& getWidth() == other.getWidth() 
				&& getHeight() == other.getHeight();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [x=");
		builder.append(getX());
		builder.append(", y=");
		builder.append(getY());
		builder.append(", w=");
		builder.append(getWidth());
		builder.append(", h=");
		builder.append(getHeight());
		builder.append("]");
		return builder.toString();
	}
}