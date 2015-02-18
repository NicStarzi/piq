package edu.udo.piq;

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
	public static final PSize NULL_SIZE = new PSize() {
		public int getWidth() {
			return 0;
		}
		public int getHeight() {
			return 0;
		}
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
			builder.append(PSize.class.getSimpleName());
			builder.append(" [width=");
			builder.append(getWidth());
			builder.append(", height=");
			builder.append(getHeight());
			builder.append("]");
			return builder.toString();
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
	
}