package edu.udo.piq.components.textbased;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.components.PSingleValueModel;
import edu.udo.piq.components.PSingleValueModelObs;
import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.components.util.SymbolicFontKey;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;

public class PLabel extends AbstractPComponent {
	
	public static final Object FONT_ID = new SymbolicFontKey(PLabel.class);
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	
	protected final ObserverList<PSingleValueModelObs> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final PSingleValueModelObs modelObs = this::onModelValueChanged;
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
	
	protected void onModelValueChanged(PSingleValueModel model, Object oldValue, Object newValue) {
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public void setModelValue(Object value) {
		if (getModel() == null) {
			return;
		}
		getModel().setValue(value);
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
		PBounds bounds = getBoundsWithoutBorder();
		
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
		cachedFont = root.fetchFontResource(FONT_ID);
		return cachedFont;
	}
	
}