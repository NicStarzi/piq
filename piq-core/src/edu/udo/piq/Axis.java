package edu.udo.piq;

public enum Axis {
	
	X,
	Y,
	;
	
	public final int getCoordinate(PPoint point) {
		if (this == X) {
			return (int) point.getX();
		} else {
			return (int) point.getY();
		}
	}
	
	public final int getCoordinate(PMouse mouse) {
		if (this == X) {
			return mouse.getX();
		} else {
			return mouse.getY();
		}
	}
	
	public final int getSize(PInsets insets) {
		if (this == X) {
			return insets.getWidth();
		} else {
			return insets.getHeight();
		}
	}
	
	public final int getSize(PSize size) {
		if (this == X) {
			return size.getWidth();
		} else {
			return size.getHeight();
		}
	}
	
	public final int getFirstCoordinate(PBounds bounds) {
		if (this == X) {
			return bounds.getX();
		} else {
			return bounds.getY();
		}
	}
	
	public final int getFinalCoordinate(PBounds bounds) {
		if (this == X) {
			return bounds.getFinalX();
		} else {
			return bounds.getFinalY();
		}
	}
	
	public final int getCenterCoordinate(PBounds bounds) {
		if (this == X) {
			return bounds.getX() + bounds.getWidth() / 2;
		} else {
			return bounds.getY() + bounds.getHeight() / 2;
		}
	}
	
	public final int getSize(PComponent component) {
		return getSize(component.getBounds());
	}
	
	public final int getPreferredSize(PComponent component) {
		return getSize(component.getPreferredSize());
	}
	
	public final int getFirstCoordinate(PComponent component) {
		return getFirstCoordinate(component.getBounds());
	}
	
	public final int getFinalCoordinate(PComponent component) {
		return getFinalCoordinate(component.getBounds());
	}
	
	public final int getCenterCoordinate(PComponent component) {
		return getCenterCoordinate(component.getBounds());
	}
	
	public final boolean isOverlapping(PBounds bounds1, PBounds bounds2) {
		int x1 = getFirstCoordinate(bounds1);
		int fx1 = getFinalCoordinate(bounds1);
		int x2 = getFirstCoordinate(bounds2);
		int fx2 = getFinalCoordinate(bounds2);
		return !(x1 > fx2 || fx1 < x2);
	}
	
	public final boolean isOverlapping(PComponent component1, PComponent component2) {
		return isOverlapping(component1.getBounds(), component2.getBounds());
	}
	
}