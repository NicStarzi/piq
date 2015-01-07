package edu.udo.piq.components;

import edu.udo.piq.PRenderer;
import edu.udo.piq.layouts.PFreeLayout;

public class PGlassPanel extends PPanel {
	
	public PGlassPanel() {
		setLayout(new PFreeLayout(this));
	}
	
	public PFreeLayout getLayout() {
		return (PFreeLayout) super.getLayout();
	}
	
	public void defaultRender(PRenderer renderer) {
	}
	
	public boolean isDefaultOpaque() {
		return false;
	}
	
}