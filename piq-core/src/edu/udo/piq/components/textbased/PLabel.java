package edu.udo.piq.components.textbased;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PLabel extends AbstractPComponent {
	
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	
	protected final ObserverList<PTextModelObs> modelObsList
		= PCompUtil.createDefaultObserverList();
	protected final PTextModelObs modelObs = model -> {
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	};
	protected PFontResource cachedFont;
	protected PTextModel model;
	
	public PLabel() {
		super();
		setModel(PModelFactory.createModelFor(this, DefaultPTextModel::new, PTextModel.class));
	}
	
	public PLabel(PTextModel model) {
		super();
		setModel(model);
	}
	
	public PLabel(Object defaultModelValue) {
		this();
		getModel().setValue(defaultModelValue);
	}
	
	public void setModel(PTextModel model) {
		PTextModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			modelObsList.fireEvent(obs -> oldModel.removeObs(obs));
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			modelObsList.fireEvent(obs -> model.addObs(obs));
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
		Object text = getModel().getText();
		if (text == null) {
			return "";
		}
		return text.toString();
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
		
		renderer.setColor(getDefaultTextColor());
		renderer.drawString(font, text, bounds.getX(), bounds.getY());
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		String text = getText();
		if (text == null || text.isEmpty()) {
			return PSize.ZERO_SIZE;
		}
		PFontResource font = getDefaultFont();
		if (font == null) {
			return PSize.ZERO_SIZE;
		}
		return font.getSize(text, prefSize);
	}
	
	protected PColor getDefaultTextColor() {
		return DEFAULT_TEXT_COLOR;
	}
	
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		if (cachedFont != null && root.isFontSupported(cachedFont)) {
			return cachedFont;
		}
		cachedFont = root.fetchFontResource(DEFAULT_FONT_NAME,
				DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
		return cachedFont;
	}
	
}