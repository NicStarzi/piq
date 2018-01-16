package edu.udo.piq.tests.styles;

import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.components.PRadioButtonTuple;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.util.StandardFontKey;
import edu.udo.piq.style.MutablePStyleComponent;
import edu.udo.piq.tools.MutablePSize;

public class SwingStylePLabel extends MutablePStyleComponent implements SwingPStyle {
	
	private static final PColor COLOR_PANEL = PColor.WHITE;
	private static final PColor COLOR_OTHER = PColor.BLACK;
	private static final Object FONT_KEY = new StandardFontKey(Font.SANS_SERIF, 14, Style.PLAIN);
	private static final Set<Class<? extends PComponent>> WHITE_COLOR_COMPS = new HashSet<>();
	static {
		WHITE_COLOR_COMPS.add(PPanel.class);
		WHITE_COLOR_COMPS.add(PCheckBoxTuple.class);
		WHITE_COLOR_COMPS.add(PRadioButtonTuple.class);
	}
	
	private final MutablePSize tmpSize = new MutablePSize();
	private PFontResource cachedFontRes = null;
	
	@Override
	public boolean fillsAllPixels(PComponent component) {
		return false;
	}
	
	@Override
	public PSize getPreferredSize(PComponent component) {
		PLabel self = (PLabel) component;
		String text = self.getText();
		if (text == null || text.isEmpty()) {
			return PSize.ZERO_SIZE;
		}
		if (cachedFontRes == null) {
			cachedFontRes = self.getRoot().fetchFontResource(FONT_KEY);
			if (cachedFontRes == null) {
				return PSize.ZERO_SIZE;
			}
		}
		return cachedFontRes.getSize(text, tmpSize);
	}
	
	@Override
	public void render(PRenderer renderer, PComponent component) {
		PLabel self = (PLabel) component;
		String text = self.getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		if (cachedFontRes == null) {
			cachedFontRes = self.getRoot().fetchFontResource(FONT_KEY);
			if (cachedFontRes == null) {
				return;
			}
		}
		PBounds bounds = self.getBoundsWithoutBorder();
		
		if (WHITE_COLOR_COMPS.contains(self.getParent().getClass())) {
			renderer.setColor(COLOR_PANEL);
		} else {
			renderer.setColor(COLOR_OTHER);
		}
		renderer.drawString(cachedFontRes, text, bounds.getX(), bounds.getY());
	}
	
}