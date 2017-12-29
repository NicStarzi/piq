package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.util.ThrowException;

public class PSplitLayout extends AbstractEnumPLayout<PSplitLayout.Constraint> {
	
	public static final Orientation DEFAULT_ORIENTATION = Orientation.HORIZONTAL;
	public static final double DEFAULT_SPLIT_POSITON = 0.5;
	
	protected Orientation ori = DEFAULT_ORIENTATION;
	protected double splitPos = DEFAULT_SPLIT_POSITON;
	
	public PSplitLayout(PComponent component) {
		super(component, Constraint.class);
	}
	
	public PComponent getDivider() {
		return getChildForConstraint(Constraint.DIVIDER);
	}
	
	public void setOrientation(Orientation orientation) {
		if (orientation == null) {
			throw new NullPointerException();
		}
		if (ori != orientation) {
			ori = orientation;
			invalidate();
		}
	}
	
	public Orientation getOrientation() {
		return ori;
	}
	
	public void setSplitPosition(double value) {
		if (value < 0) {
			value = 0;
		} else if (value > 1) {
			value = 1;
		}
		if (splitPos != value) {
			splitPos = value;
			invalidate();
		}
	}
	
	public double getSplitPosition() {
		return splitPos;
	}
	
	@Override
	protected void layOutInternal() {
		PBounds ob = getOwner().getBoundsWithoutBorder();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		
		PComponent first = getChildForConstraint(Constraint.FIRST);
		PComponent divider = getChildForConstraint(Constraint.DIVIDER);
		PComponent second = getChildForConstraint(Constraint.SECOND);
		if (getOrientation() == Orientation.HORIZONTAL) {
			int dividerW = getPreferredSizeOf(divider).getWidth();
			int compW = w - dividerW;
			int firstW = (int) Math.round(compW * splitPos);
			int secondW = compW - firstW;
			
			setChildCellFilled(first, x, y, firstW, h);
			setChildCellFilled(second, x + firstW + dividerW, y, secondW, h);
			setChildCellFilled(divider, x + firstW, y, dividerW, h);
		} else {
			int dividerH = getPreferredSizeOf(divider).getHeight();
			int compH = h - dividerH;
			int firstH = (int) Math.round(compH * splitPos);
			int secondH = compH - firstH;
			
			setChildCellFilled(first, x, y, w, firstH);
			setChildCellFilled(second, x, y + firstH + dividerH, w, secondH);
			setChildCellFilled(divider, x, y + firstH, w, dividerH);
		}
	}
	
	@Override
	protected void onInvalidated() {
		PComponent first = getChildForConstraint(Constraint.FIRST);
		PComponent divider = getChildForConstraint(Constraint.DIVIDER);
		PComponent second = getChildForConstraint(Constraint.SECOND);
		PSize firstSize = getPreferredSizeOf(first);
		PSize secondSize = getPreferredSizeOf(second);
		PSize dividerSize = getPreferredSizeOf(divider);
		int prefW;
		int prefH;
		if (getOrientation() == Orientation.HORIZONTAL) {
			prefW = firstSize.getWidth() + secondSize.getWidth() + dividerSize.getWidth();
			prefH = Math.max(firstSize.getHeight(), secondSize.getHeight());
		} else {
			prefH = firstSize.getHeight() + secondSize.getHeight() + dividerSize.getHeight();
			prefW = Math.max(firstSize.getWidth(), secondSize.getWidth());
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
	}
	
	@Override
	protected void onChildPrefSizeChanged(PComponent child) {
		ThrowException.ifFalse(containsChild(child), "containsChild(child) == false");
		if (child == getDivider()) {
			invalidate();
		}
	}
	
	public static enum Orientation {
		HORIZONTAL,
		VERTICAL,
		;
	}
	
	public static enum Constraint {
		FIRST,
		SECOND,
		DIVIDER,
		;
	}
	
}