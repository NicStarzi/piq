package edu.udo.piq.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
import edu.udo.piq.tools.ImmutablePSize;

public class PSlider extends AbstractPComponent {
	
	protected static final int DEFAULT_SLIDER_WIDTH = 8;
	protected static final int DEFAULT_SLIDER_HEIGHT = 12;
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, DEFAULT_SLIDER_HEIGHT + 2);
	
	private final PMouseObs mouseObs = new PMouseObs() {
		public void mouseMoved(PMouse mouse) {
			if (mouse.isPressed(MouseButton.LEFT) && getModel().isPressed()) {
				updatePosition(mouse);
			}
		}
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && !getModel().isPressed()
					&& isMouseWithinClippedBounds()) {
				
				getModel().setPressed(true);
				takeFocus();
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
	private final PKeyboardObs keyObs = new PKeyboardObs() {
		public void keyPressed(PKeyboard keyboard, Key key) {
			if (!hasFocus()) {
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
	private final List<PSliderModelObs> modelObsList = new CopyOnWriteArrayList<>();
	protected final PSliderModelObs modelObs = new PSliderModelObs() {
		public void rangeChanged(PSliderModel model) {
			fireReRenderEvent();
		}
		public void valueChanged(PSliderModel model) {
			fireReRenderEvent();
		}
	};
	protected PSliderModel model;
	
	public PSlider() {
		super();
		setModel(new DefaultPSliderModel());
		addObs(keyObs);
		addObs(mouseObs);
	}
	
	public void setModel(PSliderModel model) {
		PSliderModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			for (PSliderModelObs obs : modelObsList) {
				oldModel.removeObs(obs);
			}
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			for (PSliderModelObs obs : modelObsList) {
				model.addObs(obs);
			}
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
		
		int sldX = x + (int) (getModel().getValuePercent() * bnds.getWidth()) - DEFAULT_SLIDER_WIDTH / 2;
		int sldY = y + 1;
		int sldFx = sldX + DEFAULT_SLIDER_WIDTH;
		int sldFy = fy - 1;
		
		renderer.setColor(PColor.BLACK);
		renderer.strokeBottom(sldX, sldY, sldFx, sldFy);
		renderer.strokeRight(sldX, sldY, sldFx, sldFy);
		renderer.setColor(PColor.WHITE);
		renderer.strokeTop(sldX, sldY, sldFx, sldFy);
		renderer.strokeLeft(sldX, sldY, sldFx, sldFy);
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(sldX + 1, sldY + 1, sldFx - 1, sldFy - 1);
		
		if (hasFocus()) {
			renderer.setColor(PColor.GREY50);
			renderer.strokeQuad(x, y, fx, fy, 1);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	public void addObs(PSliderModelObs obs) {
		modelObsList.add(obs);
	}
	
	public void removeObs(PSliderModelObs obs) {
		modelObsList.remove(obs);
	}
	
}