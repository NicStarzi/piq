package edu.udo.piq.borders;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.tools.AbstractPBorder;
import edu.udo.piq.tools.MutablePInsets;

public class PLineBorder extends AbstractPBorder {
	
	public static final int DEFAULT_LINE_THICKNESS = 1;
	public static final PColor DEFAULT_LINE_COLOR = PColor.BLACK;
	
	protected final MutablePInsets insets = new MutablePInsets();
	protected PColor color = DEFAULT_LINE_COLOR;
	
	public PLineBorder() {
		this(DEFAULT_LINE_COLOR, DEFAULT_LINE_THICKNESS);
	}
	
	public PLineBorder(int lineThickness) {
		this(DEFAULT_LINE_COLOR, lineThickness);
	}
	
	public PLineBorder(PColor lineColor) {
		this(lineColor, DEFAULT_LINE_THICKNESS);
	}
	
	public PLineBorder(PColor lineColor, int lineThickness) {
		super();
		setLineColor(lineColor);
		setLineThickness(lineThickness);
	}
	
	public void setLineThickness(int value) {
		if (getLineThickness() != value) {
			insets.set(value);
			fireInsetsChangedEvent();
		}
	}
	
	public int getLineThickness() {
		return insets.getFromTop();
	}
	
	public void setLineColor(PColor value) {
		if (!color.equals(value)) {
			color = value;
			fireInsetsChangedEvent();
		}
	}
	
	public PColor getLineColor() {
		return color;
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
		
		renderer.setColor(getLineColor());
		// Draw top
		renderer.drawQuad( x - 0,  y - 0, fx + 0,  y + top);
		// Draw left
		renderer.drawQuad( x - 0,  y - 0,  x + lft, fy + 0);
		// Draw right
		renderer.drawQuad(fx - rgt,  y - 0, fx + 0, fy + 0);
		// Draw bottom
		renderer.drawQuad( x - 0, fy - btm, fx + 0, fy + 0);
	}
	
	@Override
	public boolean defaultFillsAllPixels(PComponent component) {
		return true;
	}
	
}