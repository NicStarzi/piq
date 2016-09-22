package edu.udo.piq;

import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.ImmutablePPoint;

public interface PPoint {
	
	public static final PPoint ORIGIN = new ImmutablePPoint(0, 0);
	
	public double getX();
	
	public double getY();
	
	public default double getDistanceTo(PPoint other) {
		double diffX = Math.abs(getX() - other.getX());
		double diffY = Math.abs(getY() - other.getY());
		return Math.sqrt(diffX*diffX + diffY*diffY);
	}
	
	public default double getAngleInRadTo(PPoint other) {
		double diffX = other.getX() - getX();
		double diffY = other.getY() - getY(); 
		double theta = Math.atan2(diffY, diffX);
		return theta;
	}
	
	public default double getAngleInDegTo(PPoint other) {
		double angleInRad = getAngleInRadTo(other);
		double angleInDeg = Math.toDegrees(angleInRad);
		if (angleInDeg < 0) {
			angleInDeg += 360;
		}
		return angleInDeg;
	}
	
	public default PBounds createBounds(PPoint other) {
		int minX;
		int minY;
		int maxX;
		int maxY;
		int x1 = (int) getX();
		int x2 = (int) other.getX();
		int y1 = (int) getY();
		int y2 = (int) other.getY();
		if (x1 < x2) {
			minX = x1;
			maxX = x2;
		} else {
			minX = x2;
			maxX = x1;
		}
		if (y1 < y2) {
			minY = y1;
			maxY = y2;
		} else {
			minY = y2;
			maxY = y1;
		}
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		return new ImmutablePBounds(minX, minY, width, height);
	}
	
	public default PPoint getOffset(double offsetX, double offsetY) {
		return new ImmutablePPoint(getX() + offsetX, getY() + offsetY);
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