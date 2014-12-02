package edu.udo.piq.basicdesign;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRoot;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PLabel;

public class BasicPLabelDesign implements PDesign {
	
	protected PColor textColor = PColor.BLACK;
	protected String fontName = "Sylfaen";
	protected Style fontStyle = Style.ITALIC;
	protected int fontSize = 14;
	
	public void setFontName(String value) {
		if (value == null) {
			throw new NullPointerException();
		}
		fontName = value;
	}
	
	public String getFontName() {
		return fontName;
	}
	
	public void setFontStyle(Style value) {
		if (value == null) {
			throw new NullPointerException();
		}
		fontStyle = value;
	}
	
	public Style getFontStyle() {
		return fontStyle;
	}
	
	public void setFontSize(int value) {
		fontSize = value;
	}
	
	public int getFontSize() {
		return fontSize;
	}
	
	public void setTextColor(PColor textColor) {
		this.textColor = textColor;
	}
	
	public PColor getTextColor() {
		return textColor;
	}
	
	protected PFontResource getFont(PComponent component) {
		PRoot root = component.getRoot();
		return root.fetchFontResource(fontName, fontSize, fontStyle);
	}
	
	public PSize getPreferredSize(PComponent component) throws NullPointerException, IllegalArgumentException {
		PLabel label = (PLabel) component;
		String text = label.getText();
		if (text == null || text.isEmpty()) {
			return PSize.NULL_SIZE;
		}
		return getFont(component).getSize(text);
	}
	
	public void render(PRenderer renderer, PComponent component) throws NullPointerException, IllegalArgumentException {
		PLabel label = (PLabel) component;
		String text = label.getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		PBounds bounds = label.getBounds();
		
		renderer.setColor(getTextColor());
		renderer.drawString(getFont(component), text, bounds.getX(), bounds.getY());
	}
	
	public boolean isOpaque(PComponent component) {
		return false;
	}
	
}