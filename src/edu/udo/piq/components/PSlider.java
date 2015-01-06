package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPSliderModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.AbstractPKeyboardObs;
import edu.udo.piq.tools.AbstractPMouseObs;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.PRenderUtil;

public class PSlider extends AbstractPComponent {
	
	protected static final int DEFAULT_SLIDER_WIDTH = 12;
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, DEFAULT_SLIDER_WIDTH);
	
	private final PMouseObs mouseObs = new AbstractPMouseObs() {
		public void mouseMoved(PMouse mouse) {
			if (mouse.isPressed(MouseButton.LEFT) && getModel().isPressed()) {
				updatePosition(mouse);
			}
		}
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && !getModel().isPressed()
					&& PCompUtil.isWithinClippedBounds(PSlider.this, mouse.getX(), mouse.getY())) {
				
				getModel().setPressed(true);
				PCompUtil.takeFocus(PSlider.this);
				updatePosition(mouse);
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && getModel().isPressed()) {
				getModel().setPressed(false);
			}
		}
		public void updatePosition(PMouse mouse) {
			int mx = mouse.getX();
			PBounds bnds = getBounds();
			double valuePercent = (mx - bnds.getX()) / (double) bnds.getWidth();
			getModel().setValuePercent(valuePercent);
		}
	};
	private final PKeyboardObs keyObs = new AbstractPKeyboardObs() {
		public void keyPressed(PKeyboard keyboard, Key key) {
			if (!PCompUtil.hasFocus(PSlider.this)) {
				return;
			}
			if (key == Key.UP || key == Key.RIGHT) {
				getModel().setValue(getModel().getValue() + 1);
			} else if (key == Key.DOWN || key == Key.LEFT) {
				getModel().setValue(getModel().getValue() - 1);
			}
			if (keyboard.isPressed(Key.CTRL)) {
				if (key == Key.Z) {
					if (getModel().getHistory() != null && getModel().getHistory().canUndo()) {
						getModel().getHistory().undo();
					}
				}
				if (key == Key.Y) {
					if (getModel().getHistory() != null && getModel().getHistory().canRedo()) {
						getModel().getHistory().redo();
					}
				}
			}
		}
	};
	protected final PSliderModelObs modelObs = new PSliderModelObs() {
		public void rangeChanged(PSliderModel model) {
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
		
		if (PCompUtil.hasFocus(this)) {
			renderer.setColor(PColor.GREY50);
			PRenderUtil.strokeQuad(renderer, x, y, fx, fy, 1);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public boolean isDefaultOpaque() {
		return false;
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	protected PKeyboardObs getKeyboardObs() {
		return keyObs;
	}
	
	protected PMouseObs getMouseObs() {
		return mouseObs;
	}
	
}