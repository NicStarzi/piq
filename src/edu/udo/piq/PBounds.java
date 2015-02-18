package edu.udo.piq;

import edu.udo.piq.tools.ImmutablePBounds;

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
	public int getWidth();
	
	/**
	 * The height of the rectangle defined by this PBounds 
	 * instance.<br>
	 * 
	 * @return height of bounds
	 */
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
	
	public default PBounds getIntersection(PBounds other) {
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
}