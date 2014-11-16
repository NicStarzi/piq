package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PCompUtil;

public class PScrollBarHorizontal extends AbstractPComponent {
	
	private static final int MIN_SLIDER_WIDTH = 20;
	
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
	
	public PScrollBarHorizontal() {
		super();
		setModel(model);
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
	
	public void onUpdate() {
		PMouse mouse = PCompUtil.getMouseOf(this);
		if (mouse == null) {
			pressed = false;
			return;
		}
		PComponent mouseOwner = mouse.getOwner();
		if (mouseOwner != null && mouseOwner != this) {
			pressed = false;
			return;
		}
		
		if (pressed) {
			if (!mouse.isPressed(MouseButton.LEFT)) {
				pressed = false;
				mouse.setOwner(null);
			}
		} else {
			if (mouse.isTriggered(MouseButton.LEFT) 
					&& PCompUtil.isMouseContained(this, PCompUtil.getClippedBoundsOf(this))) {
				pressed = true;
				mouse.setOwner(this);
			}
		}
		if (pressed && mouse.isPressed(MouseButton.LEFT)) {
			int mx = mouse.getX();
			PBounds bnds = getBounds();
			int scroll = mx - bnds.getX();
			model.setScroll(scroll);
		}
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
		int barX = x;
		int barY = y;
		int barFx = fx;
		int barFy = fy;
		int barW = barFx - barX;
		
		renderer.setColor(PColor.GREY875);
		renderer.drawQuad(barX, barY, barFx, barFy);
		
		// Draw slider
		double viewportToContentRatio = (double) model.getViewportSize() / (double) model.getContentSize();
		int sliderW = (int) (barW * viewportToContentRatio);
		if (sliderW < MIN_SLIDER_WIDTH) {
			sliderW = MIN_SLIDER_WIDTH;
		}
		int sliderX = x + model.getScroll();
		int sliderY = y;
		int sliderFx = sliderX + sliderW;
		int sliderFy = fy;
		
		renderer.setColor(PColor.BLACK);
		renderer.drawQuad(sliderX + 0, sliderY + 0, sliderFx - 0, sliderFy - 0);
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(sliderX + 0, sliderY + 0, sliderFx - 1, sliderFy - 1);
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(sliderX + 1, sliderY + 1, sliderFx - 1, sliderFy - 1);
	}
	
	public PSize getDefaultPreferredSize() {
		return new ImmutablePSize(100, 20);
	}
	
}