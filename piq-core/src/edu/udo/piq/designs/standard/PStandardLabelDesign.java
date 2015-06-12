package edu.udo.piq.designs.standard;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PLabel;

public class PStandardLabelDesign extends PStandardDesign implements PDesign {
	
	private String fontName = "Arial";
	private Style fontStyle = Style.PLAIN;
	private int fontSize = 12;
	private PColor fontColor = PColor.BLACK;
	private PColor backgroundColor = null;
	
	private PFontResource cachedFontRes;
	
	public PStandardLabelDesign(PStandardDesignSheet designSheet) {
		super(designSheet);
	}
	
	public boolean includeComponent(PComponent component) {
		return component.getClass() == PLabel.class;
	}
	
	public void render(PRenderer renderer, PComponent component) {
		PLabel lbl = (PLabel) component;
		String text = lbl.getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		if (cachedFontRes == null) {
			cachedFontRes = lbl.getRoot().
					fetchFontResource(fontName, fontSize, fontStyle);
		}
		if (cachedFontRes == null) {
			return;
		}
		refreshAsNeeded(lbl);
		
		PBounds bounds = lbl.getBounds();
		
		if (backgroundColor != null) {
			renderer.setColor(backgroundColor);
			renderer.drawQuad(bounds);
		}
		renderer.setColor(fontColor);
		renderer.drawString(cachedFontRes, text, bounds.getX(), bounds.getY());
	}
	
	public PSize getPreferredSize(PComponent component) {
		PLabel lbl = (PLabel) component;
		String text = lbl.getText();
		if (text == null || text.isEmpty()) {
			return PSize.NULL_SIZE;
		}
		if (cachedFontRes == null) {
			cachedFontRes = lbl.getRoot().
					fetchFontResource(fontName, fontSize, fontStyle);
		}
		if (cachedFontRes == null) {
			return PSize.NULL_SIZE;
		}
		return cachedFontRes.getSize(text);
	}
	
	public String getFontName() {
		return fontName;
	}
	
	public void setFontName(String value) {
		if (value == null) {
			throw new NullPointerException("fontName=null");
		}
		fontName = value;
		cachedFontRes = null;
		setReLayoutNeeded();
	}
	
	public Style getFontStyle() {
		return fontStyle;
	}
	
	public void setFontStyle(Style value) {
		if (value == null) {
			throw new NullPointerException("fontStyle=null");
		}
		fontStyle = value;
		cachedFontRes = null;
		setReLayoutNeeded();
	}
	
	public int getFontSize() {
		return fontSize;
	}
	
	public void setFontSize(int value) {
		if (value < 1) {
			throw new IllegalArgumentException("fontSize="+value);
		}
		fontSize = value;
		cachedFontRes = null;
		setReLayoutNeeded();
	}
	
	public PColor getFontColor() {
		return fontColor;
	}
	
	public void setFontColor(PColor value) {
		if (value == null) {
			throw new NullPointerException("fontColor=null");
		}
		fontColor = value;
		setReRenderNeeded();
	}
	
	public PColor getBackgroundColor() {
		return backgroundColor;
	}
	
	public void setBackgroundColor(PColor value) {
		backgroundColor = value;
		setReRenderNeeded();
	}
}