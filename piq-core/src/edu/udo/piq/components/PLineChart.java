package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPLineChartModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PLineChart extends AbstractPComponent {
	
	protected static final PColor BACKGROUND_COLOR = PColor.GREY50;
	protected static final PColor[] LINE_COLORS = new PColor[] {
		PColor.YELLOW, 
		PColor.RED, 
		PColor.GREEN, 
		PColor.MAGENTA, 
		PColor.TEAL, 
		PColor.BLUE, 
	};
	
	private final PLineChartModelObs modelObs = new PLineChartModelObs() {
		public void dataPointAdded(PLineChartModel model, int index) {
			if (maxDataIndex < 0 || model.getDataPoint(maxDataIndex) < model.getDataPoint(index)) {
				maxDataIndex = index;
			}
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
		public void dataPointRemoved(PLineChartModel model, int index) {
			if (maxDataIndex == index) {
				maxDataIndex = getHighestDataIndex();
			}
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
		public void dataPointChanged(PLineChartModel model, int index) {
			if (maxDataIndex == index) {
				maxDataIndex = getHighestDataIndex();
			} else if (maxDataIndex < 0 || model.getDataPoint(maxDataIndex) < model.getDataPoint(index)) {
				maxDataIndex = index;
			}
			firePreferredSizeChangedEvent();
			fireReRenderEvent();
		}
	};
	private PLineChartModel model;
	private int maxDataIndex = -1;
	
	public PLineChart() {
		super();
		setModel(new DefaultPLineChartModel());
	}
	
	public int getHighestDataPoint() {
		if (getModel() == null || maxDataIndex < 0) {
			return 0;
		}
		return getModel().getDataPoint(maxDataIndex);
	}
	
	public void setModel(PLineChartModel model) {
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
	
	public PLineChartModel getModel() {
		return model;
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		renderer.setColor(getDefaultBackgroundColor());
		renderer.drawQuad(x, y, fx, fy);
		
		PLineChartModel model = getModel();
		if (model != null && model.getDataCount() > 0) {
			int scaleX = getDefaultScaleX();
			int scaleY = getDefaultScaleY();
			int lineSize = getDefaultLineSize();
			
			renderer.setColor(getDefaultLineColor(0));
			int dx1 = x + 0 * scaleX;
			int dy1 = fy - model.getDataPoint(0) * scaleY;
			for (int i = 1; i < model.getDataCount(); i++) {
				int dx2 = x + i * scaleX;
				int dy2 = fy - model.getDataPoint(i) * scaleY;
				
				renderer.drawLine(dx1, dy1, dx2, dy2, lineSize);
				
				dx1 = dx2;
				dy1 = dy2;
			}
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public PSize getDefaultPreferredSize() {
		if (getModel() == null || getModel().getDataCount() == 0) {
			return PSize.NULL_SIZE;
		}
		int scaleX = getDefaultScaleX();
		int scaleY = getDefaultScaleY();
		int prefW = getModel().getDataCount() * scaleX;
		int prefH = getModel().getDataPoint(maxDataIndex) * scaleY;
		return new ImmutablePSize(prefW, prefH);
	}
	
	protected PColor getDefaultBackgroundColor() {
		return BACKGROUND_COLOR;
	}
	
	protected PColor getDefaultLineColor(int index) {
		return LINE_COLORS[index % LINE_COLORS.length];
	}
	
	protected int getDefaultScaleX() {
		return 10;
	}
	
	protected int getDefaultScaleY() {
		return 10;
	}
	
	protected int getDefaultLineSize() {
		return 1;
	}
	
	private int getHighestDataIndex() {
		int max = Integer.MIN_VALUE;
		int maxIndex = -1;
		for (int i = 0; i < getModel().getDataCount(); i++) {
			int val = model.getDataPoint(i);
			if (val > max) {
				maxIndex = i;
				max = val;
			}
		}
		return maxIndex;
	}
	
}