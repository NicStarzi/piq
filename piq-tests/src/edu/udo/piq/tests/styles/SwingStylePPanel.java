package edu.udo.piq.tests.styles;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.style.MutablePStyleComponent;

public class SwingStylePPanel extends MutablePStyleComponent {
	
	private static final PColor COLOR_BG = PColor.WHITE.mult1(0.15);
	
	@Override
	public boolean fillsAllPixels(PComponent component) {
		return true;
	}
	
	@Override
	public PSize getPreferredSize(PComponent component) {
		return component.getDefaultPreferredSize();
	}
	
	@Override
	public void render(PRenderer renderer, PComponent component) {
		PBounds bnds = component.getBoundsWithoutBorder();
		renderer.setColor(COLOR_BG);
		renderer.drawQuad(bnds);
	}
	
}