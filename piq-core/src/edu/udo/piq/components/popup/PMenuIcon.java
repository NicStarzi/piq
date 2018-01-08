package edu.udo.piq.components.popup;

import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PPicture;

public class PMenuIcon extends PPicture {
	
	protected boolean enabled = true;
	
	public void setEnabled(boolean value) {
		if (enabled != value) {
			enabled = value;
			fireReRenderEvent();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		if (isEnabled()) {
			super.defaultRender(renderer);
		}
	}
	
}