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
	protected final PTextModelObs modelObs = new PTextModelObs() {
		public void onTextChanged(PTextModel model) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	protected PTextModel model;
	
	public PLabel() {
		super();
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PTextModel defaultModel = new DefaultPTextModel();
		if (modelFac != null) {
			defaultModel = (PTextModel) modelFac.getModelFor(this, defaultModel);
		}
		
		setModel(defaultModel);
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
			for (PTextModelObs obs : modelObsList) {
				oldModel.removeObs(obs);
			}
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			for (PTextModelObs obs : modelObsList) {
				model.addObs(obs);
			}
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
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public PSize getDefaultPreferredSize() {
		String text = getText();
		if (text == null || text.isEmpty()) {
			return PSize.ZERO_SIZE;
		}
		PFontResource font = getDefaultFont();
		if (font == null) {
			return PSize.ZERO_SIZE;
		}
		return font.getSize(text);
	}
	
	protected PColor getDefaultTextColor() {
		return DEFAULT_TEXT_COLOR;
	}
	
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchFontResource(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
	}
	
}