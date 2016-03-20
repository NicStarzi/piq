package edu.udo.piq.components;

import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PColoredRectangle extends AbstractPComponent {
	
	public static final PSize DEFAULT_SIZE = new ImmutablePSize(100, 100);
	
	private PColor color = PColor.BLACK;
	private PSize prefSize = DEFAULT_SIZE;
	
	public PColoredRectangle() {
		this(PColor.BLACK);
	}
	
	public PColoredRectangle(PColor color) {
		this.color = color;
	}
	
	public void setColor(PColor color) {
		this.color = color;
		fireReRenderEvent();
	}
	
	public PColor getColor() {
		return color;
	}
	
	public void setSize(PSize size) {
		prefSize = size;
		firePreferredSizeChangedEvent();
	}
	
	public PSize getSize() {
		return prefSize;
	}
	
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(getColor());
		renderer.drawQuad(getBounds());
	}
	
	public PSize getDefaultPreferredSize() {
		return prefSize;
	}
	
}