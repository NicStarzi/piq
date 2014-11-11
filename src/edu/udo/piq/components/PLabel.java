package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.components.defaults.DefaultPLabelModel;
import edu.udo.piq.tools.AbstractPComponent;

public class PLabel extends AbstractPComponent {
	
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	
	private final PLabelModelObs modelObs = new PLabelModelObs() {
		public void textChanged(PLabelModel model) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private PLabelModel model = new DefaultPLabelModel();
	
	public PLabel() {
		setModel(model);
	}
	
	public void setModel(PLabelModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PLabelModel getModel() {
		return model;
	}
	
	public String getText() {
		if (getModel() == null) {
			return "";
		}
		Object text = model.getText();
		if (text == null) {
			return "";
		}
		return text.toString();
	}
	
	public void defaultRender(PRenderer renderer) {
		String text = getText();
		if (text == null || text.isEmpty()) {
			return;
		}
		PFontResource font = getDefaultFont();
		PBounds bounds = getBounds();
		
		renderer.setColor(getDefaultTextColor());
		renderer.drawString(font, text, bounds.getX(), bounds.getY());
	}
	
	public PSize getDefaultPreferredSize() {
		String text = getText();
		if (text == null || text.isEmpty()) {
			return PSize.NULL_SIZE;
		}
		PFontResource font = getDefaultFont();
		return font.getSize(text);
	}
	
	protected PColor getDefaultTextColor() {
		return DEFAULT_TEXT_COLOR;
	}
	
	protected PFontResource getDefaultFont() {
		return getRoot().fetchFontResource(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
	}
	
}