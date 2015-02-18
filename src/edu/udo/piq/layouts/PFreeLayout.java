package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;
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
	protected final MutablePSize prefSize = new MutablePSize();
	protected final List<PComponent> sortedChildren = new ArrayList<>();
	
	public PFreeLayout(PComponent owner) {
		super(owner);
		addObs(new PLayoutObs() {
			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				sortedChildren.remove(child);
			}
			public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				addChildSorted(child, (FreeConstraint) constraint);
			}
		});
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint != null && constraint instanceof FreeConstraint;
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
		int parentX = ob.getX();
		int parentY = ob.getY();
		
		for (PComponent cmp : getChildren()) {
			FreeConstraint con = getChildConstraint(cmp);
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
			
			setChildBounds(cmp, x, y, w, h);
		}
	}
	
	public PSize getPreferredSize() {
		int maxFx = 0;
		int maxFy = 0;
		for (PComponent cmp : getChildren()) {
			FreeConstraint constraint = getChildConstraint(cmp);
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
	
	public void clearChildren() {
		sortedChildren.clear();
		super.clearChildren();
	}
	
	private void addChildSorted(PComponent component, FreeConstraint constraint) {
		int index = sortedChildren.size();
		while (index > 0) {
			PComponent child = sortedChildren.get(index - 1);
			FreeConstraint childCon = getChildConstraint(child);
			if (childCon.z > constraint.z) {
				index--;
			} else {
				break;
			}
		}
		sortedChildren.add(index, component);
	}
	
	public PComponent getChildAt(int x, int y) {
		for (int i = sortedChildren.size() - 1; i >= 0; i--) {
			PComponent child = sortedChildren.get(i);
			if (child.isElusive()) {
				if (child.getLayout() != null) {
					PComponent grandChild = child.getLayout().getChildAt(x, y);
					if (grandChild != null) {
						return grandChild;
					}
				}
			} else if (getChildBounds(child).contains(x, y)) {
				return child;
			}
		}
		return null;
	}
	
	public Collection<PComponent> getChildren() {
		return Collections.unmodifiableList(sortedChildren);
	}
	
	public FreeConstraint getChildConstraint(PComponent child) {
		return (FreeConstraint) super.getChildConstraint(child);
	}
	
	public void updateConstraint(PComponent child, FreeConstraint newConstraint) {
		FreeConstraint con = getChildConstraint(child);
		con.x = newConstraint.getX();
		con.y = newConstraint.getY();
		con.w = newConstraint.getWidth();
		con.h = newConstraint.getHeight();
		con.z = newConstraint.getZ();
		sortedChildren.remove(child);
		addChildSorted(child, con);
//		System.out.println("PFreeLayout.updateConstraint("+child+") => "+sortedChildren);
		fireInvalidateEvent();
	}
	
	public static class FreeConstraint {
		
		protected int x, y, w, h, z;
		
		public FreeConstraint() {
			this(0, 0, -1, -1, 0);
		}
		
		public FreeConstraint(int x, int y) {
			this(x, y, -1, -1, 0);
		}
		
		public FreeConstraint(int x, int y, int w, int h) {
			this(x, y, w, h, 0);
		}
		
		public FreeConstraint(int x, int y, int w, int h, int z) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.z = z;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public int getZ() {
			return z;
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