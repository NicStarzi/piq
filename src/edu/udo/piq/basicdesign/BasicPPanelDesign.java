package edu.udo.piq.basicdesign;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.util.PCompUtil;

public class BasicPPanelDesign implements PDesign {
	
	protected PColor backgroundColor = PColor.GREY75;
	
	public void setBackgroundColor(PColor color) {
		backgroundColor = color;
	}
	
	public PColor getBackgroundColor() {
		return backgroundColor;
	}
	
	public PSize getPreferredSize(PComponent component)
			throws NullPointerException, IllegalArgumentException {
		return PCompUtil.getPreferredSizeOf(component);
	}
	
	public void render(PRenderer renderer, PComponent component)
			throws NullPointerException, IllegalArgumentException {
		PBounds bnds = PCompUtil.getBoundsOf(component);
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(getBackgroundColor());
		renderer.drawQuad(x, y, fx, fy);
	}
	
}