package edu.udo.piq.tests.styles;

import java.awt.Graphics2D;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PCheckBox;
import edu.udo.piq.style.MutablePStyleComponent;
import edu.udo.piq.swing.SwingPRenderer;
import edu.udo.piq.tools.ImmutablePSize;

public class SwingStylePCheckBox extends MutablePStyleComponent {
	
	private static final int BTN_W = 48;
	private static final int BTN_H = 24;
	private static final PSize PREF_SIZE = new ImmutablePSize(BTN_W, BTN_H);
	private static final PColor COLOR_BTN = PColor.WHITE;
	private static final PColor COLOR_CHECKED = PColor.GREEN.mult1(1.0, 1.0, 1.0, 0.75);
	private static final PColor COLOR_UNCHECKED = PColor.RED.mult1(1.0, 1.0, 1.0, 0.75);
	
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
		PCheckBox self = (PCheckBox) component;
		PBounds bnds = self.getBoundsWithoutBorder();
		int x = bnds.getX();
		int y = bnds.getY();
		int w = bnds.getWidth();
		int h = bnds.getHeight();
		
		SwingPRenderer swingRenderer = (SwingPRenderer) renderer;
		Graphics2D g2d = swingRenderer.getAwtGraphics();
		
		renderer.setColor(COLOR_BTN);
		g2d.fillRoundRect(x, y, w, h, 4, 4);
		int gap = 2;
		int boxX;
		int boxY = y + gap;
		int boxW = (w - gap - gap) / 2;
		int boxH = (h - gap - gap);
		if (self.isChecked()) {
			boxX = x + gap;
			renderer.setColor(COLOR_CHECKED);
		} else {
			boxX = x + w - gap - boxW;
			renderer.setColor(COLOR_UNCHECKED);
		}
		g2d.fillRoundRect(boxX, boxY, boxW, boxH, 4, 4);
	}
	
}