package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PLayout;
import edu.udo.piq.util.ThrowException;

public class DefaultPRootOverlay extends PGlassPanel implements PRootOverlay {
	
	public static final Object STYLE_ID = DefaultPRootOverlay.class;
	{
		setStyleID(STYLE_ID);
	}
	
	public DefaultPRootOverlay() {
		setLayout(new PFreeLayout(this));
	}
	
	@Override
	public void setLayout(PLayout layout) {
		ThrowException.ifTypeCastFails(layout, PFreeLayout.class,
				"!(layout instanceof PFreeLayout)");
		super.setLayout(layout);
	}
	
	public void setLayout(PFreeLayout layout) {
		super.setLayout(layout);
	}
	
	@Override
	public PFreeLayout getLayout() {
		return (PFreeLayout) super.getLayout();
	}
	
	@Override
	public PBounds getBounds() {
		return super.getBounds();
	}
	
}