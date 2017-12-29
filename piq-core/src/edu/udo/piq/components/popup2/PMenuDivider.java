package edu.udo.piq.components.popup2;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSpacer;
import edu.udo.piq.components.popup2.AbstractPMenuItem.MenuEntryPart;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePSize;

public class PMenuDivider extends AbstractPLayoutOwner {
	
	public static final PSize DEFAULT_SIZE = new ImmutablePSize(2, 8);
	
	public PMenuDivider() {
		DelegatedPLayout layout = new DelegatedPLayout(this);
		setLayout(layout);
		
		Object constrFirst = MenuEntryPart.ALL.get(0).getDefaultIndex();
		Object constrSecond = MenuEntryPart.ALL.get(MenuEntryPart.COUNT - 1).getDefaultIndex();
		layout.addChild(new PSpacer(0, 0), constrFirst);
		layout.addChild(new PSpacer(0, 0), constrSecond);
	}
	
	@Override
	public boolean isFocusable() {
		return false;
	}
	
	@Override
	public boolean isStrongFocusOwner() {
		return false;
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return DEFAULT_SIZE;
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBoundsWithoutBorder();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int h = bnds.getHeight();
		
		int barX = x;
		int barY = y + h / 2 - 1;
		int barFx = fx;
		int barFy = barY + 2;
		renderer.setColor(PColor.GREY50);
		renderer.strokeTop(barX, barY, barFx, barFy);
		renderer.setColor(PColor.WHITE);
		renderer.strokeBottom(barX, barY, barFx, barFy);
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}