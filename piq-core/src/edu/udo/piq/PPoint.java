package edu.udo.piq;

import edu.udo.piq.tools.AbstractPPoint;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.ImmutablePPoint;
import edu.udo.piq.tools.MutablePPoint;

/**
 * <p>Represents a location in 2D space. A point has an {@link #getX() x-} and
 * {@link #getY() y-coordinate}. Points have no size.</p>
 * 
 * <p>Two important implementations of this interface are {@link ImmutablePPoint} and
 * {@link MutablePPoint}. Instances of ImmutablePPoints should be preferred as return values.
 * Instances of MutablePPoints can be used as output parameters for methods such as
 * {@link PBounds#getCenter(MutablePPoint)} and similar.</p>
 * 
 * <p>All subclasses should inherit from {@link AbstractPPoint} for a consistent implementation
 * of {@link #toString()}, {@link #hashCode()} and {@link #equals(Object)}.</p>
 * 
 * @author Nic Starzi
 */
public interface PPoint {
	
	/**
	 * <p>If the given out parameter is null a new {@link ImmutablePPoint} will be created
	 * with the given coordinates and returned. If the out parameter is not null it will
	 * have its coordinates set to the given coordinates and then be returned.</p>
	 * 
	 * @param out		an output parameter. If null a new point will be created, if not out will be returned
	 * @param x			the x coordinate of the returned point
	 * @param y			the y coordinate of the returned point
	 * @return			a non-null instance of PPoint with the given x- and y-coordinates
	 */
	public static PPoint setOrMake(MutablePPoint out, double x, double y) {
		if (out == null) {
			return new ImmutablePPoint(x, y);
		}
		out.set(x, y);
		return out;
	}
	
	/**
	 * <p>An immutable constant for the origin, a Point at (0, 0).</p>
	 */
	public static final PPoint ORIGIN = new ImmutablePPoint(0, 0);
	
	/**
	 * <p>Returns the coordinate on the x-axis (going from left to right).
	 * This may be positive or negative values or zero.</p>
	 * @return		the coordinate on the horizontal (from left to right) axis
	 */
	public double getX();
	
	/**
	 * <p>Returns the coordinate on the y-axis (going from top to bottom).
	 * This may be positive or negative values or zero.</p>
	 * @return		the coordinate on the vertical (from top to bottom) axis
	 */
	public double getY();
	
	public default PPoint makeImmutableCopy() {
		return new ImmutablePPoint(this);
	}
	
	/**
	 * <p>Returns the distance from this point to another.
	 * The returned distance is never a negative value.</p>
	 * @param other		a non-null instance of PPoint
	 * @return			the distance from this point to the given point
	 * @see #getDistanceTo(double, double)
	 */
	public default double getDistanceTo(PPoint other) {
		return getDistanceTo(other.getX(), other.getY());
	}
	
	/**
	 * <p>Returns the angle in radians of a line from this point to the other point.
	 * The returned value is always within the range of -PI to +PI.</p>
	 * @param other		a non-null instance of PPoint
	 * @return			the angle in radians from this point to the given point
	 * @see #getAngleInRadTo(double, double)
	 */
	public default double getAngleInRadTo(PPoint other) {
		return getAngleInRadTo(other.getX(), other.getY());
	}
	
	/**
	 * <p>Returns the angle in degrees of a line from this point to the other point.
	 * The returned value is always within the range of 0 to 360.</p>
	 * @param other		a non-null instance of PPoint
	 * @return			the angle in degrees from this point to the given point
	 * @see #getAngleInDegTo(double, double)
	 */
	public default double getAngleInDegTo(PPoint other) {
		return getAngleInDegTo(other.getX(), other.getY());
	}
	
	/**
	 * <p>Returns the distance from this point to the given x and y coordinates.
	 * The returned distance is never a negative value.</p>
	 * @param x			a coordinate on the x-axis (from left to right)
	 * @param y			a coordinate on the y-axis (from top to bottom)
	 * @return			the distance from this point to the given coordinates
	 * @see #getDistanceTo(PPoint)
	 */
	public default double getDistanceTo(double x, double y) {
		double diffX = Math.abs(getX() - x);
		double diffY = Math.abs(getY() - y);
		return Math.sqrt(diffX*diffX + diffY*diffY);
	}
	
	/**
	 * <p>Returns the angle in radians of a line from this point to a point at
	 * the given coordinates. The returned value is always within the range of
	 * -PI to +PI.</p>
	 * @param x			a coordinate on the x-axis (from left to right)
	 * @param y			a coordinate on the y-axis (from top to bottom)
	 * @return			the angle in radians from this point to the given point
	 * @see #getAngleInRadTo(double, double)
	 */
	public default double getAngleInRadTo(double x, double y) {
		double diffX = x - getX();
		double diffY = y - getY();
		double theta = Math.atan2(diffY, diffX);
		return theta;
	}
	
	/**
	 * <p>Returns the angle in degrees of a line from this point to a point at
	 * the given coordinates. The returned value is always within the range of
	 * 0 to 360.</p>
	 * @param x			a coordinate on the x-axis (from left to right)
	 * @param y			a coordinate on the y-axis (from top to bottom)
	 * @return			the angle in degrees from this point to the given point
	 * @see #getAngleInDegTo(PPoint)
	 */
	public default double getAngleInDegTo(double x, double y) {
		double angleInRad = getAngleInRadTo(x, y);
		double angleInDeg = Math.toDegrees(angleInRad);
		if (angleInDeg < 0) {
			angleInDeg += 360;
		}
		return angleInDeg;
	}
	
	/**
	 * <p>Spans a rectangle around this point and the other so that both points are
	 * contained within it. This method will create a new instance of {@link PBounds}
	 * when called. No assumptions should be made about the {@link ImmutablePBounds
	 * mutability} of the returned bounds. The bounds will not be updated if either
	 * this point or the other change their coordinates after the bounds have been
	 * created.</p>
	 * @param other			a second point used to constrain the returned bounds. Must be non-null
	 * @return				a new instance of PBounds which encompasses both this point and the other
	 */
	public default PBounds createBounds(PPoint other) {
		int minX;
		int minY;
		int maxX;
		int maxY;
		double x1 = getX();
		double x2 = other.getX();
		double y1 = getY();
		double y2 = other.getY();
		if (x1 < x2) {
			minX = (int) x1;
			maxX = (int) Math.ceil(x2);
		} else {
			minX = (int) x2;
			maxX = (int) Math.ceil(x1);
		}
		if (y1 < y2) {
			minY = (int) y1;
			maxY = (int) Math.ceil(y2);
		} else {
			minY = (int) y2;
			maxY = (int) Math.ceil(y1);
		}
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		return new ImmutablePBounds(minX, minY, width, height);
	}
	
	/**
	 * <p>Returns a new instance of PPoint at a position relative to the position of
	 * this point translated by the given offsets. The returned point is
	 * {@link ImmutablePPoint immutable}.</p>
	 * @param offsetX		added to the x-coordinate of this point
	 * @param offsetY		added to the y-coordinate of this point
	 * @return				a new point at {@code (getX() + offsetX, getY() + offsetY)}
	 * @see #getTranslatedAlongLine(double, double)
	 */
	public default PPoint getTranslatedBy(double offsetX, double offsetY) {
		return new ImmutablePPoint(getX() + offsetX, getY() + offsetY);
	}
	
	/**
	 * <p>Returns an instance of PPoint at a position relative to the position of
	 * this point translated by the given offsets. If out is null a new point is
	 * instantiated and returned. If out is not null out will be returned.</p>
	 * @param offsetX		added to the x-coordinate of this point
	 * @param offsetY		added to the y-coordinate of this point
	 * @param out			an output parameter. If not null it will be set and returned.
	 * @return				a point at {@code (getX() + offsetX, getY() + offsetY)}
	 * @see #getTranslatedAlongLine(double, double)
	 */
	public default PPoint getTranslatedBy(double offsetX, double offsetY, MutablePPoint out) {
		if (out == null) {
			return new ImmutablePPoint(getX() + offsetX, getY() + offsetY);
		}
		out.set(getX() + offsetX, getY() + offsetY);
		return out;
	}
	
	/**
	 * <p>Returns a new instance of PPoint at a position relative to the position of
	 * this point translated along a line with the given angle over the given distance.
	 * The returned point is {@link ImmutablePPoint immutable}.</p>
	 * @param angleInDeg	the angle (in degrees) of the line along which the new point lies
	 * @param distance		the distance between this point and the returned point
	 * @return				a new point which lies on a line from this point with the given angle and distance
	 * @see #getTranslatedBy(double, double)
	 */
	public default PPoint getTranslatedAlongLine(double angleInDeg, double distance) {
		double ox = getOffsetInDegX(angleInDeg, distance);
		double oy = getOffsetInDegY(angleInDeg, distance);
		return getTranslatedBy(ox, oy);
	}
	
	public default double getOffsetInDegX(double angleInDeg, double distance) {
		return getOffsetInRadX(Math.toRadians(angleInDeg), distance);
	}
	
	public default double getOffsetInRadX(double angleInRad, double distance) {
		return getX() + Math.cos(angleInRad) * distance;
	}
	
	public default double getOffsetInDegY(double angleInDeg, double distance) {
		return getOffsetInRadY(Math.toRadians(angleInDeg), distance);
	}
	
	public default double getOffsetInRadY(double angleInRad, double distance) {
		return getY() + Math.sin(angleInRad) * distance;
	}
	
}