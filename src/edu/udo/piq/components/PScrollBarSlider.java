package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePSize;

public class PScrollBarSlider extends PSlider {
	
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, 12);
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		int centerY = y + bnds.getHeight() / 2;
		
		renderer.setColor(PColor.BLACK);
		renderer.drawQuad(x, centerY - 1, fx, centerY + 1);
		
		int sliderX = x + (int) (getModel().getValuePercent() * bnds.getWidth()) - DEFAULT_SLIDER_WIDTH / 2;
		int sliderFx = sliderX + DEFAULT_SLIDER_WIDTH;
		
		renderer.setColor(PColor.GREY50);
		renderer.drawQuad(sliderX, y, sliderFx, fy);
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public boolean isFocusable() {
		return false;
	}
	
}