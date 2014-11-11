package edu.udo.piq.tools;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PLabel;

public class PLabelDesign implements PDesign {
	
	protected PFontResource font;
	protected PColor textColor;
	
	public PLabelDesign() {
		textColor = PColor.BLACK;
	}
	
	public void setFont(PFontResource font) {
		this.font = font;
	}
	
	public PFontResource getFont() {
		return font;
	}
	
	public void setTextColor(PColor textColor) {
		this.textColor = textColor;
	}
	
	public PColor getTextColor() {
		return textColor;
	}
	
	public PSize getPreferredSize(PComponent component) throws NullPointerException, IllegalArgumentException {
		PLabel label = (PLabel) component;
		String text = label.getText();
		if (text == null || text.isEmpty()) {
			return PSize.NULL_SIZE;
		}
		return font.getSize(text);
	}
	
	public void render(PRenderer renderer, PComponent component) throws NullPointerException, IllegalArgumentException {
		PLabel label = (PLabel) component;
		String text = label.getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		PBounds bounds = label.getBounds();
		
		renderer.setColor(textColor);
		renderer.drawString(font, text, bounds.getX(), bounds.getY());
	}
	
}