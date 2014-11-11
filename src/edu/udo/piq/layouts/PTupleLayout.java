package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.ImmutablePSize;

public class PTupleLayout extends AbstractPLayout {
	
	protected Orientation orientation = Orientation.LEFT_TO_RIGHT;
	protected PComponent first;
	protected PComponent second;
	protected int gap = 4;
	
	public PTupleLayout(PComponent owner) {
		super(owner);
		addObs(new PLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				if (constraint == Constraint.FIRST) {
					first = child;
				} else {
					second = child;
				}
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				if (child == first) {
					first = null;
				} else {
					second = null;
				}
			}
			public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
			}
			public void layoutInvalidated(PLayout layout) {
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
//		int prefW = getPreferredWidth();
//		int prefH = getPreferredHeight();
		
		PBounds ob = getOwnerBounds();
		int x = ob.getX() + ob.getWidth() / 2 - prefW / 2;
		int y = ob.getY() + ob.getHeight() / 2 - prefH / 2;
		
		PSize prefSizeFirst = getPreferredSizeOf(first);
		PSize prefSizeSecond = getPreferredSizeOf(second);
		int prefWfirst = prefSizeFirst.getWidth();
		int prefHfirst = prefSizeFirst.getHeight();
		int prefWsecond = prefSizeSecond.getWidth();
		int prefHsecond = prefSizeSecond.getHeight();
//		int prefWfirst = getPreferredWidthOf(first);
//		int prefHfirst = getPreferredHeightOf(first);
//		int prefWsecond = getPreferredWidthOf(second);
//		int prefHsecond = getPreferredHeightOf(second);
		
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
		return new ImmutablePSize(prefW, prefH);
	}
	
//	public int getPreferredWidth() {
//		if (getOrientation() == Orientation.LEFT_TO_RIGHT) {
//			int prefW = 0;
//			prefW += getPreferredWidthOf(first);
//			prefW += getPreferredWidthOf(second);
//			if (first != null && second != null) {
//				prefW += gap;
//			}
//			return prefW;
//		} else {
//			return Math.max(
//					getPreferredWidthOf(first), 
//					getPreferredWidthOf(second));
//		}
//	}
//	
//	public int getPreferredHeight() {
//		if (getOrientation() == Orientation.TOP_TO_BOTTOM) {
//			int prefH = 0;
//			prefH += getPreferredHeightOf(first);
//			prefH += getPreferredHeightOf(second);
//			if (first != null && second != null) {
//				prefH += gap;
//			}
//			return prefH;
//		} else {
//			return Math.max(
//					getPreferredHeightOf(first), 
//					getPreferredHeightOf(second));
//		}
//	}
	
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