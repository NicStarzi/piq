package edu.udo.piq.tutorial;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.MutablePSize;

public class TrianglePLayout extends AbstractMapPLayout {
	
	protected final MutablePSize prefSize = new MutablePSize();
	private PComponent[] comps = new PComponent[Pos.values().length];
	
	public TrianglePLayout(PComponent component) {
		super(component);
		addObs(new PLayoutObs() {
			public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Pos pos = (Pos) constraint;
				comps[pos.ordinal()] = child;
			}
			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Pos pos = (Pos) constraint;
				comps[pos.ordinal()] = null;
			}
		});
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof Pos 
				&& getChildForConstraint(constraint) == null;
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
		int x = ob.getX();
		int fx = ob.getFinalX();
		int w = ob.getWidth();
		int y = ob.getY();
		int fy = ob.getFinalY();
		
		PComponent top = getChildForConstraint(Pos.TOP);
		if (top != null) {
			PSize sizeCmp = getPreferredSizeOf(top);
			int cmpW = sizeCmp.getWidth();
			int cmpH = sizeCmp.getHeight();
			int cmpX = x + w / 2 - cmpW / 2;
			int cmpY = y;
			setChildBounds(top, cmpX, cmpY, cmpW, cmpH);
		}
		PComponent lft = getChildForConstraint(Pos.BOTTOM_LEFT);
		if (lft != null) {
			PSize sizeCmp = getPreferredSizeOf(lft);
			int cmpW = sizeCmp.getWidth();
			int cmpH = sizeCmp.getHeight();
			int cmpX = x;
			int cmpY = fy - cmpH;
			setChildBounds(lft, cmpX, cmpY, cmpW, cmpH);
		}
		PComponent rgt = getChildForConstraint(Pos.BOTTOM_RIGHT);
		if (rgt != null) {
			PSize sizeCmp = getPreferredSizeOf(rgt);
			int cmpW = sizeCmp.getWidth();
			int cmpH = sizeCmp.getHeight();
			int cmpX = fx - cmpW;
			int cmpY = fy - cmpH;
			setChildBounds(rgt, cmpX, cmpY, cmpW, cmpH);
		}
	}
	
	public PSize getPreferredSize() {
		PComponent top = getChildForConstraint(Pos.TOP);
		PComponent lft = getChildForConstraint(Pos.BOTTOM_LEFT);
		PComponent rgt = getChildForConstraint(Pos.BOTTOM_RIGHT);
		
		PSize sizeTop = getPreferredSizeOf(top);
		PSize sizeLft = getPreferredSizeOf(lft);
		PSize sizeRgt = getPreferredSizeOf(rgt);
		
		int wTop = sizeTop.getWidth();
		int wBtm = sizeLft.getWidth() + sizeRgt.getWidth();
		
		int prefW = Math.max(wTop, wBtm);
		int prefH = sizeTop.getHeight() + 
				Math.max(sizeLft.getHeight(), sizeRgt.getHeight());
		
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public static enum Pos {
		TOP, BOTTOM_LEFT, BOTTOM_RIGHT;
	}
	
}