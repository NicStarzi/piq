package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PCollapsibleLayout extends AbstractEnumPLayout<PCollapsibleLayout.Constraint> {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(4);
	public static final AlignmentX DEFAULT_HEADER_ALIGNMENT_X = AlignmentX.PREFERRED_OR_CENTER;
	public static final AlignmentY DEFAULT_HEADER_ALIGNMENT_Y = AlignmentY.PREFERRED_OR_CENTER;
	public static final int DEFAULT_BUTTON_HEADER_GAP = 2;
	public static final int DEFAULT_HEADER_BODY_GAP = 2;
	
	protected AlignmentX headerAlignX = DEFAULT_HEADER_ALIGNMENT_X;
	protected AlignmentY headerAlignY = DEFAULT_HEADER_ALIGNMENT_Y;
	protected PInsets insets = DEFAULT_INSETS;
	protected int btnLblGap = DEFAULT_BUTTON_HEADER_GAP;
	protected int hdrBdyGap = DEFAULT_HEADER_BODY_GAP;
	
	protected boolean expanded;
	
	public PCollapsibleLayout(PComponent component) {
		super(component, Constraint.class);
	}
	
	public void setExpanded(boolean value) {
		if (isExpanded() != value) {
			expanded = value;
			invalidate();
		}
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
	public void setHeaderAlignX(AlignmentX value) {
		ThrowException.ifNull(value, "insets == null");
		if (getHeaderAlignX() != value) {
			headerAlignX = value;
			invalidate();
		}
	}
	
	public AlignmentX getHeaderAlignX() {
		return headerAlignX;
	}
	
	public void setHeaderAlignY(AlignmentY value) {
		ThrowException.ifNull(value, "insets == null");
		if (getHeaderAlignY() != value) {
			headerAlignY = value;
			invalidate();
		}
	}
	
	public AlignmentY getHeaderAlignY() {
		return headerAlignY;
	}
	
	public void setInsets(PInsets value) {
		ThrowException.ifNull(value, "insets == null");
		if (!getInsets().equals(value)) {
			insets = value;
			invalidate();
		}
	}
	
	public PInsets getInsets() {
		return getStyleAttribute(PReadOnlyLayout.ATTRIBUTE_KEY_INSETS, insets);
	}
	
	public void setHeaderBodyGap(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (getHeaderBodyGap() != value) {
			hdrBdyGap = value;
			invalidate();
		}
	}
	
	public int getHeaderBodyGap() {
		return hdrBdyGap;
	}
	
	public void setButtonLabelGap(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (getButtonLabelGap() != value) {
			btnLblGap = value;
			invalidate();
		}
	}
	
	public int getButtonLabelGap() {
		return btnLblGap;
	}
	
	@Override
	protected void onInvalidated() {
		PComponent btn = getChildForConstraint(Constraint.EXPAND_BUTTON);
		PComponent lbl = getChildForConstraint(Constraint.LABEL);
		PComponent bdy = getChildForConstraint(Constraint.BODY);
		
		PSize prefSizeBtn = getPreferredSizeOf(btn);
		PSize prefSizeLbl = getPreferredSizeOf(lbl);
		PSize prefSizeBdy;
		
		int prefHeaderH = Math.max(prefSizeBtn.getHeight(), prefSizeLbl.getHeight());
		int prefH;
		if (isExpanded()) {
			prefSizeBdy = getPreferredSizeOf(bdy);
			prefH = prefHeaderH + getHeaderBodyGap() + prefSizeBdy.getHeight();
		} else {
			prefSizeBdy = PSize.ZERO_SIZE;
			prefH = prefHeaderH;
		}
		int prefW = Math.max(prefSizeBdy.getWidth(),
				prefSizeBtn.getWidth() + prefSizeLbl.getWidth());
		
		PInsets insets = getInsets();
		prefW += insets.getHorizontal();
		prefH += insets.getVertical();
		
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
	}
	
	@Override
	protected void layOutInternal() {
		PInsets insets = getInsets();
		PBounds ob = getOwner().getBounds();
		int x = ob.getX() + insets.getFromLeft();
		int y = ob.getY() + insets.getFromTop();
		int w = ob.getWidth() - insets.getHorizontal();
		int h = ob.getHeight() - insets.getVertical();
		
		PComponent btn = getChildForConstraint(Constraint.EXPAND_BUTTON);
		PComponent lbl = getChildForConstraint(Constraint.LABEL);
		PComponent bdy = getChildForConstraint(Constraint.BODY);
		
		PSize prefSizeBtn = getPreferredSizeOf(btn);
		int btnPrefW = prefSizeBtn.getWidth();
		int btnPrefH = prefSizeBtn.getHeight();
		PSize prefSizeLbl = getPreferredSizeOf(lbl);
		int lblPrefW = prefSizeLbl.getWidth();
		int lblPrefH = prefSizeLbl.getHeight();
		int headerPrefH = Math.max(btnPrefH, lblPrefH);
		
		AlignmentX headerAlignX = getHeaderAlignX();
		AlignmentY headerAlignY = getHeaderAlignY();
		
		int btnX, btnW = btnPrefW;
		int lblX, lblW = lblPrefW;
		int gapX = getButtonLabelGap();
		switch (headerAlignX) {
		case PREFERRED_OR_CENTER:
		case CENTER:
			btnX = x + ((w - gapX) - (btnPrefW + lblPrefW)) / 2;
			lblX = btnX + btnW + gapX;
			break;
		case FILL:
			int halfW = (w - gapX) / 2;
			btnX = x;
			btnW = halfW;
			lblX = btnX + btnW + gapX;
			lblW = halfW;
			break;
		case LEFT:
			btnX = x;
			lblX = btnX + btnW + gapX;
			break;
		case RIGHT:
			lblX = x + w - lblW;
			btnX = lblX - gapX - btnW;
			break;
		default:
			btnX = lblX = x;
			ThrowException.always("Unsupported Alignment: "+headerAlignX);
		}
		int btnY = headerAlignY.getTopY(y, headerPrefH, btnPrefH);
		int btnH = headerAlignY.getHeight(y, headerPrefH, btnPrefH);
		int lblY = headerAlignY.getTopY(y, headerPrefH, lblPrefH);
		int lblH = headerAlignY.getHeight(y, headerPrefH, lblPrefH);
		setChildCellFilled(btn, btnX, btnY, btnW, btnH);
		setChildCellFilled(lbl, lblX, lblY, lblW, lblH);
		
		if (isExpanded()) {
			int headerFy = y + headerPrefH;
			
			int bdyX = x;
			int bdyY = headerFy + getHeaderBodyGap();
			int bdyW = w;
			int bdyH = h - (bdyY - y);
			setChildCellFilled(bdy, bdyX, bdyY, bdyW, bdyH);
		} else {
			setChildCellFilled(bdy, x, y, 0, 0);
		}
	}
	
	public static enum Constraint {
		EXPAND_BUTTON,
		LABEL,
		BODY,
		;
	}
	
}