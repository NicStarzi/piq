package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PDivider extends AbstractPComponent {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(6, 6);
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.strokeTop(x, y, fx, fy);
		renderer.strokeLeft(x, y, fx, fy);
		renderer.setColor(PColor.BLACK);
		renderer.strokeRight(x, y, fx, fy);
		renderer.strokeBottom(x, y, fx, fy);
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public boolean isFocusable() {
		return false;
	}
	
}