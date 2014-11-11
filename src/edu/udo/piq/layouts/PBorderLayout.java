package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.ImmutablePSize;

public class PBorderLayout extends AbstractPLayout {
	
	protected final PComponent[] positions;
//	protected int prefW;
//	protected int prefH;
	
	public PBorderLayout(PComponent owner) {
		super(owner);
		positions = new PComponent[Constraint.values().length];
		
		addObs(new PLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				Constraint pos = (Constraint) constraint;
				positions[pos.ordinal()] = child;
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				Constraint pos = (Constraint) constraint;
				positions[pos.ordinal()] = null;
			}
			public void childLaidOut(PLayout layout, PComponent child, Object constraint) {
			}
			public void layoutInvalidated(PLayout layout) {
			}
		});
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint != null && constraint instanceof Constraint && getAt(constraint) == null;
	}
	
	public PComponent getAt(Object constraint) {
		if (constraint == null || !(constraint instanceof Constraint)) {
			throw new IllegalArgumentException();
		}
		Constraint pos = (Constraint) constraint;
		return positions[pos.ordinal()];
	}
	
	public void layOut() {
		PBounds ob = getOwnerBounds();
		int lft = ob.getX();
		int rgt = ob.getFinalX();
		int top = ob.getY();
		int btm = ob.getFinalY();
//		prefW = 0;
//		prefH = 0;
		
		PComponent nCmp = getAt(Constraint.TOP);
		PComponent eCmp = getAt(Constraint.RIGHT);
		PComponent wCmp = getAt(Constraint.LEFT);
		PComponent sCmp = getAt(Constraint.BOTTOM);
		PComponent cCmp = getAt(Constraint.CENTER);
		
		if (nCmp != null/* && nCmp.isVisible()*/) {
			int cmpPrefH = getPreferredSizeOf(nCmp).getHeight();
			setChildBounds(nCmp, lft, top, (rgt - lft), cmpPrefH);
			top += cmpPrefH;
//			prefH += cmpPrefH;
		}
		if (sCmp != null) {
			int cmpPrefH = getPreferredSizeOf(sCmp).getHeight();
			setChildBounds(sCmp, lft, (btm - cmpPrefH), (rgt - lft), cmpPrefH);
			btm -= cmpPrefH;
//			prefH += cmpPrefH;
		}
		if (eCmp != null) {
			int cmpPrefW = getPreferredSizeOf(eCmp).getWidth();
			setChildBounds(eCmp, (rgt - cmpPrefW), top, cmpPrefW, (btm - top));
			rgt -= cmpPrefW;
//			prefW += cmpPrefW;
		}
		if (wCmp != null) {
			int cmpPrefW = getPreferredSizeOf(wCmp).getWidth();
			setChildBounds(wCmp, lft, top, cmpPrefW, (btm - top));
			lft += cmpPrefW;
//			prefW += cmpPrefW;
		}
		if (cCmp != null) {
//			prefH += getPreferredHeightOf(wCmp);
//			prefW += getPreferredWidthOf(wCmp);
			
			setChildBounds(cCmp, lft, top, (rgt - lft), (btm - top));
		}
	}
	
	public PSize getPreferredSize() {
		PSize prefLft = getPreferredSizeOf(getAt(Constraint.LEFT));
		PSize prefRgt = getPreferredSizeOf(getAt(Constraint.RIGHT));
		PSize prefTop = getPreferredSizeOf(getAt(Constraint.TOP));
		PSize prefBtm = getPreferredSizeOf(getAt(Constraint.BOTTOM));
		PSize prefCnt = getPreferredSizeOf(getAt(Constraint.CENTER));
		int prefW = prefLft.getWidth() + prefRgt.getWidth() + prefCnt.getWidth();
		int prefH = prefTop.getHeight() + prefBtm.getHeight() + prefCnt.getHeight();
		return new ImmutablePSize(prefW, prefH);
	}
	
//	public int getPreferredWidth() {
//		int prefW = 0;
//		prefW += getPreferredWidthOf(getAt(Constraint.RIGHT));
//		prefW += getPreferredWidthOf(getAt(Constraint.CENTER));
//		prefW += getPreferredWidthOf(getAt(Constraint.LEFT));
//		return prefW;
//	}
//	
//	public int getPreferredHeight() {
//		int prefH = 0;
//		prefH += getPreferredHeightOf(getAt(Constraint.TOP));
//		prefH += getPreferredHeightOf(getAt(Constraint.CENTER));
//		prefH += getPreferredHeightOf(getAt(Constraint.BOTTOM));
//		return prefH;
//	}
	
	public static enum Constraint {
		TOP,
		RIGHT,
		LEFT,
		BOTTOM,
		CENTER,
		;
	}
	
}