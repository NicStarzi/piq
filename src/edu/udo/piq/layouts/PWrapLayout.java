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
		case CENTERED_HORIZONTAL:
			alignedX = ob.getWidth() / 2 - prefW / 2;
			break;
		case CENTERED_VERTICAL:
			alignedY = ob.getHeight() / 2 - prefH / 2;
			break;
		case FROM_BOTTOM:
			alignedY = (ob.getFinalY() - insets.getFromBottom()) - prefH;
			break;
		case FROM_RIGHT:
			alignedX = (ob.getFinalX() - insets.getFromRight()) - prefW;
			break;
		case FROM_LEFT:
		case FROM_TOP:
		default:
		}
		int maxX = ob.getFinalX() - insets.getHorizontal();
		int maxY = ob.getFinalY() - insets.getVertical();
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
			setChildBounds(comp, x, y, compPrefW, compPrefH);
			
			if (isHorizontal) {
				if (lineSize < compPrefH) {
					lineSize = compPrefH;
				}
				int newX = x + compPrefW + gap;
				if (newX > maxX) {
					x = originX;
					y += lineSize + gap;
					lineSize = 0;
				} else {
					x = newX;
				}
			} else {
				if (lineSize < compPrefW) {
					lineSize = compPrefW;
				}
				int newY = y + compPrefH + gap;
				if (newY > maxY) {
					y = originY;
					x += lineSize + gap;
					lineSize = 0;
				} else {
					y = newY;
				}
			}
		}
	}
	
	public PSize getPreferredSize() {
		PInsets insets = getInsets();
		PBounds bnds = getOwner().getBounds();
		int w = bnds.getWidth() - insets.getHorizontal();
		int h = bnds.getHeight() - insets.getVertical();
		
		int prefW = 0;
		int prefH = 0;
		
		int curPrefW = 0;
		int curPrefH = 0;
		
		boolean isHorizontal = getAlignment().isHorizontal();
		for (int i = 0; i < compList.size(); i++) {
			PComponent comp = compList.get(i);
			PSize compPrefSize = getPreferredSizeOf(comp);
			int compPrefW = compPrefSize.getWidth();
			int compPrefH = compPrefSize.getHeight();
			
			if (isHorizontal) {
				if (curPrefW + compPrefW > w) {
					if (prefW < curPrefW) {
						prefW = curPrefW;
					}
					prefH += curPrefH + gap;
					curPrefW = 0;
					curPrefH = 0;
				}
				curPrefW += compPrefW + gap;
				if (curPrefH < compPrefH) {
					curPrefH = compPrefH;
				}
			} else {
				if (curPrefH + compPrefH > h) {
					if (prefH < curPrefH) {
						prefH = curPrefH;
					}
					prefW += curPrefW + gap;
					curPrefW = 0;
					curPrefH = 0;
				}
				curPrefH += compPrefH + gap;
				if (curPrefW < compPrefW) {
					curPrefW = compPrefW;
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