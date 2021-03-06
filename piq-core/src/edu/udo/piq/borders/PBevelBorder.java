package edu.udo.piq.borders;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.AbstractPBorder;
import edu.udo.piq.tools.ImmutablePInsets;

public class PBevelBorder extends AbstractPBorder implements PBorder {
	
	protected final ImmutablePInsets insets = new ImmutablePInsets(2);
	
	public PBevelBorder() {
	}
	
	@Override
	public PInsets getDefaultInsets(PComponent component) {
		return insets;
	}
	
	@Override
	public void defaultRender(PRenderer renderer, PComponent component) {
		PBounds bnds = component.getBounds();
		PInsets insets = getInsets(component);
		int top = insets.getFromTop();
		int lft = insets.getFromLeft();
		int rgt = insets.getFromRight();
		int btm = insets.getFromBottom();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.GREY50);
		renderer.strokeBottom(x, y, fx, fy, btm);
		renderer.strokeRight(x, y, fx, fy, rgt);
		renderer.setColor(PColor.BLACK);
		renderer.strokeBottom(x, y, fx, fy, 1);
		renderer.strokeRight(x, y, fx, fy, 1);
		renderer.setColor(PColor.WHITE);
		renderer.strokeTop(x, y, fx - 2, fy - 2, top);
		renderer.strokeLeft(x, y, fx - 2, fy - 2, lft);
		renderer.setColor(PColor.GREY75);
		renderer.strokeTop(x, y, fx - 1, fy - 1, 1);
		renderer.strokeLeft(x, y, fx - 1, fy - 1, 1);
	}
	
	@Override
	public boolean defaultFillsAllPixels(PComponent component) {
		return true;
	}
	
}