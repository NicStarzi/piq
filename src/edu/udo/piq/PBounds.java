package edu.udo.piq;

/**
 * A simple bounding box that can be used for all kinds of purposes.<br>
 * All {@link PComponent}s have a bounding box when they are part of a 
 * GUI, its defined by the {@link PLayout} of their parent.<br>
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
	public int getFinalX();
	
	/**
	 * The y coordinate of the lower right corner of the 
	 * rectangle defined by this PBounds instance.<br>
	 * 
	 * @return y + height
	 */
	public int getFinalY();
	
	/**
	 * The x coordinate of the center of the rectangle 
	 * defined by this PBounds instance.<br>
	 * 
	 * @return x + width / 2
	 */
	public int getCenterX();
	
	/**
	 * The y coordinate of the center of the rectangle 
	 * defined by this PBounds instance.<br>
	 * 
	 * @return y + height / 2
	 */
	public int getCenterY();
	
	/**
	 * Returns true if the point defined by x and y is 
	 * within these bounds. Otherwise false is returned.<br>
	 * 
	 * @param x
	 * @param y
	 * @return (x >= getX() && x <= getFinalX() && y >= getY() && y <= getFinalY())
	 */
	public boolean contains(int x, int y);
	
	public PBounds getIntersection(PBounds other);
	
}