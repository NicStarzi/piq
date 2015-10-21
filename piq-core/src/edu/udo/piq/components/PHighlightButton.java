package edu.udo.piq.components;

import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.ImmutablePInsets;

public class PHighlightButton extends PButton {
	
	public static final PColor DEFAULT_HIGHLIGHT_COLOR = PColor.BLUE;
	
	private boolean highlighted;
	
	public PHighlightButton() {
		super();
		addObs(new PMouseObs() {
			public void onMouseMoved(PMouse mouse) {
				setHighlighted(isMouseOverThisOrChild());
			}
		});
		getLayoutInternal().setInsets(new ImmutablePInsets(1));
	}
	
	protected void setHighlighted(boolean value) {
		if (highlighted != value) {
			highlighted = value;
			if (isEnabled()) {
				fireReRenderEvent();
			}
		}
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}
	
	public void defaultRender(PRenderer renderer) {
		if (isHighlighted()) {
			PColor highlightColor = getHighlightColor();
			renderer.setColor(highlightColor);
			renderer.setRenderMode(renderer.getRenderModeFill());
			renderer.drawQuad(getBounds());
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	protected PColor getHighlightColor() {
		return DEFAULT_HIGHLIGHT_COLOR;
	}
	
}