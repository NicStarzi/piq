package edu.udo.piq.layouts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PSize;

public class PWrapLayout extends PListLayout {
	
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
	
	public void layOut() {
		PInsets insets = getInsets();
		PBounds ob = getOwner().getBounds();
		int gap = getGap();
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
					y += lineSize + gap;
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
					x += lineSize + gap;
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
	
	public PSize getPreferredSize() {
		PInsets insets = getInsets();
		PBounds bnds = getOwner().getBounds();
		int bndsW;
		int bndsH;
		if (bnds == null) {
			bndsW = 0;
			bndsH = 0;
		} else {
			bndsW = bnds.getWidth() - insets.getHorizontal();
			bndsH = bnds.getHeight() - insets.getVertical();
		}
		
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
//					if (prefW < curPrefW) {
//						prefW = curPrefW;
//					}
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
//					if (prefH < curPrefH) {
//						prefH = curPrefH;
//					}
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
		return prefSize;
	}
	
}