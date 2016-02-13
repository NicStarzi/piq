package edu.udo.piq.components.containers;

import edu.udo.piq.PRenderer;

public class PGlassPanel extends PPanel {
	
	public PGlassPanel() {
		super();
	}
	
	public void defaultRender(PRenderer renderer) {
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public boolean isElusive() {
		return true;
	}
	
}