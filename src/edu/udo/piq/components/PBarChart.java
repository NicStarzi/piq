package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPBarChartModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PBarChart extends AbstractPComponent {
	
	private static final PColor[] DEFAULT_BAR_COLORS = new PColor[] {
		PColor.RED, PColor.BLUE, PColor.GREEN, 
		PColor.YELLOW, PColor.MAGENTA, PColor.TEAL
	};
	
	private final PBarChartModelObs modelObs = new PBarChartModelObs() {
		public void barValueChanged(int index) {
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private PBarChartModel model = new DefaultPBarChartModel(0);
	
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
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int w = bounds.getWidth();
		int h = bounds.getHeight();
		int fy = bounds.getFinalY();
		
		PBarChartModel model = getModel();
		
		int widthPerBar = w / model.getBarCount();
		double maxHeight = getMaxBarHeight();
		for (int i = 0; i < model.getBarCount(); i++) {
			int height = model.getBarValue(i);
			renderer.setColor(DEFAULT_BAR_COLORS[i % DEFAULT_BAR_COLORS.length]);
			
			int barFy = fy;
			int barY = (int) (barFy - (h * (height / maxHeight)));
			
			int barX = x + widthPerBar * i;
			int barFx = barX + widthPerBar;
			renderer.drawQuad(barX, barY, barFx, barFy);
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public PSize getDefaultPreferredSize() {
		return new ImmutablePSize(getModel().getBarCount() * 20, Math.max(getMaxBarHeight(), 200));
	}
	
	private int getMaxBarHeight() {
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