package edu.udo.piq;

import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.MutablePBounds;

/**
 * A simple bounding box that can be used for all kinds of purposes.<br>
 * All {@link PComponent}s have a bounding box when they are part of a
 * GUI, its defined by the {@link PReadOnlyLayout} of their parent.<br>
 * 
 * @author Nic Starzi
 */
public interface PBounds extends PSize {
	
	/**
	 * The x coordinate of the upper left corner of the
	 * rectangle defined by this PBounds instance.<br>
	 * 
	 * @return x coordinate
	 */
	public int getX();
	
	/**
	 * The y coordinate of the upper left corner of the
	 * rectangle defined by this PBounds instance.<br>
	 * 
	 * @return y coordinate
	 */
	public int getY();
	
	/**
	 * The width of the rectangle defined by this PBounds
	 * instance.<br>
	 * 
	 * @return width of bounds
	 */
	@Override
	public int getWidth();
	
	/**
	 * The height of the rectangle defined by this PBounds
	 * instance.<br>
	 * 
	 * @return height of bounds
	 */
	@Override
	public int getHeight();
	
	/**
	 * The x coordinate of the lower right corner of the
	 * rectangle defined by this PBounds instance.<br>
	 * 
	 * @return x + width
	 */
	public default int getFinalX() {
		return getX() + getWidth();
	}
	
	/**
	 * The y coordinate of the lower right corner of the
	 * rectangle defined by this PBounds instance.<br>
	 * 
	 * @return y + height
	 */
	public default int getFinalY() {
		return getY() + getHeight();
	}
	
	/**
	 * The x coordinate of the center of the rectangle
	 * defined by this PBounds instance.<br>
	 * 
	 * @return x + width / 2
	 */
	public default int getCenterX() {
		return getX() + getWidth() / 2;
	}
	
	/**
	 * The y coordinate of the center of the rectangle
	 * defined by this PBounds instance.<br>
	 * 
	 * @return y + height / 2
	 */
	public default int getCenterY() {
		return getY() + getHeight() / 2;
	}
	
	public default boolean contains(PPoint point) {
		return contains((int) point.getX(), (int) point.getY());
	}
	
	/**
	 * Returns true if the point defined by x and y is
	 * within these bounds. Otherwise false is returned.<br>
	 * 
	 * @param x
	 * @param y
	 * @return (x >= getX() && x <= getFinalX() && y >= getY() && y <= getFinalY())
	 */
	public default boolean contains(int x, int y) {
		return x >= getX() && x <= getFinalX() && y >= getY() && y <= getFinalY();
	}
	
	public default boolean fullyContains(PBounds other) {
		return contains(other.getX(), other.getY()) && contains(other.getFinalX(), other.getFinalY());
	}
	
	public default boolean isOverlapping(PBounds other) {
		int x1 = getX();
		int y1 = getY();
		int fx1 = getFinalX();
		int fy1 = getFinalY();
		int x2 = other.getX();
		int y2 = other.getY();
		int fx2 = other.getFinalX();
		int fy2 = other.getFinalY();
		return !(x1 > fx2 || y1 > fy2 || fx1 < x2 || fy1 < y2);
	}
	
	/**
	 * Creates and returns a new instance of {@link PBounds} that is the
	 * intersection between these bounds and the other bounds.<br>
	 * If these two PBounds are disjunct, that means there is no intersection,
	 * null will be returned.<br>
	 * @param other		a non-null instance of PBounds
	 * @return			a new PBounds object or null if no intersection exists
	 */
	public default PBounds createIntersection(PBounds other) {
		return fillIntersection(other, null);
	}
	
	public default PBounds fillIntersection(PBounds other, MutablePBounds result) {
		int x = Math.max(getX(), other.getX());
		int y = Math.max(getY(), other.getY());
		int fx = Math.min(getFinalX(), other.getFinalX());
		int fy = Math.min(getFinalY(), other.getFinalY());
		int w = fx - x;
		int h = fy - y;
		if (w < 0 || h < 0) {
			return null;
		}
		if (result == null) {
			return new ImmutablePBounds(x, y, w, h);
		}
		result.setX(x);
		result.setY(y);
		result.setWidth(w);
		result.setHeight(h);
		return result;
	}
	
	public default PBounds createCopyAndSubtract(PInsets insets) {
		int x = getX() + insets.getFromLeft();
		int fx = getFinalX() - insets.getFromRight();
		if (fx < x) {
			int tmp = fx;
			fx = x;
			x = tmp;
		}
		int y = getY() + insets.getFromTop();
		int fy = getFinalY() - insets.getFromBottom();
		if (fy < y) {
			int tmp = fy;
			fy = y;
			y = tmp;
		}
		int w = fx - x;
		int h = fy - y;
		return new ImmutablePBounds(x, y, w, h);
	}
}