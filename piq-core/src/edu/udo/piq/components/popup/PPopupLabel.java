package edu.udo.piq.components.popup;

import edu.udo.piq.PColor;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextModel;

public class PPopupLabel extends PLabel implements PPopupComponent {
	
	public static final PColor HIGHLIGHT_TEXT_COLOR = PColor.WHITE;
	
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
	
	public void setHighlighted(boolean value) {
		if (highlighted != value) {
			highlighted = value;
			fireReRenderEvent();
		}
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}
	
	public PColor getDefaultTextColor() {
		if (isHighlighted()) {
			return HIGHLIGHT_TEXT_COLOR;
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