package edu.udo.piq.tests.styles;

import java.awt.Graphics2D;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PButton;
import edu.udo.piq.style.MutablePStyleBorder;
import edu.udo.piq.style.MutablePStyleComponent;
import edu.udo.piq.style.PStyleBorder;
import edu.udo.piq.swing.SwingPRenderer;
import edu.udo.piq.tools.ImmutablePInsets;

public class SwingStylePButton extends MutablePStyleComponent {
	
	private static final PColor COLOR_FOCUS = PColor.WHITE;
	private static final PColor COLOR_UNFOCUS = COLOR_FOCUS.mult1(1.0, 1.0, 1.0, 0.5);
	private static final PColor COLOR_PRESSED = PColor.YELLOW;
	
	@Override
	public boolean fillsAllPixels(PComponent component) {
		return false;
	}
	
	@Override
	public PStyleBorder getBorderStyle(PComponent component, PBorder border) {
		return BORDER_STYLE;
	}
	
	@Override
	public void render(PRenderer renderer, PComponent component) {
		PButton self = (PButton) component;
		PBounds bnds = self.getBoundsWithoutBorder();
		int x = bnds.getX();
		int y = bnds.getY();
		int w = bnds.getWidth();
		int h = bnds.getHeight();
		
		SwingPRenderer swingRenderer = (SwingPRenderer) renderer;
		Graphics2D g2d = swingRenderer.getAwtGraphics();
		
		if (self.isPressed()) {
			renderer.setColor(COLOR_PRESSED);
		} else if (self.hasFocus()) {
			renderer.setColor(COLOR_FOCUS);
		} else {
			renderer.setColor(COLOR_UNFOCUS);
		}
		g2d.fillRoundRect(x, y, w, h, BORDER_INSETS.getFromLeft(), BORDER_INSETS.getFromTop());
	}
	
	public static final PInsets BORDER_INSETS = new ImmutablePInsets(4);
	public static final PStyleBorder BORDER_STYLE = new MutablePStyleBorder() {
		@Override
		public boolean fillsAllPixels(PBorder border, PComponent component) {
			return false;
		}
		@Override
		public PInsets getInsetsFor(PBorder border, PComponent component) {
			return BORDER_INSETS;
		}
		@Override
		public void render(PRenderer renderer, PBorder border, PComponent component) {
			PBounds bnds = component.getBounds();
			int x = bnds.getX();
			int y = bnds.getY();
			int w = bnds.getWidth() - 1;
			int h = bnds.getHeight() - 1;
			
			SwingPRenderer swingRenderer = (SwingPRenderer) renderer;
			Graphics2D g2d = swingRenderer.getAwtGraphics();
			
			if (component.hasFocus()) {
				renderer.setColor(COLOR_FOCUS);
			} else {
				renderer.setColor(COLOR_UNFOCUS);
			}
			int arc = BORDER_INSETS.getFromLeft() * 2;
			g2d.drawRoundRect(x, y, w, h, arc, arc);
		}
	};
	
}