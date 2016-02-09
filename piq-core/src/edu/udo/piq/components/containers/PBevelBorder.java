package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.AbstractPBorder;
import edu.udo.piq.tools.ImmutablePInsets;

public class PBevelBorder extends AbstractPBorder {
	
	protected static final PInsets BEVEL_INSETS = new ImmutablePInsets(2, 1, 2, 1);
	
	public PBevelBorder() {
		super();
		getLayout().setInsets(BEVEL_INSETS);
	}
	
	public PBevelBorder(PComponent content) {
		super(content);
		getLayout().setInsets(BEVEL_INSETS);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		PInsets insets = getLayout().getInsets();
		int x = bnds.getX() + insets.getFromLeft();
		int y = bnds.getY() + insets.getFromTop();
		int fx = bnds.getFinalX() - insets.getFromRight();
		int fy = bnds.getFinalY() - insets.getFromBottom();
		
		renderer.setColor(PColor.RED);
//		renderer.setColor(PColor.BLACK);
		renderer.strokeBottom(x, y, fx, fy);
		renderer.strokeRight(x, y, fx, fy);
//		renderer.setColor(PColor.GREY75);
		renderer.strokeTop(x, y, fx, fy);
		renderer.strokeLeft(x, y, fx, fy);
//		renderer.setColor(PColor.WHITE);
		renderer.strokeTop(x, y + 1, fx - 2, fy);
		renderer.strokeLeft(x + 1, y, fx, fy - 2);
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}