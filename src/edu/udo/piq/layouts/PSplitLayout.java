package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.AbstractPLayoutObs;
import edu.udo.piq.tools.MutablePSize;

public class PSplitLayout extends AbstractPLayout {
	
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
	protected PComponent first;
	protected PComponent second;
	protected PComponent slider;
	
	public PSplitLayout(PComponent component) {
		super(component);
		addObs(new AbstractPLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				if (constraint == Constraint.FIRST) {
					first = child;
				} else if (constraint == Constraint.SECOND) {
					second = child;
				} else {
					slider = child;
				}
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				if (child == first) {
					first = null;
				} else if (child == second) {
					second = null;
				} else {
					slider = null;
				}
			}
		});
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
	
	public PComponent getAt(Constraint constr) {
		if (constr == Constraint.FIRST) {
			return first;
		} else if (constr == Constraint.SECOND) {
			return second;
		} else if (constr == Constraint.SLIDER) {
			return slider;
		} else {
			throw new IllegalArgumentException("constr="+constr);
		}
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof Constraint 
				&& getAt((Constraint) constraint) == null;
	}
	
	public void layOut() {
		PBounds ob = getOwnerBounds();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		
		if (getOrientation() == Orientation.HORIZONTAL) {
			int sliderW = getPreferredSizeOf(slider).getWidth();
			int compW = w - sliderW;
			int firstW = (int) Math.round(compW * splitPos);
			int secondW = compW - firstW;
			
			setChildBounds(first, x, y, firstW, h);
			setChildBounds(second, x + firstW + sliderW, y, secondW, h);
			setChildBounds(slider, x + firstW, y, sliderW, h);
		} else {
			int sliderH = getPreferredSizeOf(slider).getHeight();
			int compH = h - sliderH;
			int firstH = (int) Math.round(compH * splitPos);
			int secondH = compH - firstH;
			
			setChildBounds(first, x, y, w, firstH);
			setChildBounds(second, x, y + firstH + sliderH, w, secondH);
			setChildBounds(slider, x, y + firstH, w, sliderH);
		}
	}
	
	public PSize getPreferredSize() {
		PSize firstSize = getPreferredSizeOf(first);
		PSize secondSize = getPreferredSizeOf(second);
		PSize sliderSize = getPreferredSizeOf(slider);
		int prefW;
		int prefH;
		if (getOrientation() == Orientation.HORIZONTAL) {
			prefW = firstSize.getWidth() + secondSize.getWidth() + sliderSize.getWidth();
			prefH = Math.max(firstSize.getHeight(), secondSize.getHeight());
		} else {
			prefH = firstSize.getHeight() + secondSize.getHeight() + sliderSize.getHeight();
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
		SLIDER,
		;
	}
	
}