package edu.udo.piq.components.defaults;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.containers.PPanel;

public class DefaultPTabBackground extends PPanel {
	
	public static final Object STYLE_ID = DefaultPTabBackground.class;
	{
		setStyleID(STYLE_ID);
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.GREY50);
		renderer.drawQuad(x, y, fx, fy);
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return PSize.ZERO_SIZE;
	}
	
}