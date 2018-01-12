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
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.util.StandardFontKey;
import edu.udo.piq.style.MutablePStyle;
import edu.udo.piq.style.PStyleComponent;
import edu.udo.piq.util.ThrowException;

public class StandardLabelStyle extends MutablePStyle implements PStyleComponent {
	
	public static final Object FONT_ID = new StandardFontKey("Monospaced", 18, Style.PLAIN);
	public static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	
	protected PColor txtColor = DEFAULT_TEXT_COLOR;
	protected PColor bgColor = null;
	protected Object fontId = FONT_ID;
	protected PFontResource cachedFont;
	
	public void setFontId(Object value) {
		if (!Objects.equals(getFontId(), value)) {
			fontId = value;
			cachedFont = null;
			fireSizeChangedEvent();
			fireReRenderEvent();
		}
	}
	
	public Object getFontId() {
		return fontId;
	}
	
	public void setTextColor(PColor value) {
		if (!Objects.equals(getTextColor(), value)) {
			txtColor = value;
			fireReRenderEvent();
		}
	}
	
	public PColor getTextColor() {
		return txtColor;
	}
	
	public void setBackgroundColor(PColor value) {
		if (!Objects.equals(getBackgroundColor(), value)) {
			bgColor = value;
			fireReRenderEvent();
		}
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
		return font.getSize(text, null);
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
		cachedFont = root.fetchFontResource(FONT_ID);
		return cachedFont;
	}
	
}