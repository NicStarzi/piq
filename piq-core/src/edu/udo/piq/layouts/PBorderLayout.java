package edu.udo.piq.layouts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PBorderLayout.Constraint;
import edu.udo.piq.tools.AbstractEnumPLayout;
import edu.udo.piq.util.ThrowException;

public class PBorderLayout extends AbstractEnumPLayout<Constraint> {
	
	public static final PInsets DEFAULT_INSETS = PInsets.ZERO_INSETS;
	public static final int DEFAULT_GAP = 0;
	
	private PInsets insets = DEFAULT_INSETS;
	private int gap = DEFAULT_GAP;
	
	public PBorderLayout(PComponent owner) {
		super(owner, Constraint.class);
	}
	
	public void setInsets(PInsets value) {
		ThrowException.ifNull(value, "value == null");
		if (!getInsets().equals(value)) {
			insets = value;
			invalidate();
		}
	}
	
	public PInsets getInsets() {
		return getStyleAttribute(ATTRIBUTE_KEY_INSETS, insets);
	}
	
	public void setGap(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (getGap() != value) {
			gap = value;
			invalidate();
		}
	}
	
	public int getGap() {
		return getStyleAttribute(ATTRIBUTE_KEY_GAP, gap);
	}
	
	@Override
	protected String getErrorMsgConstraintIllegal() {
		return "constraint.getClass() != Constraint.class";
	}
	
	@Override
	protected void onInvalidated() {
		PSize prefLft = getPreferredSizeOf(getChildForConstraint(Constraint.LEFT));
		PSize prefRgt = getPreferredSizeOf(getChildForConstraint(Constraint.RIGHT));
		PSize prefTop = getPreferredSizeOf(getChildForConstraint(Constraint.TOP));
		PSize prefBtm = getPreferredSizeOf(getChildForConstraint(Constraint.BOTTOM));
		PSize prefCnt = getPreferredSizeOf(getChildForConstraint(Constraint.CENTER));
		PInsets insets = getInsets();
		int gap = getGap();
		int prefW = prefLft.getWidth() + prefRgt.getWidth() + prefCnt.getWidth() + insets.getHorizontal();
		if (prefLft.getWidth() > 0) {
			prefW += gap;
		}
		if (prefRgt.getWidth() > 0) {
			prefW += gap;
		}
		int prefH = prefTop.getHeight() + prefBtm.getHeight() + prefCnt.getHeight() + insets.getVertical();
		if (prefTop.getWidth() > 0) {
			prefH += gap;
		}
		if (prefBtm.getWidth() > 0) {
			prefH += gap;
		}
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
	}
	
	@Override
	protected void layOutInternal() {
		PBounds ob = getOwner().getBounds();
		PInsets insets = getInsets();
		int lft = ob.getX() + insets.getFromLeft();
		int rgt = ob.getFinalX() - insets.getFromRight();
		int top = ob.getY() + insets.getFromTop();
		int btm = ob.getFinalY() - insets.getFromBottom();
		int gap = getGap();
		
		PComponent nCmp = getChildForConstraint(Constraint.TOP);
		PComponent eCmp = getChildForConstraint(Constraint.RIGHT);
		PComponent wCmp = getChildForConstraint(Constraint.LEFT);
		PComponent sCmp = getChildForConstraint(Constraint.BOTTOM);
		PComponent cCmp = getChildForConstraint(Constraint.CENTER);
		
		if (nCmp != null/* && nCmp.isVisible()*/) {
			int cmpPrefH = getPreferredSizeOf(nCmp).getHeight();
			setChildBounds(nCmp, lft, top, (rgt - lft), cmpPrefH);
			top += cmpPrefH + gap;
		}
		if (sCmp != null) {
			int cmpPrefH = getPreferredSizeOf(sCmp).getHeight();
			setChildBounds(sCmp, lft, (btm - cmpPrefH), (rgt - lft), cmpPrefH);
			btm -= (cmpPrefH + gap);
		}
		if (eCmp != null) {
			int cmpPrefW = getPreferredSizeOf(eCmp).getWidth();
			setChildBounds(eCmp, (rgt - cmpPrefW), top, cmpPrefW, (btm - top));
			rgt -= (cmpPrefW + gap);
		}
		if (wCmp != null) {
			int cmpPrefW = getPreferredSizeOf(wCmp).getWidth();
			setChildBounds(wCmp, lft, top, cmpPrefW, (btm - top));
			lft += cmpPrefW + gap;
		}
		if (cCmp != null) {
			setChildBounds(cCmp, lft, top, (rgt - lft), (btm - top));
		}
	}
	
	@Override
	protected void onChildPrefSizeChanged(PComponent child) {
		ThrowException.ifFalse(containsChild(child), "containsChild(child) == false");
		if (child != getChildForConstraint(Constraint.CENTER)) {
			invalidate();
		}
	}
	
	public static enum Constraint {
		TOP,
		RIGHT,
		LEFT,
		BOTTOM,
		CENTER,
		;
		public static final List<Constraint> ALL =
				Collections.unmodifiableList(Arrays.asList(values()));
		public static final int COUNT = ALL.size();
	}
	
}