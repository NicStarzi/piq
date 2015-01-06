package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PRenderUtil;

public class PScrollBarButton extends PButton {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	
	public void setContent(PComponent component) {
		throw new UnsupportedOperationException();
	}
	
	public PComponent getContent() {
		return null;
	}
	
	public PCentricLayout getLayout() {
		return null;
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		if (isPressed()) {
			renderer.setColor(PColor.GREY25);
			PRenderUtil.strokeQuad(renderer, x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.BLACK);
			PRenderUtil.strokeBottom(renderer, x, y, fx, fy);
			PRenderUtil.strokeRight(renderer, x, y, fx, fy);
			renderer.setColor(PColor.WHITE);
			PRenderUtil.strokeTop(renderer, x, y, fx, fy);
			PRenderUtil.strokeLeft(renderer, x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
		//TODO: Draw triangle
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public boolean isFocusable() {
		return false;
	}
	
}