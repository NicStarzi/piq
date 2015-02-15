package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PLayout;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.layouts.PFreeLayout;

public class PGlassPanel extends PPanel implements PRootOverlay {
	
	public PGlassPanel() {
		setLayout(new PFreeLayout(this));
	}
	
	public void setLayout(PLayout layout) {
		if (!(layout instanceof PFreeLayout)) {
			throw new IllegalArgumentException("layout="+layout);
		}
		super.setLayout(layout);
	}
	
	public PFreeLayout getLayout() {
		return (PFreeLayout) super.getLayout();
	}
	
	public void defaultRender(PRenderer renderer) {
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public PBounds getBounds() {
		return super.getBounds();
	}
	
	public boolean isElusive() {
		return true;
	}
	
}