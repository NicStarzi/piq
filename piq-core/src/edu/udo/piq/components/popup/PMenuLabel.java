package edu.udo.piq.components.popup;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.tools.AbstractPTextComponent;

public class PMenuLabel extends PLabel {
	
	public static final PColor DEFAULT_HIGHLIGHT_TEXT_COLOR = 
			AbstractPTextComponent.DEFAULT_TEXT_SELECTED_COLOR;
	
	protected boolean enabled = true;
	
	public void setEnabled(boolean value) {
		if (enabled != value) {
			enabled = value;
			fireReRenderEvent();
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isHighlighted() {
		AbstractPMenuItem menuItem = getFirstAncestorOfType(AbstractPMenuItem.class);
		return menuItem != null && menuItem.isHighlighted();
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		String text = getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		PFontResource font = getDefaultFont();
		if (font == null) {
			return;
		}
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		if (isEnabled()) {
			if (isHighlighted()) {
				renderer.setColor(DEFAULT_HIGHLIGHT_TEXT_COLOR);
			} else {
				renderer.setColor(getDefaultTextColor());
			}
			renderer.drawString(font, text, x, y);
		} else {
			if (!isHighlighted()) {
				renderer.setColor(PColor.WHITE);
				renderer.drawString(font, text, x + 1, y + 1);
			}
			renderer.setColor(PColor.GREY50);
			renderer.drawString(font, text, x, y);
		}
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
}