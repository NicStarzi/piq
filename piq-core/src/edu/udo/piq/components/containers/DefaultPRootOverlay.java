package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PLayout;
import edu.udo.piq.util.ThrowException;

public class DefaultPRootOverlay extends PGlassPanel implements PRootOverlay {
	
	public DefaultPRootOverlay() {
		setLayout(new PFreeLayout(this));
	}
	
	public void setLayout(PLayout layout) {
		ThrowException.ifTypeCastFails(layout, PFreeLayout.class, 
				"!(layout instanceof PFreeLayout)");
		super.setLayout(layout);
	}
	
	public void setLayout(PFreeLayout layout) {
		super.setLayout(layout);
	}
	
	public PFreeLayout getLayout() {
		return (PFreeLayout) super.getLayout();
	}
	
	public PBounds getBounds() {
		return super.getBounds();
	}
	
}