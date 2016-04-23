package edu.udo.piq.components.charts;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPBarChartModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.MutablePSize;

public class PBarChart extends AbstractPComponent {
	
	public static final PColor[] DEFAULT_BAR_COLORS = new PColor[] {
		PColor.RED, PColor.BLUE, PColor.GREEN, 
		PColor.YELLOW, PColor.MAGENTA, PColor.TEAL
	};
	public static final PColor DEFAULT_BACKGROUND_COLOR = PColor.WHITE;
	public static final int MIN_BAR_W = 5;
	public static final int DEFAULT_BAR_WIDTH = 20;
	public static final int DEFAULT_BAR_GAP = 4;
	public static final int DEFAULT_BAR_HEIGHT = 200;
	
	protected final PBarChartModelObs modelObs = new PBarChartModelObs() {
		public void onBarValueChanged(PBarChartModel model, int index) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	protected PBarChartModel model = new DefaultPBarChartModel(0);
	protected MutablePSize prefSize = new MutablePSize(0, 200);
	protected PColor bgColor = DEFAULT_BACKGROUND_COLOR;
	protected int prefBarW = DEFAULT_BAR_WIDTH;
	protected int prefBarH = DEFAULT_BAR_HEIGHT;
	protected int prefBarGap = DEFAULT_BAR_GAP;
	
	public void setModel(PBarChartModel model) {
		getModel().removeObs(modelObs);
		this.model = model;
		getModel().addObs(modelObs);
		firePreferredSizeChangedEvent();
		fireReRenderEvent();
	}
	
	public PBarChartModel getModel() {
		return model;
	}
	
	public void setBackgroundColor(PColor color) {
		bgColor = color;
	}
	
	public PColor getBackgroundColor() {
		return bgColor;
	}
	
	public int getPreferredBarWidth() {
		return prefBarW;
	}
	
	public int getPreferredBarHeight() {
		return prefBarH;
	}
	
	public int getPreferredBarGap() {
		return prefBarGap;
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int w = bounds.getWidth();
		int h = bounds.getHeight();
		int fy = bounds.getFinalY();
		
		if (getBackgroundColor() != null) {
			renderer.setColor(getBackgroundColor());
			renderer.drawQuad(bounds);
		}
		
		PBarChartModel model = getModel();
		
		int barW = getPreferredBarWidth();
		int barGap = getPreferredBarGap();
		int barCount = getModel().getBarCount();
		int prefW = barCount * barW + (barCount - 1) * barGap;
		
		if (prefW > w) {
			barW = (w - barCount + 1) / barCount;
			barGap = 1;
			if (barW < MIN_BAR_W) {
				barW = MIN_BAR_W;
			}
		}
		
//		int widthPerBar = w / model.getBarCount();
		int barX = x;
		double maxHeight = getMaxBarHeight();
		for (int i = 0; i < model.getBarCount(); i++) {
			int height = model.getBarValue(i);
			renderer.setColor(DEFAULT_BAR_COLORS[i % DEFAULT_BAR_COLORS.length]);
			
			int barFy = fy;
			int barY = (int) (barFy - (h * (height / maxHeight)));
			
//			int barX = x + barW * i;
			int barFx = barX + barW;
			renderer.drawQuad(barX, barY, barFx, barFy);
			
			barX += barW + barGap;
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return getBackgroundColor() != null;
	}
	
	public PSize getDefaultPreferredSize() {
		int barCount = getModel().getBarCount();
		int prefW = barCount * prefBarW + (barCount - 1) * prefBarGap;
		int prefH = prefBarH;
		prefSize.setWidth(prefW);
		prefSize.setHeight(prefH);
		return prefSize;
//		return new ImmutablePSize(getModel().getBarCount() * 20, Math.max(getMaxBarHeight(), 200));
	}
	
	protected int getMaxBarHeight() {
		int maxHeight = 0;
		PBarChartModel model = getModel();
		for (int i = 0; i < model.getBarCount(); i++) {
			int height = model.getBarValue(i);
			if (height > maxHeight) {
				maxHeight = height;
			}
		}
		return maxHeight;
	}
	
}