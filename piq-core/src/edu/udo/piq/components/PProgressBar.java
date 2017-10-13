package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPProgressBarModel;
import edu.udo.piq.components.util.SymbolicFontKey;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PProgressBar extends AbstractPComponent {
	
	public static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, 20);
	public static final Object FONT_ID = new SymbolicFontKey(PProgressBar.class);
	public static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	public static final PColor DEFAULT_TEXT_HIGHLIGHT_COLOR = PColor.WHITE;
	public static final PColor DEFAULT_BACKGROUND_COLOR = PColor.WHITE;
	public static final PColor DEFAULT_BORDER_COLOR = PColor.BLACK;
	public static final PColor DEFAULT_PROGRESS_COLOR = PColor.BLUE;
	
	protected final ObserverList<PProgressBarModelObs> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final PProgressBarModelObs modelObs = model -> PProgressBar.this.onModelValueChanged();
	protected PProgressBarModel model;
	
	public PProgressBar() {
		super();
		setModel(PModelFactory.createModelFor(this, DefaultPProgressBarModel::new, PProgressBarModel.class));
	}
	
	public PProgressBar(PProgressBarModel model) {
		super();
		setModel(model);
	}
	
	public void setModel(PProgressBarModel model) {
		PProgressBarModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			modelObsList.forEach(obs -> oldModel.removeObs(obs));
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			modelObsList.forEach(obs -> model.addObs(obs));
		}
		fireReRenderEvent();
	}
	
	public PProgressBarModel getModel() {
		return model;
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int w = bounds.getWidth();
		int h = bounds.getHeight();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(getDefaultBorderColor());
		renderer.strokeQuad(x, y, fx, fy);
		
		double value = getModel().getValue();
		double maxValue = getModel().getMaxValue();
		double valuePercent = value / maxValue;
		int barFx = x + 1 + (int) ((fx - x - 2) * valuePercent);
		
		if (value > 0) {
			renderer.setColor(getDefaultProgressColor());
			renderer.drawQuad(x + 1, y + 1, barFx, fy - 1);
		}
		if (value < maxValue) {
			renderer.setColor(getDefaultBackgroundColor());
			renderer.drawQuad(barFx + 1, y + 1, fx - 1, fy - 1);
		}
		
		PFontResource font = getDefaultFont();
		if (font == null) {
			return;
		}
		String text = (int) (valuePercent * 100) + "%";
		PSize textSize = font.getSize(text, null);
		int textW = textSize.getWidth();
		int textH = textSize.getHeight();
		int textX = x + w / 2 - textW / 2;
		int textY = y + h / 2 - textH / 2;
		int textFx = textX + textW;
		
		if (textFx < barFx) {
			renderer.setColor(getDefaultTextHighlightColor());
			renderer.drawString(font, text, textX, textY);
		} else if (textX > barFx) {
			renderer.setColor(getDefaultTextColor());
			renderer.drawString(font, text, textX, textY);
		} else {
			renderer.setClipBounds(x, y, barFx, fy);
			renderer.setColor(getDefaultTextHighlightColor());
			renderer.drawString(font, text, textX, textY);
			
			renderer.setClipBounds(barFx, y, fx, fy);
			renderer.setColor(getDefaultTextColor());
			renderer.drawString(font, text, textX, textY);
		}
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	protected PColor getDefaultTextColor() {
		return DEFAULT_TEXT_COLOR;
	}
	
	protected PColor getDefaultTextHighlightColor() {
		return DEFAULT_TEXT_HIGHLIGHT_COLOR;
	}
	
	protected PColor getDefaultBackgroundColor() {
		return DEFAULT_BACKGROUND_COLOR;
	}
	
	protected PColor getDefaultBorderColor() {
		return DEFAULT_BORDER_COLOR;
	}
	
	protected PColor getDefaultProgressColor() {
		return DEFAULT_PROGRESS_COLOR;
	}
	
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchFontResource(FONT_ID);
	}
	
	public void addObs(PProgressBarModelObs obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PProgressBarModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	protected void onModelValueChanged() {
		fireReRenderEvent();
	}
	
}