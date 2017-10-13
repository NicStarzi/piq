package edu.udo.piq.tutorial;

import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class ColoredBox extends AbstractPComponent {
	
	private static final PSize DEFAULT_SIZE = new ImmutablePSize(60, 60);
	
	private PColor normalColor = PColor.RED;
	private PColor pressedColor = PColor.BLUE;
	private boolean isPressed = false;
	
	public ColoredBox() {
		super();
		
		addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				if (btn == MouseButton.LEFT && isMouseOverThisOrChild()) {
					isPressed = true;
					fireReRenderEvent();
				}
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				if (btn == MouseButton.LEFT && isPressed) {
					isPressed = false;
					fireReRenderEvent();
				}
			}
		});
	}
	
	public void setColor(PColor color) {
		this.normalColor = color;
		fireReRenderEvent();
	}
	
	public PColor getColor() {
		return normalColor;
	}
	
	public void defaultRender(PRenderer renderer) {
		if (isPressed) {
			renderer.setColor(pressedColor);
		} else {
			renderer.setColor(normalColor);
		}
		renderer.drawQuad(getBounds());
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_SIZE;
	}
	
}