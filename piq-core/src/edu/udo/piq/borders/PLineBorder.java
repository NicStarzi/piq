package edu.udo.piq.borders;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.AbstractPBorder;
import edu.udo.piq.tools.MutablePInsets;

public class PLineBorder extends AbstractPBorder {
	
	protected final MutablePInsets insets = new MutablePInsets();
	
	public PLineBorder() {
		this(1);
	}
	
	public PLineBorder(int lineThickness) {
		super();
		setLineThickness(lineThickness);
	}
	
//	public PLineBorder(PComponent content) {
//		this(content, 1);
//	}
//	
//	public PLineBorder(PComponent content, int lineThickness) {
//		super(content);
//		setLineThickness(lineThickness);
//	}
	
	public void setLineThickness(int value) {
		if (getLineThickness() != value) {
			insets.set(value);
			fireInsetsChangedEvent();
		}
//		getLayout().setInsets(new ImmutablePInsets(value));
	}
	
	public int getLineThickness() {
		return insets.getFromTop();
	}
	
	public PInsets getDefaultInsets(PComponent component) {
		return insets;
	}
	
	public void defaultRender(PRenderer renderer, PComponent component) {
		PBounds bnds = component.getBounds();
//		PInsets insets = getLayout().getInsets();
		PInsets insets = getDefaultInsets(component);
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
	
	public boolean defaultFillsAllPixels(PComponent component) {
		return true;
	}
	
}