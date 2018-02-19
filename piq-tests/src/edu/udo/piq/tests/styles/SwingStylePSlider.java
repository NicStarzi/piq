package edu.udo.piq.tests.styles;

import java.awt.Graphics2D;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSlider;
import edu.udo.piq.style.MutablePStyleComponent;
import edu.udo.piq.swing.SwingPRenderer;
import edu.udo.piq.tools.ImmutablePSize;

public class SwingStylePSlider extends MutablePStyleComponent implements SwingPStyle {
	
	private static final int KNOB_W = 8;
	private static final int KNOB_H = 16;
	private static final PSize PREF_SIZE = new ImmutablePSize(100, KNOB_H + 2);
	private static final PColor COLOR_FOCUS = PColor.WHITE;
	private static final PColor COLOR_UNFOCUS = COLOR_FOCUS.mult1(1.0f, 1.0f, 1.0f, 0.75f);
	private static final PColor COLOR_DISABLED = PColor.GREY50;
	
	@Override
	public boolean fillsAllPixels(PComponent component) {
		return false;
	}
	
	@Override
	public PSize getPreferredSize(PComponent component) {
		return PREF_SIZE;
	}
	
	@Override
	public void render(PRenderer renderer, PComponent component) {
		PSlider self = (PSlider) component;
		PBounds bnds = self.getBoundsWithoutBorder();
		int w = bnds.getWidth() - KNOB_W;
		int x = bnds.getX() + KNOB_W / 2;
		int centerY = bnds.getCenterY();
		
		SwingPRenderer swingRenderer = (SwingPRenderer) renderer;
		Graphics2D g2d = swingRenderer.getAwtGraphics();
		
		if (!self.isEnabled()) {
			renderer.setColor(COLOR_DISABLED);
		} else if (self.hasFocus()) {
			renderer.setColor(COLOR_FOCUS);
		} else {
			renderer.setColor(COLOR_UNFOCUS);
		}
		int barH = 4;
		int barY = centerY - barH / 2;
		g2d.fillRoundRect(x, barY, w, barH, 4, 2);
		
		int sldX = x + (int) (self.getModel().getValuePercent() * w) - KNOB_W / 2;
		int sldY = centerY - KNOB_H / 2;
		g2d.fillRoundRect(sldX, sldY, KNOB_W, KNOB_H, 4, 6);
	}
	
}