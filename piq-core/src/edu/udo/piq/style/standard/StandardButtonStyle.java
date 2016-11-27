package edu.udo.piq.style.standard;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PStyleBorder;
import edu.udo.piq.PStyleComponent;
import edu.udo.piq.PStyleLayout;
import edu.udo.piq.borders.PButtonBorder;
import edu.udo.piq.components.PButton;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.tools.ImmutablePColor;
import edu.udo.piq.util.ThrowException;

public class StandardButtonStyle implements PStyleComponent {
	
	protected PColor bgColor = new ImmutablePColor(0.66, 0.66, 1.0);
	protected PColor focusColor = new ImmutablePColor(1.0, 1.0, 0.33);
	protected StandardStyleSheet sheet;
	
	public StandardButtonStyle() {
		this(null);
	}
	
	public StandardButtonStyle(StandardStyleSheet styleSheet) {
		sheet = styleSheet;
	}
	
	public void setFocusColor(PColor value) {
		focusColor = value;
	}
	
	public PColor getFocusColor() {
		return focusColor;
	}
	
	public void setBackgroundColor(PColor value) {
		bgColor = value;
	}
	
	public PColor getBackgroundColor() {
		return bgColor;
	}
	
	@Override
	public boolean fillsAllPixels(PComponent component) {
		return component.getBorder().fillsAllPixels(component);
	}
	
	@Override
	public void render(PRenderer renderer, PComponent component) {
		PButton btn = ThrowException.ifTypeCastFails(component, PButton.class,
				"(component instanceof PButton) == false");
		PBounds bnds = btn.getBoundsWithoutBorder();
		renderer.setColor(bgColor);
		renderer.drawQuad(bnds);
		
		if (btn.hasFocus() && btn.getContent() != null) {
			PBounds focusBnds = btn.getContent().getBounds();
			
			renderer.setRenderMode(renderer.getRenderModeOutlineDashed());
			renderer.setColor(getFocusColor());
			renderer.drawQuad(focusBnds);
		}
	}
	
	@Override
	public PStyleBorder getBorderStyle(PComponent component, PBorder border) {
		if (sheet != null) {
			return sheet.getStyleFor(border);
		}
		if (border instanceof PButtonBorder) {
			return new StandardButtonBorderStyle();
		}
		return PStyleBorder.DEFAULT_BORDER_STYLE;
	}
	
	@Override
	public PStyleLayout getLayoutStyle(PComponent component, PReadOnlyLayout layout) {
		if (sheet != null) {
			return sheet.getStyleFor(layout);
		}
		if (layout instanceof PAnchorLayout) {
			return new StandardButtonLayoutStyle();
		}
		return PStyleLayout.DEFAULT_LAYOUT_STYLE;
	}
	
}