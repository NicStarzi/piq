package edu.udo.piq.tools;

import edu.udo.piq.PSize;
import edu.udo.piq.util.ThrowException;

/**
 * <p>A mutable implementation of {@link PSize}. The {@link #getWidth() 
 * width} and {@link #getHeight() height} of instances can be changed 
 * after creation.</p>
 * <p>The implementation of {@link #toString()}, {@link #hashCode()} and 
 * {@link #equals(Object)} of this class is consistent with the 
 * implementations in {@link MutablePSize}, {@link PSize#ZERO_SIZE} and 
 * {@link PSize#INFINITE_SIZE}</p>
 * 
 * @author Nic Starzi
 * 
 * @see PSize
 * @see AbstractPSize
 * @see ImmutablePSize
 * @see PSize#ZERO_SIZE
 * @see PSize#INFINITE_SIZE
 */
public class MutablePSize extends AbstractPSize implements PSize {
	
	/**
	 * Attributes for {@link #getWidth() width} and {@link #getHeight() height} respectively.
	 */
	protected int w, h;
	
	/**
	 * <p>Creates a new size with a {@link #getWidth() width} and {@link #getHeight() height} of 0.</p>
	 */
	public MutablePSize() {
		this(0, 0);
	}
	
	/**
	 * <p>Copy constructor which takes the {@link #getWidth() width} and 
	 * {@link #getHeight() height} of the given size and copies them.</p>
	 * @param other		a non-null instance of {@link PSize} from which the width and height are copied
	 */
	public MutablePSize(PSize other) {
		this(other.getWidth(), other.getHeight());
	}
	
	/**
	 * <p>Creates a new instance of {@link MutablePSize} with the given 
	 * values for {@link #getWidth() width} and {@link #getHeight() height}.</p>
	 * @param width		horizontal size (X-Axis) going from left to right
	 * @param height	vertical size (Y-Axis) going from top to bottom
	 */
	public MutablePSize(int width, int height) {
		w = width;
		h = height;
	}
	
	/**
	 * <p>Sets both the {@link #getWidth() width} and 
	 * {@link #getHeight() height} to the given values. 
	 * No sanity checks are performed. 
	 * Negative values should not be passed.</p>
	 * @param width			the new value for the width of this size
	 * @param height		the new value for the height of this size
	 * @see #setWidth(int)
	 * @see #setHeight(int)
	 * @see #set(PSize)
	 */
	public void set(int width, int height) {
		setWidth(width);
		setHeight(height);
	}
	
	/**
	 * <p>Copies and stores the {@link #getWidth() width} and 
	 * {@link #getHeight() height} of the given size.</p>
	 * @param other			a non-null instance of PSize from which the width and height are copied
	 * @see #setWidth(int)
	 * @see #setHeight(int)
	 * @see #set(int, int)
	 */
	public void set(PSize other) {
		ThrowException.ifNull(other, "other == null");
		set(other.getWidth(), other.getHeight());
	}
	
	/**
	 * <p>Sets the {@link #getWidth() width} of this size to the given value. 
	 * No sanity checks are performed. 
	 * Negative values should not be passed.</p>
	 * @param value		the new value for the width of this size
	 * @see #set(int, int)
	 * @see #getWidth()
	 * @see #addWidth(int)
	 */
	public void setWidth(int value) {
		w = value;
	}
	
	@Override
	public int getWidth() {
		return w;
	}
	
	/**
	 * <p>Sets the {@link #getHeight() height} of this size to the given value. 
	 * No sanity checks are performed. 
	 * Negative values should not be passed.</p>
	 * @param value		the new value for the height of this size
	 * @see #set(int, int)
	 * @see #getHeight()
	 * @see #addHeight(int)
	 */
	public void setHeight(int value) {
		h = value;
	}
	
	@Override
	public int getHeight() {
		return h;
	}
	
	/**
	 * <p>Adds the given width and height to this size. 
	 * No sanity checks are performed. 
	 * The resulting width or height may be negative as 
	 * a result of this method call.</p>
	 * @param width			added to the current width of this size			
	 * @param height		added to the current height of this size
	 * @see #add(PSize)
	 * @see #addWidth(int)
	 * @see #addHeight(int)
	 */
	public void add(int width, int height) {
		addWidth(width);
		addHeight(height);
	}
	
	/**
	 * <p>Adds the width and height of the given size to this size. 
	 * No sanity checks are performed. 
	 * The resulting width or height may be negative as 
	 * a result of this method call.</p>
	 * @param other			a non-null instance of PSize from which the width and height are taken
	 * @see #add(int, int)
	 * @see #addWidth(PSize)
	 * @see #addHeight(PSize)
	 */
	public void add(PSize other) {
		ThrowException.ifNull(other, "other == null");
		add(other.getWidth(), other.getHeight());
	}
	
	/**
	 * <p>Adds the given value to the width of this size. 
	 * No sanity checks are performed. 
	 * The resulting width may be negative as a result of this method call.</p>
	 * @param value			the value added to the width of this size			
	 * @see #add(int, int)
	 * @see #addWidth(PSize)
	 */
	public void addWidth(int value) {
		setWidth(getWidth() + value);
	}
	
	/**
	 * <p>Adds the width of the given size to the width of this size.</p>
	 * @param other			a non-null instance of PSize from which the width is taken
	 * @see #add(PSize)
	 * @see #addWidth(int)
	 */
	public void addWidth(PSize other) {
		ThrowException.ifNull(other, "other == null");
		addWidth(other.getWidth());
	}
	
	/**
	 * <p>Adds the given value to the height of this size. 
	 * No sanity checks are performed. 
	 * The resulting height may be negative as a result of this method call.</p>
	 * @param value			the value added to the height of this size			
	 * @see #add(int, int)
	 * @see #addHeight(PSize)
	 */
	public void addHeight(int value) {
		setHeight(getHeight() + value);
	}
	
	/**
	 * <p>Adds the height of the given size to the height of this size.</p>
	 * @param other			a non-null instance of PSize from which the height is taken
	 * @see #add(PSize)
	 * @see #addHeight(int)
	 */
	public void addHeight(PSize other) {
		ThrowException.ifNull(other, "other == null");
		addHeight(other.getHeight());
	}
	
}