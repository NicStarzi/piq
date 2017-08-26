package edu.udo.piq;

import edu.udo.piq.tools.AbstractPSize;
import edu.udo.piq.tools.ImmutablePSize;

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
		@Override
		public int getWidth() {
			return 0;
		}
		@Override
		public int getHeight() {
			return 0;
		}
	};
	
	/**
	 * A simple implementation of the {@link PSize} interface that
	 * always returns {@link Integer.MAX_VALUE} for both width and height.<br>
	 * Usage of this size is encouraged for components which need to grow
	 * indefinitely.<br>
	 */
	public static final PSize INFINITE_SIZE = new AbstractPSize() {
		@Override
		public int getWidth() {
			return Integer.MAX_VALUE;
		}
		@Override
		public int getHeight() {
			return Integer.MIN_VALUE;
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
	
	public default PSize createCopy() {
		return createCopyAndAdd(0, 0);
	}
	
	public default PSize createCopyAndAdd(int width, int height) {
		return new ImmutablePSize(getWidth() + width, getHeight() + height);
	}
	
	public default PSize createCopyAndAdd(PSize other) {
		return createCopyAndAdd(other.getWidth(), other.getHeight());
	}
	
	public default PSize createCopyAndAdd(PInsets insets) {
		return createCopyAndAdd(insets.getWidth(), insets.getHeight());
	}
	
}