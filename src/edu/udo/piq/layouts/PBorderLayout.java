package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PBorderLayout extends AbstractPLayout {
	
	/**
	 * Used to store all components at their position.<br>
	 * The index of a component is equal to the ordinal 
	 * number of the positions constraint.<br>
	 */
	protected final PComponent[] positions;
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize;
	
	public PBorderLayout(PComponent owner) {
		super(owner);
		positions = new PComponent[Constraint.values().length];
		prefSize = new MutablePSize();
		
		addObs(new PLayoutObs() {
			public void childAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Constraint pos = (Constraint) constraint;
				positions[pos.ordinal()] = child;
			}
			public void childRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				Constraint pos = (Constraint) constraint;
				positions[pos.ordinal()] = null;
			}
		});
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint != null && constraint instanceof Constraint && getChildForConstraint(constraint) == null;
	}
	
	public PComponent getChildForConstraint(Object constraint) {
		if (constraint == null || !(constraint instanceof Constraint)) {
			throw new IllegalArgumentException();
		}
		Constraint pos = (Constraint) constraint;
		return positions[pos.ordinal()];
	}
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
		int lft = ob.getX();
		int rgt = ob.getFinalX();
		int top = ob.getY();
		int btm = ob.getFinalY();
		
		PComponent nCmp = getChildForConstraint(Constraint.TOP);
		PComponent eCmp = getChildForConstraint(Constraint.RIGHT);
		PComponent wCmp = getChildForConstraint(Constraint.LEFT);
		PComponent sCmp = getChildForConstraint(Constraint.BOTTOM);
		PComponent cCmp = getChildForConstraint(Constraint.CENTER);
		
		if (nCmp != null/* && nCmp.isVisible()*/) {
			int cmpPrefH = getPreferredSizeOf(nCmp).getHeight();
			setChildBounds(nCmp, lft, top, (rgt - lft), cmpPrefH);
			top += cmpPrefH;
		}
		if (sCmp != null) {
			int cmpPrefH = getPreferredSizeOf(sCmp).getHeight();
			setChildBounds(sCmp, lft, (btm - cmpPrefH), (rgt - lft), cmpPrefH);
			btm -= cmpPrefH;
		}
		if (eCmp != null) {
			int cmpPrefW = getPreferredSizeOf(eCmp).getWidth();
			setChildBounds(eCmp, (rgt - cmpPrefW), top, cmpPrefW, (btm - top));
			rgt -= cmpPrefW;
		}
		if (wCmp != null) {
			int cmpPrefW = getPreferredSizeOf(wCmp).getWidth();
			setChildBounds(wCmp, lft, top, cmpPrefW, (btm - top));
			lft += cmpPrefW;
		}
		if (cCmp != null) {
			setChildBounds(cCmp, lft, top, (rgt - lft), (btm - top));
		}
	}
	
	public PSize getPreferredSize() {
		PSize prefLft = getPreferredSizeOf(getChildForConstraint(Constraint.LEFT));
		PSize prefRgt = getPreferredSizeOf(getChildForConstraint(Constraint.RIGHT));
		PSize prefTop = getPreferredSizeOf(getChildForConstraint(Constraint.TOP));
		PSize prefBtm = getPreferredSizeOf(getChildForConstraint(Constraint.BOTTOM));
		PSize prefCnt = getPreferredSizeOf(getChildForConstraint(Constraint.CENTER));
		int prefW = prefLft.getWidth() + prefRgt.getWidth() + prefCnt.getWidth();
		int prefH = prefTop.getHeight() + prefBtm.getHeight() + prefCnt.getHeight();
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
	}
	
	public static enum Constraint {
		TOP,
		RIGHT,
		LEFT,
		BOTTOM,
		CENTER,
		;
	}
	
}