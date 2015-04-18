package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPContainer;
import edu.udo.piq.tools.ImmutablePSize;

public class PPanel extends AbstractPContainer {
	
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(20, 20);
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(x, y, fx, fy);
	}
	
	public PSize getDefaultPreferredSize() {
		if (getLayout() != null) {
			return getLayout().getPreferredSize();
		}
		return DEFAULT_PREFERRED_SIZE;
	}
}