package edu.udo.piq.layouts;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PBorderLayout.BorderLayoutConstraint;
import edu.udo.piq.util.ThrowException;

public class PBorderLayout extends AbstractEnumPLayout<BorderLayoutConstraint> {
	
	public static final PInsets DEFAULT_INSETS = PInsets.ZERO_INSETS;
	public static final AlignmentX DEFAULT_ALIGN_X = AlignmentX.FILL;
	public static final AlignmentY DEFAULT_ALIGN_Y = AlignmentY.FILL;
	public static final int DEFAULT_GAP = 0;
	
	protected PInsets insets = PBorderLayout.DEFAULT_INSETS;
	protected AlignmentX alignX = DEFAULT_ALIGN_X;
	protected AlignmentY alignY = DEFAULT_ALIGN_Y;
	protected int gap = PBorderLayout.DEFAULT_GAP;
	
	public PBorderLayout(PComponent owner) {
		super(owner, BorderLayoutConstraint.class);
	}
	
	public void setInsets(PInsets value) {
		ThrowException.ifNull(value, "value == null");
		if (!getInsets().equals(value)) {
			insets = value;
			invalidate();
		}
	}
	
	public PInsets getInsets() {
		return getStyleAttribute(PReadOnlyLayout.ATTRIBUTE_KEY_INSETS, insets);
	}
	
	public void setAlignment(AlignmentX alignmentX, AlignmentY alignmentY) {
		setAlignmentX(alignmentX);
		setAlignmentY(alignmentY);
	}
	
	public void setAlignmentX(AlignmentX value) {
		ThrowException.ifNull(value, "value == null");
		if (alignX != value) {
			alignX = value;
			invalidate();
		}
	}
	
	public AlignmentX getAlignmentX() {
		return getStyleAttribute(PReadOnlyLayout.ATTRIBUTE_KEY_ALIGNMENT_X, alignX);
	}
	
	public void setAlignmentY(AlignmentY value) {
		ThrowException.ifNull(value, "value == null");
		if (alignY != value) {
			alignY = value;
			invalidate();
		}
	}
	
	public AlignmentY getAlignmentY() {
		return getStyleAttribute(PReadOnlyLayout.ATTRIBUTE_KEY_ALIGNMENT_Y, alignY);
	}
	
	public void setGap(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (getGap() != value) {
			gap = value;
			invalidate();
		}
	}
	
	public int getGap() {
		return getStyleAttribute(PReadOnlyLayout.ATTRIBUTE_KEY_GAP, gap);
	}
	
	@Override
	protected String getErrorMsgConstraintIllegal() {
		return "constraint.getClass() != Constraint.class";
	}
	
	@Override
	protected void onInvalidated() {
		PSize prefLft = getPreferredSizeOf(getChildForConstraint(BorderLayoutConstraint.LEFT));
		PSize prefRgt = getPreferredSizeOf(getChildForConstraint(BorderLayoutConstraint.RIGHT));
		PSize prefTop = getPreferredSizeOf(getChildForConstraint(BorderLayoutConstraint.TOP));
		PSize prefBtm = getPreferredSizeOf(getChildForConstraint(BorderLayoutConstraint.BOTTOM));
		PSize prefCnt = getPreferredSizeOf(getChildForConstraint(BorderLayoutConstraint.CENTER));
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
		
		PComponent cmpTop = getChildForConstraint(BorderLayoutConstraint.TOP);
		PComponent cmpRgt = getChildForConstraint(BorderLayoutConstraint.RIGHT);
		PComponent cmpLft = getChildForConstraint(BorderLayoutConstraint.LEFT);
		PComponent cmpBtm = getChildForConstraint(BorderLayoutConstraint.BOTTOM);
		PComponent cmpCtr = getChildForConstraint(BorderLayoutConstraint.CENTER);
		
		AlignmentX alignX = getAlignmentX();
		AlignmentY alignY = getAlignmentY();
		
		if (cmpTop != null) {
			int cmpPrefH = getPreferredSizeOf(cmpTop).getHeight();
			setChildCell(cmpTop, lft, top, (rgt - lft), cmpPrefH, alignX, alignY);
			top += cmpPrefH + gap;
		}
		if (cmpBtm != null) {
			int cmpPrefH = getPreferredSizeOf(cmpBtm).getHeight();
			setChildCell(cmpBtm, lft, (btm - cmpPrefH), (rgt - lft), cmpPrefH, alignX, alignY);
			btm -= (cmpPrefH + gap);
		}
		if (cmpRgt != null) {
			int cmpPrefW = getPreferredSizeOf(cmpRgt).getWidth();
			setChildCell(cmpRgt, (rgt - cmpPrefW), top, cmpPrefW, (btm - top), alignX, alignY);
			rgt -= (cmpPrefW + gap);
		}
		if (cmpLft != null) {
			int cmpPrefW = getPreferredSizeOf(cmpLft).getWidth();
			setChildCell(cmpLft, lft, top, cmpPrefW, (btm - top), alignX, alignY);
			lft += cmpPrefW + gap;
		}
		if (cmpCtr != null) {
			setChildCell(cmpCtr, lft, top, (rgt - lft), (btm - top), alignX, alignY);
		}
	}
	
	@Override
	protected void onChildPrefSizeChanged(PComponent child) {
		ThrowException.ifFalse(containsChild(child), "containsChild(child) == false");
		if (child != getChildForConstraint(BorderLayoutConstraint.CENTER)) {
			invalidate();
		}
	}
	
	public static enum BorderLayoutConstraint {
		TOP,
		RIGHT,
		LEFT,
		BOTTOM,
		CENTER,
		;
		public static final List<BorderLayoutConstraint> ALL =
				Collections.unmodifiableList(Arrays.asList(BorderLayoutConstraint.values()));
		public static final int COUNT = BorderLayoutConstraint.ALL.size();
	}
	
}