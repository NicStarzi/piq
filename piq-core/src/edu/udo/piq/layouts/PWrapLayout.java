package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;
import edu.udo.piq.util.ThrowException;

public class PWrapLayout extends PListLayout {
	
	public static final int DEFAULT_SECONDARY_GAP = 2;
	public static final PrefSizeMode DEFAULT_PREF_SIZE_MODE = PrefSizeMode.PREFER_WRAPPING;
	public static final String ATTRIBUTE_KEY_SECONDARY_GAP = "secondary_gap";
	public static final String ATTRIBUTE_KEY_PREF_SIZE_MODE = "pref_size_mode";
	
	protected int secondaryGap = DEFAULT_SECONDARY_GAP;
	protected PrefSizeMode prefSizeMode = DEFAULT_PREF_SIZE_MODE;
	
	public PWrapLayout(PComponent owner) {
		super(owner);
	}
	
	public PWrapLayout(PComponent owner, ListAlignment alignment) {
		super(owner, alignment);
	}
	
	public PWrapLayout(PComponent owner, int gap) {
		super(owner, gap);
	}
	
	public PWrapLayout(PComponent owner, ListAlignment alignment, int gap) {
		super(owner, alignment, gap);
	}
	
	public void setSecondaryGap(int value) {
		ThrowException.ifLess(0, value, "value < 0");
		if (getGap() != value) {
			secondaryGap = value;
			invalidate();
		}
	}
	
	public int getSecondaryGap() {
		return getStyleAttribute(ATTRIBUTE_KEY_SECONDARY_GAP, secondaryGap);
	}
	
	public void setPrefSizeMode(PrefSizeMode value) {
		ThrowException.ifNull(value, "value == null");
		if (getPrefSizeMode() != value) {
			prefSizeMode = value;
			invalidate();
		}
	}
	
	public PrefSizeMode getPrefSizeMode() {
		return getStyleAttribute(ATTRIBUTE_KEY_PREF_SIZE_MODE, prefSizeMode);
	}
	
	@Override
	protected void layOutInternal() {
		PInsets insets = getInsets();
		PBounds ob = getOwner().getBoundsWithoutBorder();
		int gap = getGap();
		int sndGap = getSecondaryGap();
		int minX = ob.getX() + insets.getFromLeft();
		int minY = ob.getY() + insets.getFromTop();
		int alignedX = minX;
		int alignedY = minY;
		
		int prefW = 0;
		int prefH = 0;
		PSize[] compPrefSizes = new PSize[compList.size()];
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = getPreferredSizeOf(comp);
			compPrefSizes[i] = compPrefSize;
			prefW += compPrefSize.getWidth() + gap;
			prefH += compPrefSize.getHeight() + gap;
		}
		if (compPrefSizes.length > 0) {
			prefW -= gap;
			prefH -= gap;
		}
		
		ListAlignment align = getAlignment();
		boolean isHorizontal = align.isHorizontal();
		switch (align) {
		case CENTERED_LEFT_TO_RIGHT:
			alignedX = ob.getWidth() / 2 - prefW / 2;
			break;
		case CENTERED_TOP_TO_BOTTOM:
			alignedY = ob.getHeight() / 2 - prefH / 2;
			break;
		case RIGHT_TO_LEFT:
			alignedX = (ob.getFinalX() - insets.getFromRight()) - prefW;
			break;
		case BOTTOM_TO_TOP:
			alignedY = (ob.getFinalY() - insets.getFromBottom()) - prefH;
			break;
		case LEFT_TO_RIGHT:
		case TOP_TO_BOTTOM:
		default:
		}
		int maxX = ob.getFinalX() - insets.getFromRight();
		int maxY = ob.getFinalY() - insets.getFromBottom();
		int originX = Math.max(alignedX, minX);
		int originY = Math.max(alignedY, minY);
		int x = originX;
		int y = originY;
		int lineSize = 0;
		
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = compPrefSizes[i];
			int compPrefW = compPrefSize.getWidth();
			int compPrefH = compPrefSize.getHeight();
			
			if (isHorizontal) {
				if (lineSize < compPrefH) {
					lineSize = compPrefH;
				}
				if (x + compPrefW > maxX) {
					x = originX;
					y += lineSize + sndGap;
					setChildBounds(comp, x, y, compPrefW, compPrefH);
					x += compPrefW + gap;
					lineSize = compPrefH;
				} else {
					setChildBounds(comp, x, y, compPrefW, compPrefH);
					x += compPrefW + gap;
				}
			} else {
				if (lineSize < compPrefW) {
					lineSize = compPrefW;
				}
				if (y + compPrefH > maxY) {
					y = originY;
					x += lineSize + sndGap;
					setChildBounds(comp, x, y, compPrefW, compPrefH);
					y += compPrefH + gap;
					lineSize = compPrefW;
				} else {
					setChildBounds(comp, x, y, compPrefW, compPrefH);
					y += compPrefH + gap;
				}
			}
		}
	}
	
	@Override
	protected void onInvalidated() {
		if (getPrefSizeMode() == PrefSizeMode.AVOID_WRAPPING) {
			super.onInvalidated();
			return;
		} else if (getPrefSizeMode() != PrefSizeMode.PREFER_WRAPPING) {
			ThrowException.always("Unknown "+PrefSizeMode.class.getSimpleName()+": "+getPrefSizeMode());
		}
		PInsets insets = getInsets();
		PBounds bnds = getOwner().getBoundsWithoutBorder();
		int bndsW;
		int bndsH;
		if (bnds == null) {
			bndsW = 0;
			bndsH = 0;
		} else {
			bndsW = bnds.getWidth() - insets.getHorizontal();
			bndsH = bnds.getHeight() - insets.getVertical();
		}
		int gap = getGap();
		
		int prefW = 0;
		int prefH = 0;
		
		int curPrefW = 0;
		int curPrefH = 0;
		
		final boolean isHorizontal = getAlignment().isHorizontal();
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = getPreferredSizeOf(comp);
			int compPrefW = compPrefSize.getWidth();
			int compPrefH = compPrefSize.getHeight();
			
			if (isHorizontal) {
				if (curPrefW + compPrefW > bndsW) {
					if (prefW < compPrefW) {
						prefW = compPrefW;
					}
					prefH += curPrefH + gap;
					curPrefW = compPrefW + gap;
					curPrefH = compPrefH;
				} else {
					curPrefW += compPrefW + gap;
					if (curPrefH < compPrefH) {
						curPrefH = compPrefH;
					}
				}
			} else {
				if (curPrefH + compPrefH > bndsH) {
					if (prefH < compPrefH) {
						prefH = compPrefH;
					}
					prefW += curPrefW + gap;
					curPrefW = compPrefW;
					curPrefH = compPrefH + gap;
				} else {
					curPrefH += compPrefH + gap;
					if (curPrefW < compPrefW) {
						curPrefW = compPrefW;
					}
				}
			}
		}
		prefW += curPrefW;
		prefH += curPrefH;
		if (!compList.isEmpty()) {
			if (isHorizontal) {
				prefW -= gap;
			} else {
				prefH -= gap;
			}
		}
		prefW += insets.getHorizontal();
		prefH += insets.getVertical();
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
	}
	
	public static enum PrefSizeMode {
		AVOID_WRAPPING,
		PREFER_WRAPPING,
		;
	}
	
}