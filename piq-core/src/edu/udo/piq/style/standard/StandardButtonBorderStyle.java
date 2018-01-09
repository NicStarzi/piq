package edu.udo.piq.style.standard;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PButton;
import edu.udo.piq.style.MutablePStyle;
import edu.udo.piq.style.PStyleBorder;
import edu.udo.piq.tools.ImmutablePColor;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ThrowException;

public class StandardButtonBorderStyle extends MutablePStyle implements PStyleBorder {
	
	public static final PInsets INSETS = new ImmutablePInsets(2);
	protected PColor lightInner = new ImmutablePColor(0.66, 0.66, 1.0);
	protected PColor lightOuter = new ImmutablePColor(0.9, 0.9, 1.0);
	protected PColor shadowInner = new ImmutablePColor(0.33, 0.33, 0.75);
	protected PColor shadowOuter = new ImmutablePColor(0.1, 0.1, 0.3);
	
	public void setColorHighlightInner(PColor value) {
		lightInner = value;
		fireReRenderEvent();
	}
	
	public PColor getColorHighlightInner() {
		return lightInner;
	}
	
	public void setColorHighlightOuter(PColor value) {
		lightOuter = value;
		fireReRenderEvent();
	}
	
	public PColor getColorHighlightOuter() {
		return lightOuter;
	}
	
	public void setColorShadowInner(PColor value) {
		shadowInner = value;
		fireReRenderEvent();
	}
	
	public PColor getColorShadowInner() {
		return shadowInner;
	}
	
	public void setColorShadowOuter(PColor value) {
		shadowOuter = value;
		fireReRenderEvent();
	}
	
	public PColor getColorShadowOuter() {
		return shadowOuter;
	}
	
	@Override
	public PInsets getInsetsFor(PBorder border, PComponent component) {
		return INSETS;
	}
	
	@Override
	public void render(PRenderer renderer,
			PBorder border, PComponent component)
	{
		PButton btn = ThrowException.ifTypeCastFails(component, PButton.class,
				"(component instanceof PButton) == false");
		PBounds bnds = btn.getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		if (btn.isPressed()) {
			renderer.setColor(getColorHighlightInner());
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(getColorShadowInner());
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			
			renderer.setColor(getColorShadowInner());
			renderer.strokeBottom(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeRight(x + 1, y + 1, fx - 1, fy - 1);
			renderer.setColor(getColorShadowOuter());
			renderer.strokeTop(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeLeft(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(getColorShadowOuter());
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(getColorHighlightOuter());
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			
			renderer.setColor(getColorShadowInner());
			renderer.strokeBottom(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeRight(x + 1, y + 1, fx - 1, fy - 1);
			renderer.setColor(getColorHighlightInner());
			renderer.strokeTop(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeLeft(x + 1, y + 1, fx - 1, fy - 1);
		}
	}
	
	@Override
	public boolean fillsAllPixels(PBorder border, PComponent component) {
		return true;
	}
	
}