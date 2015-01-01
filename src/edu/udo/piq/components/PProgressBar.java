package edu.udo.piq.components;

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
import edu.udo.piq.util.PRenderUtil;

public class PProgressBar extends AbstractPComponent {
	
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, 20);
	protected static final String DEFAULT_FONT_NAME = "Arial";
	protected static final int DEFAULT_FONT_SIZE = 14;
	protected static final Style DEFAULT_FONT_STYLE = Style.PLAIN;
	
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
		setModel(model);
	}
	
	public void setModel(PProgressBarModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
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
		
		renderer.setColor(PColor.BLACK);
		PRenderUtil.strokeQuad(renderer, x, y, fx, fy);
		
		double value = getModel().getValue();
		double maxValue = getModel().getMaxValue();
		double valuePercent = value / maxValue;
		int barFx = x + 1 + (int) ((fx - x - 2) * valuePercent);
		
		if (value > 0) {
			renderer.setColor(PColor.BLUE);
			renderer.drawQuad(x + 1, y + 1, barFx, fy - 1);
		}
		if (value < maxValue) {
			renderer.setColor(PColor.WHITE);
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
			renderer.setColor(PColor.WHITE);
			renderer.drawString(font, text, textX, textY);
		} else if (textX > barFx) {
			renderer.setColor(PColor.BLACK);
			renderer.drawString(font, text, textX, textY);
		} else {
			renderer.setClipBounds(x, y, barFx, fy);
			renderer.setColor(PColor.WHITE);
			renderer.drawString(font, text, textX, textY);
			
			renderer.setClipBounds(barFx, y, fx, fy);
			renderer.setColor(PColor.BLACK);
			renderer.drawString(font, text, textX, textY);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	protected PFontResource getDefaultFont() {
		PRoot root = getRoot();
		if (root == null) {
			return null;
		}
		return root.fetchFontResource(DEFAULT_FONT_NAME, DEFAULT_FONT_SIZE, DEFAULT_FONT_STYLE);
	}
	
}