package edu.udo.piq.components.popup;

import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextModel;

public class PPopupLabel extends PLabel implements PPopupComponent {
	
	public static final PColor DEFAULT_HIGHLIGHT_BACKGROUND_COLOR = PColor.BLUE;
	public static final PColor DEFAULT_HIGHLIGHT_TEXT_COLOR = PColor.WHITE;
	public static final PColor DEFAULT_DISABLED_TEXT_COLOR = PColor.GREY50;
	
	protected boolean enabled = true;
	protected boolean highlighted;
	
	public PPopupLabel(Object defaultModelValue) {
		super(defaultModelValue);
	}
	
	public PPopupLabel(PTextModel model) {
		super(model);
	}
	
	public PPopupLabel() {
		super();
	}
	
	public void setEnabled(boolean value) {
		if (enabled != value) {
			enabled = value;
			fireReRenderEvent();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setHighlighted(boolean value) {
		if (highlighted != value) {
			highlighted = value;
			fireReRenderEvent();
		}
	}
	
	public boolean isHighlighted() {
		return highlighted && isEnabled();
	}
	
	public void defaultRender(PRenderer renderer) {
		if (isHighlighted()) {
			renderer.setColor(DEFAULT_HIGHLIGHT_BACKGROUND_COLOR);
			renderer.drawQuad(getBounds());
		}
		super.defaultRender(renderer);
	}
	
	public boolean defaultFillsAllPixels() {
		return isHighlighted() || super.defaultFillsAllPixels();
	}
	
	protected PColor getDefaultTextColor() {
		if (!isEnabled()) {
			return DEFAULT_DISABLED_TEXT_COLOR;
		}
		if (isHighlighted()) {
			return DEFAULT_HIGHLIGHT_TEXT_COLOR;
		}
		return super.getDefaultTextColor();
	}
	
	public void addObs(PPopupComponentObs obs) {
		// We dont need popup component observers since labels 
		// can not force a popup close.
	}
	
	public void removeObs(PPopupComponentObs obs) {
		// see addObs(PPopupComponentObs obs)
	}
	
}