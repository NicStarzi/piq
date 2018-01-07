package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.tools.ImmutablePInsets;

public class PBoxLayout extends AbstractMapPLayout {
	
	protected PInsets insets = new ImmutablePInsets(4, 4);
	private Box rootBox;
	
	public PBoxLayout(PComponent owner) {
		super(owner);
		rootBox = new Box();
	}
	
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
		Box box = (Box) data.getConstraint();
		if (box.isBox()) {
			throw new IllegalArgumentException("constraint.isBox()=true");
		}
	}
	
	public void setInsets(PInsets value) {
		if (value == null) {
			throw new IllegalArgumentException("insets="+value);
		}
		insets = new ImmutablePInsets(value);
		invalidate();
	}
	
	public PInsets getInsets() {
		return getStyleAttribute(PReadOnlyLayout.ATTRIBUTE_KEY_INSETS, insets);
	}
	
	public Box getRootBox() {
		return rootBox;
	}
	
	@Override
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof Box
				&& !((Box) constraint).isBox()
				&& getChildForConstraint(constraint) == null;
	}
	
	@Override
	protected void layOutInternal() {
		PBounds ob = getOwner().getBounds();
		PInsets insets = getInsets();
		int x = ob.getX() + insets.getFromLeft();
		int y = ob.getY() + insets.getFromRight();
		int w = ob.getWidth() - insets.getHorizontal();
		int h = ob.getHeight() - insets.getVertical();
		
		Box root = getRootBox();
		if (root.isBox()) {
			recursiveLayOut(root, x, y, w, h);
		} else {
			setChildCellFilled(getChildForConstraint(root), x, y, w, h);
		}
	}
	
	private void recursiveLayOut(Box box, int x, int y, int w, int h) {
		int x1 = x;
		int y1 = y;
		int x2;
		int y2;
		int w1;
		int w2;
		int h1;
		int h2;
		if (box.isHorizontal()) {
			int hNoGap = h - box.gap;
			w1 = w;
			w2 = w;
			h1 = (int) Math.ceil(hNoGap * box.getTopWeight());
			h2 = hNoGap - h1;
			x2 = x;
			y2 = y + h1 + box.gap;
		} else {
			int wNoGap = w - box.gap;
			w1 = (int) Math.ceil(wNoGap * box.getLeftWeight());
			w2 = wNoGap - w1;
			h1 = h;
			h2 = h;
			x2 = x + w1 + box.gap;
			y2 = y;
		}
		Box one = box.one;
		if (one.isBox()) {
			recursiveLayOut(one, x1, y1, w1, h1);
		} else {
			PComponent child = getChildForConstraint(one);
			setChildCellFilled(child, x1, y1, w1, h1);
		}
		Box other = box.other;
		if (other.isBox()) {
			recursiveLayOut(other, x2, y2, w2, h2);
		} else {
			PComponent child = getChildForConstraint(other);
			setChildCellFilled(child, x2, y2, w2, h2);
		}
	}
	
	@Override
	protected void onInvalidated() {
		Box root = getRootBox();
		int w = recursiveGetPrefW(root);
		int h = recursiveGetPrefH(root);
		prefSize.setWidth(w);
		prefSize.setHeight(h);
	}
	
	private int recursiveGetPrefW(Box cell) {
		if (cell.isBox()) {
			Box box = cell;
			return recursiveGetPrefW(box.one) + recursiveGetPrefW(box.other) + box.gap;
		}
		return getPreferredSizeOf(getChildForConstraint(cell)).getWidth();
	}
	
	private int recursiveGetPrefH(Box cell) {
		if (cell.isBox()) {
			Box box = cell;
			return recursiveGetPrefH(box.one) + recursiveGetPrefH(box.other) + box.gap;
		}
		return getPreferredSizeOf(getChildForConstraint(cell)).getHeight();
	}
	
	public static class Box {
		
		protected double weightOfFirst;
		protected int gap;
		protected boolean horizontal;
		protected Box parent;
		protected Box one;
		protected Box other;
		
		public void splitHorizontal(double weight) {
			splitHorizontal(weight, 0);
		}
		
		public void splitHorizontal(double weight, int gap) {
			if (one != null) {
				throw new IllegalStateException("Box is already split");
			} if (weight < 0 || weight > 1.0) {
				throw new IllegalArgumentException("weight="+weight);
			} if (gap < 0) {
				throw new IllegalArgumentException("gap="+gap);
			}
			this.gap = gap;
			one = new Box();
			one.parent = this;
			other = new Box();
			other.parent = this;
			horizontal = true;
			weightOfFirst = weight;
		}
		
		public void splitVertical(double weight) {
			splitVertical(weight, 0);
		}
		
		public void splitVertical(double weight, int gap) {
			if (one != null) {
				throw new IllegalStateException("Box is already split");
			} if (weight < 0 || weight > 1.0) {
				throw new IllegalArgumentException("weight="+weight);
			} if (gap < 0) {
				throw new IllegalArgumentException("gap="+gap);
			}
			this.gap = gap;
			one = new Box();
			one.parent = this;
			other = new Box();
			other.parent = this;
			horizontal = false;
			weightOfFirst = weight;
		}
		
		public boolean isHorizontal() {
			if (one == null) {
				throw new IllegalStateException("Box is not split.");
			}
			return horizontal;
		}
		
		public boolean isVertical() {
			if (one == null) {
				throw new IllegalStateException("Box is not split.");
			}
			return !isHorizontal();
		}
		
		public double getLeftWeight() {
			throwExceptionIfNotSplit(false);
			return weightOfFirst;
		}
		
		public Box getLeft() {
			throwExceptionIfNotSplit(false);
			return one;
		}
		
		public double getRightWeight() {
			throwExceptionIfNotSplit(false);
			return 1.0 - weightOfFirst;
		}
		
		public Box getRight() {
			throwExceptionIfNotSplit(false);
			return other;
		}
		
		public double getTopWeight() {
			throwExceptionIfNotSplit(true);
			return weightOfFirst;
		}
		
		public Box getTop() {
			throwExceptionIfNotSplit(true);
			return one;
		}
		
		public double getBottomWeight() {
			throwExceptionIfNotSplit(true);
			return 1.0 - weightOfFirst;
		}
		
		public Box getBottom() {
			throwExceptionIfNotSplit(true);
			return other;
		}
		
		private void throwExceptionIfNotSplit(boolean horizontal) {
			if (one == null || other == null) {
				throw new IllegalStateException("Box is not split.");
			} if (!horizontal && !isVertical()) {
				throw new IllegalStateException("isVertical()=false");
			} if (horizontal && !isHorizontal()) {
				throw new IllegalStateException("isHorizontal()=false");
			}
		}
		
		public boolean isBox() {
			return one != null;
		}
		
	}
	
}