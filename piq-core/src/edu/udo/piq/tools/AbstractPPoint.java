package edu.udo.piq.tools;

import edu.udo.piq.PPoint;

/**
 * <p>Offers a common implementation for the methods {@link #hashCode()}, 
 * {@link #equals(Object)} and {@link #toString()}. All instances of 
 * {@link PPoint} should either extend this class or implement the above 
 * methods in a compatible way.</p>
 * 
 * @author Nic Starzi
 * 
 * @see ImmutablePPoint
 * @see MutablePPoint
 */
public abstract class AbstractPPoint implements PPoint {
	
	/**
	 * The hashCode of a PPoint is defined by its coordinates.
	 */
	@Override
	public int hashCode() {
		return Double.hashCode(getX()) + 
				Double.hashCode(getY()) * 971;
	}
	
	/**
	 * Two instances of PPoint are considered equal if both have identical x and y coordinates. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PPoint)) {
			return false;
		}
		PPoint other = (PPoint) obj;
		return Double.compare(getX(), other.getX()) == 0 && Double.compare(getY(), other.getY()) == 0;
	}
	
	/**
	 * <p>Returns a String consisting of the {@link Class#getSimpleName() simple name} of the 
	 * class of the calling point followed by its {@link #getX() x-} and {@link #getY() 
	 * y-coordinates} within curly brackets "{}" and separated by a comma ",".</p>
	 * Example Output: "<i>ImmutablePPoint{4.2, 19.84}</i>"
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("{");
		sb.append(getX());
		sb.append(", ");
		sb.append(getY());
		sb.append("}");
		return sb.toString();
	}
}