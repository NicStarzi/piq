package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PTupleLayout extends AbstractMapPLayout {
	
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize;
	protected Orientation orientation = Orientation.LEFT_TO_RIGHT;
	protected PComponent first;
	protected PComponent second;
	protected int gap = 4;
	
	public PTupleLayout(PComponent owner) {
		super(owner);
		prefSize = new MutablePSize();
		
		addObs(new PLayoutObs() {
			public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				if (constraint == Constraint.FIRST) {
					first = child;
				} else {
					second = child;
				}
			}
			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				if (child == first) {
					first = null;
				} else {
					second = null;
				}
			}
		});
	}
	
	public void setOrientation(Orientation orientation) {
		if (orientation == null) {
			throw new IllegalArgumentException();
		}
		this.orientation = orientation;
		fireInvalidateEvent();
	}
	
	public Orientation getOrientation() {
		return orientation;
	}
	
	public void setGap(int value) {
		gap = value;
		fireInvalidateEvent();
	}
	
	public int getGap() {
		return gap;
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint != null && constraint instanceof Constraint && getAt(constraint) == null;
	}
	
	public PComponent getAt(Object constraint) {
		if (constraint == Constraint.FIRST) {
			return first;
		} else if (constraint == Constraint.SECOND) {
			return second;
		}
		throw new IllegalArgumentException();
	}
	
	public void layOut() {
//		System.out.println(getClass().getSimpleName()+".reLayout prefW="+prefW+", prefH="+prefH);
//		System.out.println("first="+first+", second="+second);
		
		PSize prefSize = getPreferredSize();
		int prefW = prefSize.getWidth();
		int prefH = prefSize.getHeight();
		
		PBounds ob = getOwner().getBounds();
		int x = ob.getX() + ob.getWidth() / 2 - prefW / 2;
		int y = ob.getY() + ob.getHeight() / 2 - prefH / 2;
		
		PSize prefSizeFirst = getPreferredSizeOf(first);
		PSize prefSizeSecond = getPreferredSizeOf(second);
		int prefWfirst = prefSizeFirst.getWidth();
		int prefHfirst = prefSizeFirst.getHeight();
		int prefWsecond = prefSizeSecond.getWidth();
		int prefHsecond = prefSizeSecond.getHeight();
		
		if (getOrientation() == Orientation.LEFT_TO_RIGHT) {
			if (first != null) {
				int w = prefWfirst;
				int h = prefHfirst;
				int cy = y + prefH / 2 - h / 2;
				setChildBounds(first, x, cy, w, h);
				x += w + gap;
			}
			if (second != null) {
				int w = prefWsecond;
				int h = prefHsecond;
				int cy = y + prefH / 2 - h / 2;
				setChildBounds(second, x, cy, w, h);
			}
		} else {
			if (first != null) {
				int w = prefWfirst;
				int h = prefHfirst;
				setChildBounds(first, x, y, w, h);
				y += h + gap;
			}
			if (second != null) {
				int w = prefWsecond;
				int h = prefHsecond;
				setChildBounds(second, x, y, w, h);
			}
		}
	}
	
	public PSize getPreferredSize() {
		int prefW = 0;
		int prefH = 0;
		PSize sizeFirst = getPreferredSizeOf(first);
		PSize sizeSecond = getPreferredSizeOf(second);
		if (getOrientation() == Orientation.LEFT_TO_RIGHT) {
			prefW += sizeFirst.getWidth();
			prefW += sizeSecond.getWidth();
			if (first != null && second != null) {
				prefW += gap;
			}
			prefH = Math.max(sizeFirst.getHeight(), sizeSecond.getHeight());
		} else {
			prefW = Math.max(sizeFirst.getWidth(), sizeSecond.getWidth());
			prefH += sizeFirst.getHeight();
			prefH += sizeSecond.getHeight();
			if (first != null && second != null) {
				prefH += gap;
			}
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public static enum Constraint {
		FIRST,
		SECOND;
	}
	
	public static enum Orientation {
		LEFT_TO_RIGHT,
		TOP_TO_BOTTOM,
		;
	}
	
}