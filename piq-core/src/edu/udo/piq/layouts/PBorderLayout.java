package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractEnumPLayout;
import edu.udo.piq.tools.MutablePSize;

public class PBorderLayout extends 
	AbstractEnumPLayout<edu.udo.piq.layouts.PBorderLayout.Constraint> 
{
	
	/**
	 * To save memory the preferred size of the layout 
	 * is an instance of MutablePSize which is updated 
	 * and returned by the {@link #getPreferredSize()} 
	 * method.<br>
	 */
	protected final MutablePSize prefSize = new MutablePSize();
	
	public PBorderLayout(PComponent owner) {
		super(owner, Constraint.class);
	}
	
	protected String getErrorMsgConstraintIllegal() {
		return "constraint.getClass() != Constraint.class";
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