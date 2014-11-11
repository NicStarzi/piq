package edu.udo.piq;

/**
 * {@link PInsets} define a padding inside a bounding box 
 * with individual distances to each side.<br>
 * Insets are used, amongst others, by {@link PLayout}s to 
 * define a padding for the contents of a container.<br>
 * 
 * @author Nic Starzi
 */
public interface PInsets {
	
	/**
	 * Returns the distance from the top.<br>
	 * The distance should never be a negative value.<br>
	 * 
	 * @return distance from the top
	 */
	public int getFromTop();
	
	/**
	 * Returns the distance from the bottom.<br>
	 * The distance should never be a negative value.<br>
	 * 
	 * @return distance from the bottom
	 */
	public int getFromBottom();
	
	/**
	 * Returns the distance from the left edge.<br>
	 * The distance should never be a negative value.<br>
	 * 
	 * @return distance from the left
	 */
	public int getFromLeft();
	
	/**
	 * Returns the distance from the right edge.<br>
	 * The distance should never be a negative value.<br>
	 * 
	 * @return distance from the right
	 */
	public int getFromRight();
	
	/**
	 * Returns the total horizontal distance, that is 
	 * the distance from the left and the distance 
	 * from the right summed up.<br>
	 * 
	 * @return distance from left and distance from right summed up
	 */
	public int getHorizontal();
	
	/**
	 * Returns the total vertical distance, that is 
	 * the distance from the top and the distance 
	 * from the bottom summed up.<br>
	 * 
	 * @return distance from top and distance from bottom summed up
	 */
	public int getVertical();
	
	/**
	 * Two instances of PInsets are referred to as equal if their 
	 * distances to every side are equal.<br>
	 * 
	 * @param other an object or null
	 * @return true other is an instance of PInsets and has the same 
	 * distances as this for every direction
	 * @see #getFromTop()
	 * @see #getFromBottom()
	 * @see #getFromLeft()
	 * @see #getFromRight()
	 */
	public boolean equals(Object other);
	
	/**
	 * The String representation of insets should include the 
	 * distances to each side separately.<br>
	 * Adding the horizontal and vertical size is optional.<br>
	 * 
	 * @return a String representation of these insets
	 * @see #getFromTop()
	 * @see #getFromBottom()
	 * @see #getFromLeft()
	 * @see #getFromRight()
	 */
	public String toString();
	
}