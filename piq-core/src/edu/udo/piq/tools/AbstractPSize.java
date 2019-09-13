package edu.udo.piq.tools;

import edu.udo.piq.PSize;

public abstract class AbstractPSize implements PSize {
	
	/**
	 * <p>The hash code of a PSize instance is calculated from its 
	 * {@link #getWidth() width} and {@link #getHeight() height}.</p>
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getWidth();
		result = prime * result + getHeight();
		return result;
	}
	
	/**
	 * <p>Two instances of PSize are considered equal if they have the 
	 * same values for {@link #getWidth() width} and {@link #getHeight() 
	 * height}, as well, as equal {@link #hashCode() hash codes}.</p>
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PSize)) {// this also checks for null
			return false;
		}
		PSize other = (PSize) obj;
		return getWidth() == other.getWidth() 
				&& getHeight() == other.getHeight()
				// as per definition of hashCode() 
				// we need to make sure hashCode() is implemented correctly for equality to work
				&& hashCode() == other.hashCode();
	}
	
	/**
	 * <p>Returns a String representation in the following format:</p>
	 * <pre>getClass().getSimpleName() [width=getWidth(), height=getHeight()]</pre>
	 */
	@Override
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