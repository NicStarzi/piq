package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.AbstractPLayoutObs;
import edu.udo.piq.tools.MutablePSize;

public class PBoxLayout extends AbstractPLayout {
	
	protected final MutablePSize prefSize;
	private Box rootBox;
	
	public PBoxLayout(PComponent owner) {
		super(owner);
		prefSize = new MutablePSize();
		
		addObs(new AbstractPLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				Box box = (Box) constraint;
				if (box.isBox()) {
					throw new IllegalArgumentException("constraint.isBox()=true");
				}
			}
		});
		rootBox = new Box();
	}
	
	public Box getRootBox() {
		return rootBox;
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof Box 
				&& !((Box) constraint).isBox() 
				&& getChildForConstraint(constraint) == null;
	}
	
	public void layOut() {
		PBounds ob = getOwnerBounds();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		
		Box root = getRootBox();
		if (root.isBox()) {
			recursiveLayOut(root, x, y, w, h);
		} else {
			setChildBounds(getChildForConstraint(root), x, y, w, h);
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
			w1 = w;
			w2 = w;
			h1 = (int) Math.ceil(h * box.getTopWeight());
			h2 = h - h1;
			x2 = x;
			y2 = y + h1;
		} else {
			w1 = (int) Math.ceil(w * box.getLeftWeight());
			w2 = w - w1;
			h1 = h;
			h2 = h;
			x2 = x + w1;
			y2 = y;
		}
		Box one = box.one;
		if (one.isBox()) {
			recursiveLayOut(one, x1, y1, w1, h1);
		} else {
			PComponent child = getChildForConstraint(one);
			setChildBounds(child, x1, y1, w1, h1);
		}
		Box other = box.other;
		if (other.isBox()) {
			recursiveLayOut(other, x2, y2, w2, h2);
		} else {
			PComponent child = getChildForConstraint(other);
			setChildBounds(child, x2, y2, w2, h2);
		}
	}
	
	public PSize getPreferredSize() {
		Box root = getRootBox();
		int w = recursiveGetPrefW(root);
		int h = recursiveGetPrefH(root);
		prefSize.setWidth(w);
		prefSize.setHeight(h);
		return prefSize;
	}
	
	private int recursiveGetPrefW(Box cell) {
		if (cell.isBox()) {
			Box box = cell;
			return recursiveGetPrefW(box.one) + recursiveGetPrefW(box.other);
		}
		return getPreferredSizeOf(getChildForConstraint(cell)).getWidth();
	}
	
	private int recursiveGetPrefH(Box cell) {
		if (cell.isBox()) {
			Box box = cell;
			return recursiveGetPrefH(box.one) + recursiveGetPrefH(box.other);
		}
		return getPreferredSizeOf(getChildForConstraint(cell)).getHeight();
	}
	
	public static class Box {
		
		double weightOfFirst;
		boolean horizontal;
		Box parent;
		Box one;
		Box other;
		
		public void splitHorizontal(double weight) {
			if (one != null) {
				throw new IllegalStateException("Box is already split");
			} if (weight < 0 || weight > 1.0) {
				throw new IllegalArgumentException("weight == "+weight);
			}
			one = new Box();
			one.parent = this;
			other = new Box();
			other.parent = this;
			horizontal = true;
			weightOfFirst = weight;
		}
		
		public void splitVertical(double weight) {
			if (one != null) {
				throw new IllegalStateException("Box is already split");
			} if (weight < 0 || weight > 1.0) {
				throw new IllegalArgumentException("weight == "+weight);
			}
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