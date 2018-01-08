package edu.udo.piq.components.popup;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.tools.ImmutablePSize;

public class PMenuCheckBox extends PCheckBox {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
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
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int w = bounds.getWidth();
		int h = bounds.getHeight();
		
		if (isEnabled()) {
			renderer.setColor(PColor.BLACK);
		} else {
			renderer.setColor(PColor.GREY50);
		}
		int x1 = x + 1;
		int y1 = y + h / 2;
		int x2 = x1 + 3;
		int y2 = y1;
		int x3 = x + w / 2;
		int y3 = y + h - 1;
		renderer.drawTriangle(x1, y1, x2, y2, x3, y3);
		
		int x4 = x3;
		int y4 = y3;
		int x5 = x + w - 1;
		int y5 = y + 1;
		int x6 = x5 - 3;
		int y6 = y5;
		renderer.drawTriangle(x4, y4, x5, y5, x6, y6);
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
}