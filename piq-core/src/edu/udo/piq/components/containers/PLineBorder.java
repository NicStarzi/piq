package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.AbstractPBorder;
import edu.udo.piq.tools.ImmutablePInsets;

public class PLineBorder extends AbstractPBorder {
	
	public PLineBorder() {
		this(1);
	}
	
	public PLineBorder(int lineThickness) {
		super();
		setLineThickness(lineThickness);
	}
	
	public PLineBorder(PComponent content) {
		super(content);
	}
	
	public PLineBorder(PComponent content, int lineThickness) {
		super(content);
		setLineThickness(lineThickness);
	}
	
	public void setLineThickness(int value) {
		getLayout().setInsets(new ImmutablePInsets(value));
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		PInsets insets = getLayout().getInsets();
		int top = insets.getFromTop();
		int lft = insets.getFromLeft();
		int rgt = insets.getFromRight();
		int btm = insets.getFromBottom();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(PColor.BLACK);
		// Draw top
		renderer.drawQuad( x - 0,  y - 0, fx + 0,  y + top);
		// Draw left
		renderer.drawQuad( x - 0,  y - 0,  x + lft, fy + 0);
		// Draw right
		renderer.drawQuad(fx - rgt,  y - 0, fx + 0, fy + 0);
		// Draw bottom
		renderer.drawQuad( x - 0, fy - btm, fx + 0, fy + 0);
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}