package edu.udo.piq.components.containers;

import edu.udo.piq.PRenderer;

public class PGlassPanel extends PPanel {
	
	public static final Object STYLE_ID = PGlassPanel.class;
	{
		setStyleID(STYLE_ID);
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	@Override
	public boolean isIgnoredByPicking() {
		return true;
	}
	
}