package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PFreeLayout extends AbstractPLayout {
	
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize;
	
	public PFreeLayout(PComponent owner) {
		super(owner);
		prefSize = new MutablePSize();
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint != null && constraint instanceof FreeConstraint;
	}
	
	public void layOut() {
		PBounds ob = getOwnerBounds();
		int parentX = ob.getX();
		int parentY = ob.getY();
		
		for (PComponent cmp : getChildren()) {
			FreeConstraint con = (FreeConstraint) getChildConstraint(cmp);
			int x = parentX + con.x;
			int y = parentY + con.y;
			int w = con.w;
			int h = con.h;
			if (w < 0 || h < 0) {
				PSize prefSize = getPreferredSizeOf(cmp);
				if (w < 0) {
					w = prefSize.getWidth();
				}
				if (h < 0) {
					h = prefSize.getHeight();
				}
			}
//			if (w < 0) {
//				w = getPreferredWidthOf(cmp);
//			}
//			if (h < 0) {
//				h = getPreferredHeightOf(cmp);
//			}
			
			setChildBounds(cmp, x, y, w, h);
		}
	}
	
	public PSize getPreferredSize() {
		int maxFx = 0;
		int maxFy = 0;
		for (PComponent cmp : getChildren()) {
			FreeConstraint constraint = (FreeConstraint) getChildConstraint(cmp);
			int fx;
			if (constraint.getWidth() < 0) {
				fx = constraint.getX() + getPreferredSizeOf(cmp).getWidth();
			} else {
				fx = constraint.getFinalX();
			}
			if (maxFx < fx) {
				maxFx = fx;
			}
			
			int fy;
			if (constraint.getHeight() < 0) {
				fy = constraint.getY() + getPreferredSizeOf(cmp).getHeight();
			} else {
				fy = constraint.getFinalY();
			}
			if (maxFy < fy) {
				maxFy = fy;
			}
		}
		prefSize.setWidth(maxFx);
		prefSize.setHeight(maxFy);
		return prefSize;
	}
	
	public static class FreeConstraint {
		
		protected int x, y, w, h;
		
		public FreeConstraint() {
			this(0, 0, -1, -1);
		}
		
		public FreeConstraint(int x, int y) {
			this(x, y, -1, -1);
		}
		
		public FreeConstraint(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getWidth() {
			return w;
		}
		
		public int getHeight() {
			return h;
		}
		
		public int getFinalX() {
			return x + w;
		}
		
		public int getFinalY() {
			return y + h;
		}
	}
	
}