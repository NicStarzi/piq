package edu.udo.piq.tools;

import edu.udo.piq.PPoint;

public abstract class AbstractPPoint implements PPoint {
	
	public int hashCode() {
		return Double.hashCode(getX()) + 
				Double.hashCode(getY()) * 971;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AbstractPPoint)) {
			return false;
		}
		AbstractPPoint other = (AbstractPPoint) obj;
		if (Double.compare(getX(), other.getX()) != 0) {
			return false;
		}
		return Double.compare(getY(), other.getY()) == 0;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PPoint{");
		sb.append(getX());
		sb.append(", ");
		sb.append(getY());
		sb.append("}");
		return sb.toString();
	}
}