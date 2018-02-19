package edu.udo.piq.scroll2;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.AbstractEnumPLayout;
import edu.udo.piq.layouts.Axis;

public class PScrollBarLayout extends AbstractEnumPLayout<PScrollBarLayout.ScrollBarPart> {
	
	private PScrollBar scrollBar;
	
	public PScrollBarLayout(PScrollBar scrollBar) {
		super(scrollBar, PScrollBarLayout.ScrollBarPart.class);
		this.scrollBar = scrollBar;
	}
	
	@Override
	protected void onInvalidated() {
		prefSize.set(PScrollBarKnob.DEFAULT_SIZE);
	}
	
	@Override
	protected void layOutInternal() {
		PBounds ob = getOwner().getBoundsWithoutBorder();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		int fx = x + w;
		int fy = y + h;
		
		int bgX = x;
		int bgY = y;
		int bgW = w;
		int bgH = h;
		
		int knobX;
		int knobY;
		PComponent knob = getChildForConstraint(ScrollBarPart.KNOB);
		PSize knobSize = getPreferredSizeOf(knob);
		int knobW = knobSize.getWidth();
		int knobH = knobSize.getHeight();
		
		PComponent btnUp = getChildForConstraint(ScrollBarPart.BTN_DECREMENT);
		PComponent btnDown = getChildForConstraint(ScrollBarPart.BTN_INCREMENT);
		PSize btnUpSize = getPreferredSizeOf(btnUp);
		int btnW;
		int btnH;
		int btnDownX;
		int btnDownY;
		double scrollPercent = scrollBar.getScrollPercent();
		if (scrollBar.getAxis() == Axis.X) {
			btnW = btnUpSize.getWidth();
			btnH = h;
			btnDownX = fx - btnW;
			btnDownY = y;
			
			bgX += btnW;
			bgW -= btnW * 2;
			
			knobX = x + btnW + knobW / 2 + (int) ((bgW - knobW) * scrollPercent) - knobW / 2;
			knobY = y;
		} else {
			btnW = w;
			btnH = btnUpSize.getHeight();
			btnDownX = x;
			btnDownY = fy - btnH;
			
			bgY += btnH;
			bgH -= btnH * 2;
			
			knobX = x;
			knobY = y + btnH + knobH / 2 + (int) ((bgH - knobH) * scrollPercent) - knobH / 2;
		}
		setChildCellFilled(btnUp, x, y, btnW, btnH);
		setChildCellFilled(btnDown, btnDownX, btnDownY, btnW, btnH);
		
		PComponent bg = getChildForConstraint(ScrollBarPart.BACKGROUND);
		setChildCellFilled(bg, bgX, bgY, bgW, bgH);
		
		setChildCellFilled(knob, knobX, knobY, knobW, knobH);
	}
	
	public static enum ScrollBarPart {
		BTN_DECREMENT, BTN_INCREMENT, KNOB, BACKGROUND,
		;
	}
	
}