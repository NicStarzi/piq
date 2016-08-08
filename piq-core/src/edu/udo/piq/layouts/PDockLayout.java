package edu.udo.piq.layouts;

import edu.udo.piq.PComponent;
import edu.udo.piq.tools.AbstractMapPLayout;

public class PDockLayout extends AbstractMapPLayout {
	
	public PDockLayout(PComponent owner) {
		super(owner);
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint != null && constraint instanceof Constraint;
	}
	
	public void layOutInternal() {
//		PBounds ob = getOwner().getBounds();
//		int lft = ob.getX();
//		int rgt = ob.getFinalX();
//		int top = ob.getY();
//		int btm = ob.getFinalY();
//		
	}
	
	public void onInvalidated() {
//		int prefW = prefLft.getWidth() + prefRgt.getWidth() + prefCnt.getWidth();
//		int prefH = prefTop.getHeight() + prefBtm.getHeight() + prefCnt.getHeight();
//		prefSize.setWidth(prefW);
//		prefSize.setHeight(prefH);
	}
	
	public static enum Constraint {
		TOP,
		RIGHT,
		LEFT,
		BOTTOM,
		;
	}
	
}