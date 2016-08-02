package edu.udo.piq;

import edu.udo.piq.tools.AbstractPSize;

/**
 * This interface represents an abstract two dimensional size in 
 * width and height.<br>
 * 
 * @author Nic Starzi
 */
public interface PSize {
	
	/**
	 * A simple implementation of the {@link PSize} interface that 
	 * always returns 0 for both width and height.<br>
	 * This instance could be used to save memory whenever this 
	 * specific size is needed.<br>
	 */
	public static final PSize ZERO_SIZE = new AbstractPSize() {
		public int getWidth() {
			return 0;
		}
		public int getHeight() {
			return 0;
		}
	};
	
	/**
	 * Returns a horizontal size, that is, from left to right.<br>
	 * 
	 * @return distance from left to right
	 * @see #getHeight()
	 * @see PBounds
	 * @see PInsets
	 */
	public int getWidth();
	
	/**
	 * Returns a vertical size, that is, from top to bottom.<br>
	 * 
	 * @return distance from top to bottom
	 * @see #getWidth()
	 * @see PBounds
	 * @see PInsets
	 */
	public int getHeight();
	
	public default boolean isEmpty() {
		return getWidth() * getHeight() < 1;
	}
	
}