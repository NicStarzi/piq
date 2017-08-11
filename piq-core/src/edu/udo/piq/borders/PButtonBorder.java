package edu.udo.piq.borders;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PButton;
import edu.udo.piq.tools.AbstractPBorder;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class PButtonBorder extends AbstractPBorder implements PBorder {
	
	public static final ImmutablePInsets INSETS = new ImmutablePInsets(2);
	
	@Override
	public PInsets getDefaultInsets(PComponent component) {
		return INSETS;
	}
	
	@Override
	public void defaultRender(PRenderer renderer, PComponent component) {
		PButton btn = ThrowException.ifTypeCastFails(component, PButton.class,
				"(component instanceof PButton) == false");
		PBounds bnds = btn.getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		if (btn.isPressed()) {
			renderer.setColor(PColor.GREY875);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(PColor.GREY50);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			
			renderer.setColor(PColor.GREY50);
			renderer.strokeBottom(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeRight(x + 1, y + 1, fx - 1, fy - 1);
			renderer.setColor(PColor.GREY25);
			renderer.strokeTop(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeLeft(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.BLACK);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(PColor.WHITE);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			
			renderer.setColor(PColor.GREY25);
			renderer.strokeBottom(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeRight(x + 1, y + 1, fx - 1, fy - 1);
			renderer.setColor(PColor.GREY875);
			renderer.strokeTop(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeLeft(x + 1, y + 1, fx - 1, fy - 1);
		}
	}
	
	@Override
	public boolean defaultFillsAllPixels(PComponent component) {
		return true;
	}
}