package edu.udo.piq.layouts;

import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;

public class PGridLayout extends AbstractPLayout {
	
	protected final PComponent[] componentGrid;
	protected final int w, h;
	
	protected PGridLayout(PComponent component, int width, int height) {
		super(component);
		w = width;
		h = height;
		componentGrid = new PComponent[w * h];
	}
	
	public int getWidth() {
		return w;
	}
	
	public int getHeight() {
		return h;
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof GridConstraint 
				&& isFree((GridConstraint) constraint);
	}
	
	protected boolean isFree(GridConstraint constr) {
		for (int i = 0; i < constr.w; i++) {
			for (int j = 0; j < constr.h; j++) {
				int cellX = constr.x + i;
				int cellY = constr.y + j;
				if (getComponentAt(cellX, cellY) != null) {
					return false;
				}
			}
		}
		return true;
	}
	
	protected PComponent getComponentAt(int x, int y) {
		return componentGrid[x + y * w];
	}
	
	public void layOut() {
	}
	
	public PSize getPreferredSize() {
		return PSize.NULL_SIZE;
	}
	
	public static class GridConstraint {
		
		protected final int x, y, w, h;
		protected final HorizontalAlignment alignH;
		protected final VerticalAlignment alignV;
		
		public GridConstraint(int x, int y, 
				HorizontalAlignment horizontalAlignment, 
				VerticalAlignment verticalAlignment) {
			this(x, y, 1, 1, horizontalAlignment, verticalAlignment);
		}
		
		public GridConstraint(int x, int y, int width, int height, 
				HorizontalAlignment horizontalAlignment, 
				VerticalAlignment verticalAlignment) {
			if (x < 0 || y < 0 || width <= 0 || height <= 0) {
				throw new IllegalArgumentException("x="+x+", y="+y+", width="+width+", height="+height);
			}
			this.x = x;
			this.y = y;
			this.w = width;
			this.h = height;
			alignH = horizontalAlignment;
			alignV = verticalAlignment;
		}
		
	}
	
	public static enum HorizontalAlignment {
		LEFT,
		RIGHT,
		CENTER,
		FILL,
		;
	}
	
	public static enum VerticalAlignment {
		TOP,
		BOTTOM,
		CENTER,
		FILL,
		;
	}
	
	public static enum Growth {
		PREFERRED, 
		MAXIMIZE,
		;
	}
	
}