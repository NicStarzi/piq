package edu.udo.piq.style.standard;

import java.util.Objects;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PStyleComponent;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.util.ThrowException;

public class StandardLabelStyle implements PStyleComponent {
	
	public static final String DEFAULT_FONT_NAME = "Monospaced";
	public static final int DEFAULT_FONT_SIZE = 18;
	public static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	public static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	
	protected PColor txtColor = DEFAULT_TEXT_COLOR;
	protected PColor bgColor = null;
	protected String fontName = DEFAULT_FONT_NAME;
	protected int fontSize = DEFAULT_FONT_SIZE;
	protected Style fontStyle = DEFAULT_FONT_STYLE;
	protected PFontResource cachedFont;
	
	public void setFontName(String value) {
		if (!Objects.equals(getFontName(), value)) {
			fontName = value;
			cachedFont = null;
		}
	}
	
	public String getFontName() {
		return fontName;
	}
	
	public void setFontSize(int value) {
		if (getFontSize() != value) {
			fontSize = value;
			cachedFont = null;
		}
	}
	
	public int getFontSize() {
		return fontSize;
	}
	
	public void setFontStyle(Style value) {
		if (getFontStyle() != value) {
			fontStyle = value;
			cachedFont = null;
		}
	}
	
	public Style getFontStyle() {
		return fontStyle;
	}
	
	public void setTextColor(PColor value) {
		txtColor = value;
	}
	
	public PColor getTextColor() {
		return txtColor;
	}
	
	public void setBackgroundColor(PColor value) {
		bgColor = value;
	}
	
	public PColor getBackgroundColor() {
		return bgColor;
	}
	
	@Override
	public PSize getPreferredSize(PComponent component) {
		PLabel lbl = ThrowException.ifTypeCastFails(component, PLabel.class,
				"(component instanceof PLabel) == false");
		String text = lbl.getText();
		if (text == null || text.isEmpty()) {
			return PSize.ZERO_SIZE;
		}
		PFontResource font = getFont(lbl);
		if (font == null) {
			return PSize.ZERO_SIZE;
		}
		return font.getSize(text);
	}
	
	@Override
	public boolean fillsAllPixels(PComponent component) {
		return getBackgroundColor() != null;
	}
	
	@Override
	public void render(PRenderer renderer, PComponent component) {
		PLabel lbl = ThrowException.ifTypeCastFails(component, PLabel.class,
				"(component instanceof PLabel) == false");
		PBounds bnds = lbl.getBoundsWithoutBorder();
		if (getBackgroundColor() != null) {
			renderer.setColor(getBackgroundColor());
			renderer.drawQuad(bnds);
		}
		String text = lbl.getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		PFontResource font = getFont(lbl);
		renderer.setColor(getTextColor());
		renderer.drawString(font, text, bnds.getX(), bnds.getY());
	}
	
	protected PFontResource getFont(PComponent component) {
		PRoot root = component.getRoot();
		if (root == null) {
			return null;
		}
		if (cachedFont != null && root.isFontSupported(cachedFont)) {
			return cachedFont;
		}
		cachedFont = root.fetchFontResource(fontName,
				fontSize, fontStyle);
		return cachedFont;
	}
	
}