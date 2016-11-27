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
	
	@Override
	public String toString() {
		return toString1();
	}
	
	public String toString255() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [r=");
		builder.append(getRed255());
		builder.append(", g=");
		builder.append(getGreen255());
		builder.append(", b=");
		builder.append(getBlue255());
		builder.append(", a=");
		builder.append(getAlpha255());
		builder.append("]");
		return builder.toString();
	}
	
	public String toString1() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [r=");
		builder.append(getRed1());
		builder.append(", g=");
		builder.append(getGreen1());
		builder.append(", b=");
		builder.append(getBlue1());
		builder.append(", a=");
		builder.append(getAlpha1());
		builder.append("]");
		return builder.toString();
	}
	
	@Override
	public int hashCode() {
		return getRed255()
				+ getGreen255() * 255
				+ getBlue255() * 255 * 255
				+ getAlpha255() * 255 * 255 * 255;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof PColor)) {
			return false;
		}
		PColor other = (PColor) obj;
		return getRed255() == other.getRed255()
				&& getGreen255() == other.getGreen255()
				&& getBlue255() == other.getBlue255()
				&& getAlpha255() == other.getAlpha255();
	}
	
}