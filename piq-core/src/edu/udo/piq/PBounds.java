package edu.udo.piq;

import edu.udo.piq.tools.AbstractPBounds;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.ImmutablePPoint;
import edu.udo.piq.tools.MutablePBounds;
import edu.udo.piq.tools.MutablePPoint;
import edu.udo.piq.util.PiqUtil;

/**
 * <p>Represents a bounding box defined by an {@link #getX() x-} and {@link #getY() y-coordinate} 
 * as well as a {@link #getWidth() width} and {@link #getHeight() height} or alternatively a 
 * second {@link #getFinalX() x-} and {@link #getFinalY() y-coordinate}. The horizontal axis (x) 
 * goes from left to right while the vertical axis (y) goes from top to bottom. The origin is at 
 * the top-left corner of the {@link PRoot#getBounds() bounds} of a {@link PRoot}</p>
 * 
 * <p>The most important use for PBounds objects is to describe {@link PComponent#getBounds() 
 * the bounds of components}.</p>
 * 
 * <p>Two important implementations of this interface are {@link ImmutablePBounds} and 
 * {@link MutablePBounds}. Instances of ImmutablePBounds should be preferred as return values. 
 * Instances of MutablePBounds can be used as output parameters for methods such as 
 * {@link PiqUtil#fillClippedBounds(MutablePBounds, PComponent)} and similar.</p>
 * 
 * <p>All subclasses should inherit from {@link AbstractPBounds} for a consistent implementation 
 * of {@link #toString()}, {@link #hashCode()} and {@link #equals(Object)}.</p>
 * 
 * @author Nic Starzi
 */
public interface PBounds extends PSize {
	
	/**
	 * <p>The x coordinate of the left edge of the rectangle defined by this PBounds 
	 * instance. The x-coordinates grow from left to right.</p>
	 * <p>This value should be equal to <code>{@link #getFinalX()} - {@link #getWidth()}</code></p>
	 * @return		the x-coordinate of the left edge of these bounds
	 */
	public int getX();
	
	/**
	 * <p>The y coordinate of the top edge of the rectangle defined by this PBounds 
	 * instance. The y-coordinates grow from top to bottom.</p>
	 * <p>This value should be equal to <code>{@link #getFinalY()} - {@link #getHeight()}</code></p>
	 * @return		the y-coordinate of the top edge of these bounds
	 */
	public int getY();
	
	/**
	 * <p>The size along the x-axis of the rectangle defined by this PBounds
	 * instance.</p>
	 * <p>This value should be equal to <code>{@link #getFinalX()} - {@link #getX()}</code></p>
	 * @return		the distance between the left and right edges of these bounds
	 */
	@Override
	public int getWidth();
	
	/**
	 * <p>The size along the y-axis of the rectangle defined by this PBounds
	 * instance.</p>
	 * <p>This value should be equal to <code>{@link #getFinalY()} - {@link #getY()}</code></p>
	 * @return		the distance between the top and bottom edges of these bounds
	 */
	@Override
	public int getHeight();
	
	/**
	 * <p>The x coordinate of the right edge of the rectangle defined by this PBounds 
	 * instance. The x-coordinates grow from left to right.</p>
	 * <p>This value should be equal to <code>{@link #getX()} + {@link #getWidth()}</code></p>
	 * @return		the x-coordinate of the right edge of these bounds
	 */
	public default int getFinalX() {
		return getX() + getWidth();
	}
	
	/**
	 * <p>The y coordinate of the bottom edge of the rectangle defined by this PBounds 
	 * instance. The y-coordinates grow from top to bottom.</p>
	 * <p>This value should be equal to <code>{@link #getY()} + {@link #getHeight()}</code></p>
	 * @return		the y-coordinate of the bottom edge of these bounds
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
	
	public default PBounds makeImmutableCopy() {
		return new ImmutablePBounds(this);
	}
	
	/**
	 * <p>Returns an instance of {@link PPoint} with its x at the {@link #getX() 
	 * x-} and its y at the {@link #getY() y-}coordinate of these bounds.</p>
	 * <p>No assumptions should be made about whether the returned 
	 * point is {@link MutablePPoint mutable} or {@link ImmutablePPoint not}. 
	 * A new point may or may not be created for each call of this method.</p>
	 * @return		a non-null instance of PPoint with coordinates of (x, y)
	 * @see #getUpperLeft(MutablePPoint)
	 */
	public default PPoint getUpperLeft() {
		return new ImmutablePPoint(getX(), getY());
	}
	
	/**
	 * <p>Returns an instance of {@link PPoint} with its x at the {@link #getFinalX() 
	 * final x-} and its y at the {@link #getY() y-}coordinate of these bounds.</p>
	 * <p>No assumptions should be made about whether the returned 
	 * point is {@link MutablePPoint mutable} or {@link ImmutablePPoint not}. 
	 * A new point may or may not be created for each call of this method.</p>
	 * @return		a non-null instance of PPoint with coordinates of (x + width, y)
	 * @see #getUpperRight(MutablePPoint)
	 */
	public default PPoint getUpperRight() {
		return new ImmutablePPoint(getFinalX(), getY());
	}
	
	/**
	 * <p>Returns an instance of {@link PPoint} with its x at the {@link #getX() 
	 * x-} and its y at the {@link #getFinalY() final y-}coordinate of these bounds.</p>
	 * <p>No assumptions should be made about whether the returned 
	 * point is {@link MutablePPoint mutable} or {@link ImmutablePPoint not}. 
	 * A new point may or may not be created for each call of this method.</p>
	 * @return		a non-null instance of PPoint with coordinates of (x, y + height)
	 * @see #getLowerLeft(MutablePPoint)
	 */
	public default PPoint getLowerLeft() {
		return new ImmutablePPoint(getX(), getFinalY());
	}
	
	public default PPoint getLowerRight() {
		return new ImmutablePPoint(getFinalX(), getFinalY());
	}
	
	public default PPoint getCenter() {
		return new ImmutablePPoint(getCenterX(), getCenterY());
	}
	
	/**
	 * <p>Returns an instance of {@link PPoint} with the same x- and 
	 * y-coordinates as these bounds.</p>
	 * <p>No assumptions should be made about whether the returned 
	 * point is {@link MutablePPoint mutable} or {@link ImmutablePPoint not}. 
	 * A new point may or may not be created for each call of this method.</p>
	 * @param out	an output parameter. If out is not null it will be returned by this method
	 * @return		a non-null instance of PPoint with the same x and y as these bounds. This is a reference to out if out is not null
	 * @see #getUpperLeft(MutablePPoint)
	 */
	public default PPoint getUpperLeft(MutablePPoint out) {
		return PPoint.setOrMake(out, getX(), getY());
	}
	
	public default PPoint getUpperRight(MutablePPoint out) {
		return PPoint.setOrMake(out, getFinalX(), getY());
	}
	
	public default PPoint getLowerLeft(MutablePPoint out) {
		return PPoint.setOrMake(out, getX(), getFinalY());
	}
	
	public default PPoint getLowerRight(MutablePPoint out) {
		return PPoint.setOrMake(out, getFinalX(), getFinalY());
	}
	
	public default PPoint getCenter(MutablePPoint out) {
		return PPoint.setOrMake(out, getCenterX(), getCenterY());
	}
	
	public default boolean contains(PPoint point) {
		return contains((int) point.getX(), (int) point.getY());
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
	
	public default boolean fullyContains(PBounds other) {
		return contains(other.getX(), other.getY()) && contains(other.getFinalX(), other.getFinalY());
	}
	
	public default boolean isOverlapping(PBounds other) {
		int x1 = getX();
		int y1 = getY();
		int fx1 = getFinalX();
		int fy1 = getFinalY();
		int x2 = other.getX();
		int y2 = other.getY();
		int fx2 = other.getFinalX();
		int fy2 = other.getFinalY();
		return !(x1 > fx2 || y1 > fy2 || fx1 < x2 || fy1 < y2);
	}
	
	/**
	 * Creates and returns a new instance of {@link PBounds} that is the
	 * intersection between these bounds and the other bounds.<br>
	 * If these two PBounds are disjunct, that means there is no intersection,
	 * null will be returned.<br>
	 * @param other		a non-null instance of PBounds
	 * @return			a new PBounds object or null if no intersection exists
	 */
	public default PBounds createIntersection(PBounds other) {
		return fillIntersection(other, null);
	}
	
	public default PBounds fillIntersection(PBounds other, MutablePBounds result) {
		int x = Math.max(getX(), other.getX());
		int y = Math.max(getY(), other.getY());
		int fx = Math.min(getFinalX(), other.getFinalX());
		int fy = Math.min(getFinalY(), other.getFinalY());
		int w = fx - x;
		int h = fy - y;
		if (w < 0 || h < 0) {
			return null;
		}
		if (result == null) {
			return new ImmutablePBounds(x, y, w, h);
		}
		result.setX(x);
		result.setY(y);
		result.setWidth(w);
		result.setHeight(h);
		return result;
	}
	
	public default boolean hasIntersection(PBounds other) {
		int x = Math.max(getX(), other.getX());
		int y = Math.max(getY(), other.getY());
		int fx = Math.min(getFinalX(), other.getFinalX());
		int fy = Math.min(getFinalY(), other.getFinalY());
		int w = fx - x;
		int h = fy - y;
		return w > 0 && h > 0;
	}
	
	@Override
	public default PBounds scaleCopy(double scale) {
		return new ImmutablePBounds(getX(), getY(), (int) (getWidth() * scale), (int) (getHeight() * scale));
	}
	
	public default PBounds createCopyAndSubtract(PInsets insets) {
		if (insets.isEmpty()) {
			return this;
		}
		int x = getX() + insets.getFromLeft();
		int fx = getFinalX() - insets.getFromRight();
		if (fx < x) {
			int tmp = fx;
			fx = x;
			x = tmp;
		}
		int y = getY() + insets.getFromTop();
		int fy = getFinalY() - insets.getFromBottom();
		if (fy < y) {
			int tmp = fy;
			fy = y;
			y = tmp;
		}
		int w = fx - x;
		int h = fy - y;
		return new ImmutablePBounds(x, y, w, h);
	}
}