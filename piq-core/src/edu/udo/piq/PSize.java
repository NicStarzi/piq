package edu.udo.piq;

import edu.udo.piq.tools.AbstractPSize;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.tools.MutablePSize;

/**
 * <p>An abstract representation of size expressed as width and height.</p>
 * 
 * <p>Two important implementations of this interface are
 * {@link ImmutablePSize} and {@link MutablePSize}. Instances of
 * ImmutablePSize should be preferred as results returned by such methods
 * as {@link PComponent#getPreferredSize()} or similar. Instances of
 * MutablePSize can be used as output parameters for methods such as
 * {@link PFontResource#getSize(String, MutablePSize)}.</p>
 * 
 * <p>All subclasses should inherit from {@link AbstractPSize} for a
 * consistent implementation of {@link #toString()}, {@link #hashCode()}
 * and {@link #equals(Object)}.</p>
 * 
 * <p>The constants {@link #ZERO_SIZE} and {@link #INFINITE_SIZE} can be
 * used for size instances with a width and height of {@code 0} or
 * {@link Integer#MAX_VALUE} respectively.</p>
 * 
 * @author Nic Starzi
 * @see AbstractPSize
 * @see MutablePSize
 * @see ImmutablePSize
 * @see PBounds
 * @see PInsets
 * @see #ZERO_SIZE
 * @see #INFINITE_SIZE
 */
public interface PSize {
	
	/**
	 * <p>An implementation of {@link PSize} which always returns
	 * {@code 0} for both its width and height.</p>
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
	 * <p>An implementation of {@link PSize} which always returns
	 * {@link Integer#MAX_VALUE} for both its width and height.</p>
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
	 * <p>Returns the size in the horizontal dimension (X-Axis), that is, from left to right.</p>
	 * 
	 * @return distance from left to right
	 * @see #getHeight()
	 */
	public int getWidth();
	
	/**
	 * <p>Returns the size in the vertical dimension (Y-Axis), that is, from top to bottom.</p>
	 * 
	 * @return distance from top to bottom
	 * @see #getWidth()
	 */
	public int getHeight();
	
	/**
	 * <p>Returns true if the area of this size is {@code 0}, that is, if either
	 * {@link #getWidth() width} or {@link #getHeight() height} is {@code 0}.</p>
	 * @return true if {@code getWidth() * getHeight() == 0}
	 */
	public default boolean isEmpty() {
		return getWidth() * getHeight() == 0;
	}
	
	public default PSize createImmutableCopy() {
		return new ImmutablePSize(this);
	}
	
	/**
	 * <p>Creates and returns a new instance of {@link PSize} with the exact same
	 * {@link #getWidth() width} and {@link #getHeight() height} as this size.</p>
	 * <p>No assumptions should be made about whether or not the returned instance
	 * is mutable. Changes to this size after the copy was created should not affect
	 * the copy.</p>
	 * @return an instance of {@link ImmutablePSize} with the same width and height as this
	 * @see #createCopyAndAdd(int, int)
	 * @see #createCopyAndAdd(PSize)
	 */
	public default PSize createCopy() {
		return createImmutableCopy();
	}
	
	/**
	 * <p>Creates and returns a new instance of {@link PSize} with the exact same
	 * {@link #getWidth() width} and {@link #getHeight() height} as this size.</p>
	 * <p>No assumptions should be made about whether or not the returned instance
	 * is mutable. Changes to this size after the copy was created should not affect
	 * the copy.</p>
	 * @return an instance of {@link ImmutablePSize} with the same width and height as this
	 * @see #createCopy()
	 * @see #createCopyAndAdd(PSize)
	 */
	public default PSize createCopyAndAdd(int width, int height) {
		return new ImmutablePSize(getWidth() + width, getHeight() + height);
	}
	
	/**
	 * <p>Creates and returns a new instance of {@link PSize} with the exact same
	 * {@link #getWidth() width} and {@link #getHeight() height} as this size.</p>
	 * <p>No assumptions should be made about whether or not the returned instance
	 * is mutable. Changes to this size after the copy was created should not affect
	 * the copy.</p>
	 * @return an instance of {@link ImmutablePSize} with the same width and height as this
	 * @see #createCopy()
	 * @see #createCopyAndAdd(int, int)
	 */
	public default PSize createCopyAndAdd(PSize other) {
		return createCopyAndAdd(other.getWidth(), other.getHeight());
	}
	
}