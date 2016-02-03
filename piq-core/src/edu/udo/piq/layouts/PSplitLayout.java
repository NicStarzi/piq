package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractEnumPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PSplitLayout extends AbstractEnumPLayout<PSplitLayout.Constraint> {
	
	public static final Orientation DEFAULT_ORIENTATION = Orientation.HORIZONTAL;
	public static final double DEFAULT_SPLIT_POSITON = 0.5;
	
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize = new MutablePSize();
	protected Orientation ori = DEFAULT_ORIENTATION;
	protected double splitPos = DEFAULT_SPLIT_POSITON;
	
	public PSplitLayout(PComponent component) {
		super(component, Constraint.class);
	}
	
	public void setOrientation(Orientation orientation) {
		if (orientation == null) {
			throw new NullPointerException();
		}
		if (ori != orientation) {
			ori = orientation;
			fireInvalidateEvent();
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
			fireInvalidateEvent();
		}
	}
	
	public double getSplitPosition() {
		return splitPos;
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
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
			
			setChildBounds(first, x, y, firstW, h);
			setChildBounds(second, x + firstW + dividerW, y, secondW, h);
			setChildBounds(divider, x + firstW, y, dividerW, h);
		} else {
			int dividerH = getPreferredSizeOf(divider).getHeight();
			int compH = h - dividerH;
			int firstH = (int) Math.round(compH * splitPos);
			int secondH = compH - firstH;
			
			setChildBounds(first, x, y, w, firstH);
			setChildBounds(second, x, y + firstH + dividerH, w, secondH);
			setChildBounds(divider, x, y + firstH, w, dividerH);
		}
	}
	
	public PSize getPreferredSize() {
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
		return prefSize;
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