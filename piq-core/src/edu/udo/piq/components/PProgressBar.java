package edu.udo.piq.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PFontResource;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PFontResource.Style;
import edu.udo.piq.components.defaults.DefaultPProgressBarModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PProgressBar extends AbstractPComponent {
	
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, 20);
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	protected static final PColor DEFAULT_TEXT_COLOR = PColor.BLACK;
	protected static final PColor DEFAULT_TEXT_HIGHLIGHT_COLOR = PColor.WHITE;
	protected static final PColor DEFAULT_BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor DEFAULT_BORDER_COLOR = PColor.BLACK;
	protected static final PColor DEFAULT_PROGRESS_COLOR = PColor.BLUE;
	
	private final List<PProgressBarModelObs> modelObsList = new CopyOnWriteArrayList<>();
	private final PProgressBarModelObs modelObs = new PProgressBarModelObs() {
		public void valueChanged(PProgressBarModel model) {
			fireReRenderEvent();
		}
	};
	private PProgressBarModel model;
	
	public PProgressBar() {
		this(new DefaultPProgressBarModel());
	}
	
	public PProgressBar(PProgressBarModel model) {
		super();
		setModel(model);
	}
	
	public void setModel(PProgressBarModel model) {
		PProgressBarModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			for (PProgressBarModelObs obs : modelObsList) {
				oldModel.removeObs(obs);
			}
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			for (PProgressBarModelObs obs : modelObsList) {
				model.addObs(obs);
			}
		}
		fireReRenderEvent();
	}
	
	public PProgressBarModel getModel() {
		return model;
	}
	
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
		PSize textSize = font.getSize(text);
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
		return root.fetchFontResource(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
	}
	
	public void addObs(PProgressBarModelObs obs) {
		modelObsList.add(obs);
	}
	
	public void removeObs(PProgressBarModelObs obs) {
		modelObsList.remove(obs);
	}
	
}