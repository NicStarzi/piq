package edu.udo.piq.tools;

import edu.udo.piq.PBounds;

public abstract class AbstractPBounds implements PBounds {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getHeight();
		result = prime * result + getWidth();
		result = prime * result + getX();
		result = prime * result + getY();
		return result;
	}
	
	@Override
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
	
	@Override
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