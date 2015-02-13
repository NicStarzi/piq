package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.AbstractPBorder;

public class PLineBorder extends AbstractPBorder {
	
	public PLineBorder() {
		super();
	}
	
	public PLineBorder(PComponent content) {
		super(content);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.BLACK);
		renderer.drawQuad( x - 0,  y - 0, fx + 0,  y + 1);
		renderer.drawQuad( x - 0,  y - 0,  x + 1, fy + 0);
		renderer.drawQuad(fx - 1,  y - 0, fx + 0, fy + 0);
		renderer.drawQuad( x - 0, fy - 1, fx + 0, fy + 0);
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}