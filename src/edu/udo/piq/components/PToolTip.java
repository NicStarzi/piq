package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.AbstractPMouseObs;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PRenderUtil;

public class PToolTip extends AbstractPComponent {
	
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	protected static final PColor DEFAULT_BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor DEFAULT_BORDER_COLOR = PColor.BLACK;
	protected static final int DEFAULT_GAP = 2;
	
	private final PMouseObs mouseObs = new AbstractPMouseObs() {
		public void mouseMoved(PMouse mouse) {
		}
	};
	private final PTextModelObs modelObs = new PTextModelObs() {
		public void textChanged(PTextModel model) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private PTextModel model;
	
	public PToolTip() {
		this(new DefaultPTextModel());
	}
	
	public PToolTip(PTextModel model) {
		setModel(model);
	}
	
	public void setModel(PTextModel model) {
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
	
	public PTextModel getModel() {
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
		if (font == null) {
			return;
		}
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(getBackgroundTextColor());
		renderer.drawQuad(x, y, fx, fy);
		
		renderer.setColor(getBorderTextColor());
		PRenderUtil.strokeQuad(renderer, x, y, fx, fy);
		
		renderer.setColor(getDefaultTextColor());
		renderer.drawString(font, text, x + DEFAULT_GAP, y + DEFAULT_GAP);
	}
	
	public PSize getDefaultPreferredSize() {
		String text = getText();
		if (text == null || text.isEmpty()) {
			return PSize.NULL_SIZE;
		}
		PFontResource font = getDefaultFont();
		if (font == null) {
			return PSize.NULL_SIZE;
		}
		PSize textSize = font.getSize(text);
		return new ImmutablePSize(
				textSize.getWidth() + 2 * DEFAULT_GAP, 
				textSize.getHeight() + 2 * DEFAULT_GAP);
	}
	
	protected PMouseObs getMouseObs() {
		return mouseObs;
	}
	
	protected PColor getDefaultTextColor() {
		return DEFAULT_TEXT_COLOR;
	}
	
	protected PColor getBackgroundTextColor() {
		return DEFAULT_BACKGROUND_COLOR;
	}
	
	protected PColor getBorderTextColor() {
		return DEFAULT_BORDER_COLOR;
	}
	
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchFontResource(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
	}
}