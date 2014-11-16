package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPSliderModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PCompUtil;

public class PSlider extends AbstractPComponent {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, 20);
	private static final int DEFAULT_SLIDER_WIDTH = 12;
	
	protected final PSliderModelObs modelObs = new PSliderModelObs() {
		public void boundsChanged(PSliderModel model) {
			fireReRenderEvent();
		}
		public void valueChanged(PSliderModel model) {
			fireReRenderEvent();
		}
	};
	protected PSliderModel model = new DefaultPSliderModel();
	
	public PSlider() {
		setModel(model);
	}
	
	protected void onUpdate() {
		PMouse mouse = PCompUtil.getMouseOf(this);
		if (mouse == null) {
			model.setPressed(false);
			return;
		}
		PComponent mouseOwner = mouse.getOwner();
		if (mouseOwner != null && mouseOwner != this) {
			model.setPressed(false);
			return;
		}
		
		if (model.isPressed()) {
			if (!mouse.isPressed(MouseButton.LEFT)) {
				model.setPressed(false);
				mouse.setOwner(null);
			}
		} else {
			if (mouse.isTriggered(MouseButton.LEFT) 
					&& PCompUtil.isMouseContained(this, PCompUtil.getClippedBoundsOf(this))) {
				model.setPressed(true);
				mouse.setOwner(this);
			}
		}
		if (model.isPressed() && mouse.isPressed(MouseButton.LEFT)) {
			int mx = mouse.getX();
			PBounds bnds = getBounds();
			double valuePercent = (mx - bnds.getX()) / (double) bnds.getWidth();
			model.setValuePercent(valuePercent);
		}
	}
	
	public void setModel(PSliderModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		fireReRenderEvent();
	}
	
	public PSliderModel getModel() {
		return model;
	}
	
	public boolean isPressed() {
		return getModel().isPressed();
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		int centerY = y + bnds.getHeight() / 2;
		
		renderer.setColor(PColor.BLACK);
		renderer.drawQuad(x, centerY - 1, fx, centerY + 1);
		
		int sliderX = x + (int) (getModel().getValuePercent() * bnds.getWidth()) - DEFAULT_SLIDER_WIDTH / 2;
		int sliderFx = sliderX + DEFAULT_SLIDER_WIDTH;
		
		renderer.setColor(PColor.GREY50);
		renderer.drawQuad(sliderX, y, sliderFx, fy);
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
}