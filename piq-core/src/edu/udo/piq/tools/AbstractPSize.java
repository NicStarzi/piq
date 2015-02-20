package edu.udo.piq.tools;

import edu.udo.piq.PSize;

public abstract class AbstractPSize implements PSize {
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getWidth();
		result = prime * result + getHeight();
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof PSize)) {
			return false;
		}
		PSize other = (PSize) obj;
		return getWidth() == other.getWidth() && getHeight() == other.getHeight();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		builder.append(" [width=");
		builder.append(getWidth());
		builder.append(", height=");
		builder.append(getHeight());
		builder.append("]");
		return builder.toString();
	}
}