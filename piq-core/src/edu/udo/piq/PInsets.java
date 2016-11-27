package edu.udo.piq;

/**
 * {@link PInsets} define a padding inside a bounding box 
 * with individual distances to each side.<br>
 * Insets are used, amongst others, by {@link PReadOnlyLayout}s to 
 * define a padding for the contents of a container.<br>
 * 
 * @author Nic Starzi
 */
public interface PInsets {
	
	public static final PInsets ZERO_INSETS = new PInsets() {
		public int getFromTop() {
			return 0;
		}
		public int getFromBottom() {
			return 0;
		}
		public int getFromLeft() {
			return 0;
		}
		public int getFromRight() {
			return 0;
		}
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getFromTop();
			result = prime * result + getFromBottom();
			result = prime * result + getFromLeft();
			result = prime * result + getFromRight();
			return result;
		}
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof PInsets)) {
				return false;
			}
			PInsets other = (PInsets) obj;
			return getFromTop() == other.getFromTop() 
					&& getFromBottom() == other.getFromBottom() 
					&& getFromLeft() == other.getFromLeft() 
					&& getFromRight() == other.getFromRight();
		}
		public String toString() {
			return "ZERO_SIZE_INSETS";
		}
	};
	
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
	public default int getHorizontal() {
		return getFromLeft() + getFromRight();
	}
	
	public default int getWidth() {
		return getHorizontal();
	}
	
	/**
	 * Returns the total vertical distance, that is 
	 * the distance from the top and the distance 
	 * from the bottom summed up.<br>
	 * 
	 * @return distance from top and distance from bottom summed up
	 */
	public default int getVertical() {
		return getFromTop() + getFromBottom();
	}
	
	public default int getHeight() {
		return getVertical();
	}
	
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