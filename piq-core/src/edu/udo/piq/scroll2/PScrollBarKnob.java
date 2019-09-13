package edu.udo.piq.scroll2;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PScrollBarKnob extends AbstractPComponent {
	
	public static final PSize DEFAULT_SIZE = new ImmutablePSize(18, 18);
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.BLACK);
		renderer.strokeBottom(x, y, fx, fy);
		renderer.strokeRight(x, y, fx, fy);
		renderer.setColor(PColor.WHITE);
		renderer.strokeTop(x, y, fx, fy);
		renderer.strokeLeft(x, y, fx, fy);
		
		renderer.setColor(PColor.GREY25);
		renderer.strokeBottom(x + 1, y + 1, fx - 1, fy - 1);
		renderer.strokeRight(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.GREY875);
		renderer.strokeTop(x + 1, y + 1, fx - 1, fy - 1);
		renderer.strokeLeft(x + 1, y + 1, fx - 1, fy - 1);
		
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(x + 2, y + 2, fx - 2, fy - 2);
	}
	
	@Override
	protected PSize getNoLayoutDefaultPreferredSize() {
		return DEFAULT_SIZE;
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}