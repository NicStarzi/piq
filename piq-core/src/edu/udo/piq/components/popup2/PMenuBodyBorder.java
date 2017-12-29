package edu.udo.piq.components.popup2;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.AbstractPBorder;
import edu.udo.piq.tools.ImmutablePInsets;

public class PMenuBodyBorder extends AbstractPBorder {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(2);
	
	@Override
	public PInsets getDefaultInsets(PComponent component) {
		return DEFAULT_INSETS;
	}
	
	@Override
	public void defaultRender(PRenderer renderer, PComponent component) {
		PBounds bnds = component.getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.GREY75);
		renderer.strokeTop(x + 0, y + 0, fx - 1, fy - 1);
		renderer.strokeLeft(x + 0, y + 0, fx - 1, fy - 1);
		renderer.setColor(PColor.WHITE);
		renderer.strokeTop(x + 1, y + 1, fx - 2, fy - 2);
		renderer.strokeLeft(x + 1, y + 1, fx - 2, fy - 2);
		renderer.setColor(PColor.GREY50);
		renderer.strokeBottom(x + 1, y + 1, fx - 1, fy - 1);
		renderer.strokeRight(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.GREY25);
		renderer.strokeBottom(x + 0, y + 0, fx - 0, fy - 0);
		renderer.strokeRight(x + 0, y + 0, fx - 0, fy - 0);
	}
	
	@Override
	public boolean defaultFillsAllPixels(PComponent component) {
		return true;
	}
	
}