package edu.udo.piq.tools;

import edu.udo.piq.PColor;

public abstract class AbstractPColor implements PColor {
	
	protected void throwExceptionIfValueIllegal255(int value) {
		if (value < 0 || value > 255) {
			throw new IllegalArgumentException("Color value must be between 0 and 255 but was: "+value);
		}
	}
	
	protected void throwExceptionIfValueIllegal1(double value) {
		if (value < 0 || value > 1) {
			throw new IllegalArgumentException("Color value must be between 0.0 and 1.0 but was: "+value);
		}
	}
	
	public String toString() {
		return toString255();
	}
	
	public String toString255() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [red=");
		builder.append(getRed255());
		builder.append(", green=");
		builder.append(getGreen255());
		builder.append(", blue=");
		builder.append(getBlue255());
		builder.append(", alpha=");
		builder.append(getAlpha255());
		builder.append("]");
		return builder.toString();
	}
	
	public String toString1() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [red=");
		builder.append(getRed1());
		builder.append(", green=");
		builder.append(getGreen1());
		builder.append(", blue=");
		builder.append(getBlue1());
		builder.append(", alpha=");
		builder.append(getAlpha1());
		builder.append("]");
		return builder.toString();
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getRed255();
		result = prime * result + getGreen255();
		result = prime * result + getBlue255();
		result = prime * result + getAlpha255();
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null || !(obj instanceof PColor)) {
			return false;
		}
		PColor other = (PColor) obj;
		return getRed255() == other.getRed255()
				&& getGreen255() == other.getGreen255()
				&& getBlue255() == other.getBlue255()
				&& getAlpha255() == other.getAlpha255();
	}
	
}