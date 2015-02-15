package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.tools.MutablePSize;

public class PMatrixLayout extends AbstractPLayout {
	
	protected final int gridW;
	protected final int gridH;
	protected final PComponent[] grid;
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize;
	protected PInsets insets = new ImmutablePInsets(4, 4);
	protected int gapH = 4;
	protected int gapV = 4;
	
	public PMatrixLayout(PComponent owner, int width, int height) {
		super(owner);
		gridW = width;
		gridH = height;
		grid = new PComponent[gridW * gridH];
		prefSize = new MutablePSize();
		
		addObs(new PLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				MatrixConstraint cell = (MatrixConstraint) constraint;
				grid[gridID(cell.getX(), cell.getY())] = child;
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				MatrixConstraint cell = (MatrixConstraint) constraint;
				grid[gridID(cell.getX(), cell.getY())] = null;
			}
		});
	}
	
	public void setInsets(PInsets value) {
		if (value == null) {
			throw new IllegalArgumentException("insets="+value);
		}
		insets = new ImmutablePInsets(value);
		fireInvalidateEvent();
	}
	
	public PInsets getInsets() {
		return insets;
	}
	
	public void setGap(int horizontal, int vertical) {
		gapH = horizontal;
		gapV = vertical;
		fireInvalidateEvent();
	}
	
	public int getHorizontalGap() {
		return gapH;
	}
	
	public int getVerticalGap() {
		return gapV;
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint != null && constraint instanceof MatrixConstraint 
				&& isValidCell((MatrixConstraint) constraint)
				&& getChildForConstraint((MatrixConstraint) constraint) == null;
	}
	
	public PComponent getChildForConstraint(Object constraint) {
		if (constraint == null || !(constraint instanceof MatrixConstraint)) {
			throw new IllegalArgumentException();
		}
		MatrixConstraint cell = (MatrixConstraint) constraint;
		return getChildAt(cell.getX(), cell.getY());
	}
	
	public PComponent getChildAt(int x, int y) {
		PComponent child = grid[gridID(x, y)];
		if (child.isElusive()) {
			if (child.getLayout() != null) {
				PComponent grandChild = child.getLayout().getChildAt(x, y);
				if (grandChild != null) {
					return grandChild;
				}
			}
			return null;
		}
		return child;
	}
	
	public PBounds getChildBoundsAt(int x, int y) {
		return getChildBounds(getChildAt(x, y));
	}
	
	private int gridID(int x, int y) {
		return x + y * gridW;
	}
	
	public boolean isValidCell(MatrixConstraint cell) {
		return isValidCell(cell.getX(), cell.getY());
	}
	
	public boolean isValidCell(int x, int y) {
		return x >= 0 && y >= 0 && x < getGridWidth() && y < getGridHeight();
	}
	
	public int getGridWidth() {
		return gridW;
	}
	
	public int getGridHeight() {
		return gridH;
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
		PInsets insets = getInsets();
		int ownerX = ob.getX() + insets.getFromLeft();
		int ownerY = ob.getY() + insets.getFromTop();
		int ownerW = ob.getWidth() - insets.getHorizontal();
		int ownerH = ob.getHeight() - insets.getVertical();
		
		int gridW = getGridWidth();
		int gridH = getGridHeight();
		
		int cellW = (ownerW - (gridW - 1) * gapH) / gridW;
		int cellH = (ownerH - (gridH - 1) * gapV) / gridH;
		
		for (int x = 0; x < gridW; x++) {
			for (int y = 0; y < gridH; y++) {
				PComponent cmp = getChildAt(x, y);
				if (cmp == null) {
					continue;
				}
				
				int cellX = ownerX + x * (cellW + gapH);
				int cellY = ownerY + y * (cellH + gapV);
				
				setChildBounds(cmp, cellX, cellY, cellW, cellH);
			}
		}
	}
	
	public PSize getPreferredSize() {
		int maxW = 0;
		int maxH = 0;
		int paddingH = getInsets().getHorizontal();
		int paddingV = getInsets().getVertical();
		for (PComponent child : getChildren()) {
			PSize prefSize = getPreferredSizeOf(child);
			if (prefSize.getWidth() > maxW) {
				maxW = prefSize.getWidth();
			}
			if (prefSize.getHeight() > maxH) {
				maxH = prefSize.getHeight();
			}
		}
		int prefW = (maxW + gapH) * getGridWidth() - gapH + paddingH;
		int prefH = (maxH + gapV) * getGridHeight() - gapV + paddingV;
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public static class MatrixConstraint {
		
		protected final int x, y;
		
		public MatrixConstraint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}
		
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj == null || !(obj instanceof MatrixConstraint)) {
				return false;
			}
			MatrixConstraint other = (MatrixConstraint) obj;
			return x == other.x && y == other.y;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("GridConstraint [x=");
			builder.append(x);
			builder.append(", y=");
			builder.append(y);
			builder.append("]");
			return builder.toString();
		}
		
	}
	
}