package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPScrollBarModel;
import edu.udo.piq.layouts.PScrollPanelLayout;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PRenderUtil;

public class PScrollBar extends AbstractPComponent {
	
	private static final int MIN_SLIDER_WIDTH = PScrollPanelLayout.SCROLL_BAR_SIZE;
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(MIN_SLIDER_WIDTH, MIN_SLIDER_WIDTH);
	
	private final PMouseObs mouseObs = new PMouseObs() {
		public void mouseMoved(PMouse mouse) {
			if (pressed && mouse.isPressed(MouseButton.LEFT)) {
				moveTo(mouse.getX());
			}
		}
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && isMouseWithinClippedBounds()) 
			{
				pressed = true;
				moveTo(mouse.getX());
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			if (pressed && btn == MouseButton.LEFT) {
				pressed = false;
			}
		}
		private void moveTo(int x) {
			PBounds bnds = getBounds();
			int scroll = x - bnds.getX();
			model.setScroll(scroll);
		}
	};
	private final PScrollBarModelObs modelObs = new PScrollBarModelObs() {
		public void sizeChanged(PScrollBarModel model) {
			fireReRenderEvent();
		}
		public void scrollChanged(PScrollBarModel model) {
			fireReRenderEvent();
		}
	};
	private PScrollBarModel model;
	private boolean pressed;
	
	public PScrollBar() {
		super();
		setModel(new DefaultPScrollBarModel());
		
		addObs(new PComponentObs() {
			public void wasRemoved(PComponent component) {
				pressed = false;
				getModel().setContentSize(0);
				getModel().setViewportSize(0);
				getModel().setScroll(0);
			}
		});
		addObs(mouseObs);
	}
	
	public void setModel(PScrollBarModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		fireReRenderEvent();
	}
	
	public PScrollBarModel getModel() {
		return model;
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		PScrollBarModel model = getModel();
		int x = bnds.getX();
		int y = bnds.getY();
		int w = bnds.getWidth();
		int h = bnds.getHeight();
		int fx = x + w;
		int fy = y + h;
		
		// Draw bar
		int barW = fx - x;
		defaultRenderBackground(renderer, x, y, fx, fy);
		
		// Draw slider
		double viewportToContentRatio = (double) model.getViewportSize() / (double) model.getContentSize();
		int sliderW = (int) (barW * viewportToContentRatio);
		if (sliderW < MIN_SLIDER_WIDTH) {
			sliderW = MIN_SLIDER_WIDTH;
		}
		double scrlPercent = model.getScroll() / (double) model.getMaxScroll();
		double size = (barW - sliderW) * scrlPercent;
		
		int scrollX = (int) size;
		int sliderX = x + scrollX;
		int sliderY = y;
		int sliderFx = sliderX + sliderW;
		int sliderFy = fy;
		
		defaultRenderButtonOrSlider(renderer, sliderX, sliderY, sliderFx, sliderFy);
	}
	
	private void defaultRenderBackground(PRenderer renderer, int x, int y, int fx, int fy) {
		renderer.setColor(PColor.GREY875);
		renderer.drawQuad(x, y, fx, fy);
	}
	
	private void defaultRenderButtonOrSlider(PRenderer renderer, int x, int y, int fx, int fy) {
		renderer.setColor(PColor.BLACK);
		PRenderUtil.strokeBottom(renderer, x, y, fx, fy);
		PRenderUtil.strokeRight(renderer, x, y, fx, fy);
		renderer.setColor(PColor.WHITE);
		PRenderUtil.strokeTop(renderer, x, y, fx, fy);
		PRenderUtil.strokeLeft(renderer, x, y, fx, fy);
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
	}
	
	private void defaultRenderTriangle(PRenderer renderer, int x, int y, int fx, int fy, int dir) {
		int x1 = x;
		int x2 = x;
		int x3 = fx;
		int y1 = y;
		int y2 = fy;
		int y3 = y + (fy - y) / 2;
		
		renderer.setColor(PColor.BLACK);
		renderer.drawTriangle(x1, y1, x2, y2, x3, y3);
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
}