package edu.udo.piq.layouts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.util.ThrowException;

public class PFreeLayout extends AbstractMapPLayout {
	
	protected final List<PComponent> sortedChildren = new ArrayList<>();
	
	public PFreeLayout(PComponent owner) {
		super(owner);
	}
	
	@Override
	protected void onChildAdded(PComponentLayoutData data) {
		addChildSorted(data.getComponent(), (FreeConstraint) data.getConstraint());
	}
	
	@Override
	protected void onChildRemoved(PComponentLayoutData data) {
		super.onChildRemoved(data);
		sortedChildren.remove(data.getComponent());
	}
	
	@Override
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint != null && constraint instanceof FreeConstraint;
	}
	
	@Override
	protected void layOutInternal() {
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
			AlignmentX alignX = con.getAlignmentX();
			AlignmentY alignY = con.getAlignmentY();
			setChildCell(cmp, x, y, w, h, alignX, alignY);
		}
	}
	
	@Override
	protected void onInvalidated() {
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
	}
	
	@Override
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
	
	@Override
	public PComponent getChildAt(int x, int y) {
		if (!getOwner().getBounds().contains(x, y)) {
			return null;
		}
		if (isEmpty()) {
			return null;
		}
		for (PComponent child : sortedChildren) {
//		for (int i = 0; i < sortedChildren.size(); i++) {
//			PComponent child = sortedChildren.get(i);
			if (child.isIgnoredByPicking()) {
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
	
	@Override
	public Iterable<PComponent> getChildren() {
		return Collections.unmodifiableList(sortedChildren);
	}
	
	@Override
	public FreeConstraint getChildConstraint(PComponent child) {
		return (FreeConstraint) super.getChildConstraint(child);
	}
	
	public void updateConstraint(PComponent child, int x, int y) {
		FreeConstraint con = getChildConstraint(child);
		updateConstraint(child, con, x, y, con.w, con.h, con.z, con.alignX, con.alignY);
	}
	
	public void updateConstraint(PComponent child, int x, int y, int z) {
		FreeConstraint con = getChildConstraint(child);
		updateConstraint(child, con, x, y, con.w, con.h, z, con.alignX, con.alignY);
	}
	
	public void updateConstraint(PComponent child, int x, int y, int width, int height) {
		FreeConstraint con = getChildConstraint(child);
		updateConstraint(child, con, x, y, width, height, con.z, con.alignX, con.alignY);
	}
	
	public void updateConstraint(PComponent child, int x, int y, int width, int height, int z) {
		FreeConstraint con = getChildConstraint(child);
		updateConstraint(child, con, x, y, width, height, z, con.alignX, con.alignY);
	}
	
	protected void updateConstraint(PComponent child, FreeConstraint con, int x, int y, 
			int width, int height, int z, AlignmentX alignX, AlignmentY alignY) 
	{
		boolean invalidate = false;
		if (con.x != x || con.y != y || con.w != width || con.h != height || con.alignX != alignX || con.alignY != alignY) {
			con.x = x;
			con.y = y;
			con.w = width;
			con.h = height;
			con.alignX = alignX;
			con.alignY = alignY;
			invalidate = true;
		}
		if (con.z != z) {
			con.z = z;
			sortedChildren.remove(child);
			addChildSorted(child, con);
			invalidate = true;
		}
		if (invalidate) {
			invalidate();
		}
	}
	
	protected void setConstraintX(FreeConstraint c, int value) {
		c.x = value;
	}
	
	protected void setConstraintY(FreeConstraint c, int value) {
		c.y = value;
	}
	
	protected void setConstraintZ(FreeConstraint c, int value) {
		c.z = value;
	}
	
	protected void setConstraintW(FreeConstraint c, int value) {
		c.w = value;
	}
	
	protected void setConstraintH(FreeConstraint c, int value) {
		c.h = value;
	}
	
	protected void setConstraintAlignmentX(FreeConstraint c, AlignmentX value) {
		c.alignX = value;
	}
	
	protected void setConstraintAlignmentY(FreeConstraint c, AlignmentY value) {
		c.alignY = value;
	}
	
	public void updateConstraint(PComponent child, AlignmentX alignX, AlignmentY alignY) {
		ThrowException.ifNull(alignX, "alignX == null");
		ThrowException.ifNull(alignY, "alignY == null");
		FreeConstraint con = getChildConstraint(child);
		con.alignX = alignX;
		con.alignY = alignY;
		invalidate();
	}
	
	public void updateConstraint(PComponent child, FreeConstraint newConstraint) {
		FreeConstraint con = getChildConstraint(child);
		updateConstraint(child, con,
				newConstraint.getX(), newConstraint.getY(),
				newConstraint.getWidth(), newConstraint.getHeight(),
				newConstraint.getZ(), 
				newConstraint.getAlignmentX(), newConstraint.getAlignmentY());
	}
	
	@Override
	protected void onChildPrefSizeChanged(PComponent child) {
		FreeConstraint cnstr = getChildConstraint(child);
		if (cnstr.w == -1 || cnstr.h == -1) {
			invalidate();
		}
	}
	
	public static class FreeConstraint {
		
		protected int x, y, w, h, z;
		protected AlignmentX alignX;
		protected AlignmentY alignY;
		
		public FreeConstraint() {
			this(0, 0, -1, -1, 0);
		}
		
		public FreeConstraint(int x, int y) {
			this(x, y, -1, -1, 0);
		}
		
		public FreeConstraint(int x, int y, AlignmentX alignX, AlignmentY alignY) {
			this(x, y, -1, -1, -1, alignX, alignY);
		}
		
		public FreeConstraint(int z) {
			this(0, 0, -1, -1, z);
		}
		
		public FreeConstraint(int x, int y, int z) {
			this(x, y, -1, -1, z);
		}
		
		public FreeConstraint(int x, int y, int z, AlignmentX alignX, AlignmentY alignY) {
			this(x, y, -1, -1, z, alignX, alignY);
		}
		
		public FreeConstraint(int x, int y, int w, int h) {
			this(x, y, w, h, 0);
		}
		
		public FreeConstraint(int x, int y, int w, int h, AlignmentX alignX, AlignmentY alignY) {
			this(x, y, w, h, -1, alignX, alignY);
		}
		
		public FreeConstraint(int x, int y, int w, int h, int z) {
			this(x, y, w, h, z, AlignmentX.FILL, AlignmentY.FILL);
		}
		
		public FreeConstraint(int x, int y, int w, int h, int z, AlignmentX alignX, AlignmentY alignY) {
			ThrowException.ifLess(-1, w, "width < 0");
			ThrowException.ifLess(-1, h, "height < 0");
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.z = z;
			this.alignX = alignX;
			this.alignY = alignY;
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
		
		public AlignmentX getAlignmentX() {
			return alignX;
		}
		
		public AlignmentY getAlignmentY() {
			return alignY;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getClass().getSimpleName());
			sb.append("{x=");
			sb.append(getX());
			sb.append("; y=");
			sb.append(getY());
			sb.append("; z=");
			sb.append(getZ());
			sb.append("; width=");
			sb.append(getWidth());
			sb.append("; height=");
			sb.append(getHeight());
			sb.append("; alignX=");
			sb.append(getAlignmentX());
			sb.append("; alignY=");
			sb.append(getAlignmentY());
			sb.append("}");
			return sb.toString();
		}
	}
	
}