package edu.udo.piq.tools;

import edu.udo.piq.PSize;

/**
 * <p>An immutable implementation of {@link PSize}. After creation the 
 * {@link #getWidth() width} and {@link #getHeight() height} of this 
 * size will be constant.</p>
 * <p>The implementation of {@link #toString()}, {@link #hashCode()} and 
 * {@link #equals(Object)} of this class is consistent with the 
 * implementations in {@link MutablePSize}, {@link PSize#ZERO_SIZE} and 
 * {@link PSize#INFINITE_SIZE}</p>
 * 
 * @author Nic Starzi
 * 
 * @see PSize
 * @see AbstractPSize
 * @see MutablePSize
 * @see PSize#ZERO_SIZE
 * @see PSize#INFINITE_SIZE
 */
public class ImmutablePSize extends AbstractPSize implements PSize {
	
	/**
	 * Attributes for {@link #getWidth() width} and {@link #getHeight() height} respectively.
	 */
	private final int w, h;
	
	/**
	 * <p>Copy constructor which takes the {@link #getWidth() width} and 
	 * {@link #getHeight() height} of the given size and copies them.</p>
	 * @param other		a non-null instance of {@link PSize} from which the width and height are copied
	 */
	public ImmutablePSize(PSize other) {
		this(other.getWidth(), other.getHeight());
	}
	
	/**
	 * <p>Creates a new instance of {@link ImmutablePSize} with the given 
	 * values for {@link #getWidth() width} and {@link #getHeight() height}.</p>
	 * @param width		horizontal size (X-Axis) going from left to right
	 * @param height	vertical size (Y-Axis) going from top to bottom
	 */
	public ImmutablePSize(int width, int height) {
		w = width;
		h = height;
	}
	
	@Override
	public int getWidth() {
		return w;
	}
	
	@Override
	public int getHeight() {
		return h;
	}
	
	@Override
	public PSize getAsImmutable() {
		return this;
	}
}