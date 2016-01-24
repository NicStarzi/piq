package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPSliderModel;
import edu.udo.piq.components.util.PInput;
import edu.udo.piq.tools.AbstractPInputComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PSlider extends AbstractPInputComponent {
	
	protected static final int DEFAULT_SLIDER_WIDTH = 8;
	protected static final int DEFAULT_SLIDER_HEIGHT = 12;
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, DEFAULT_SLIDER_HEIGHT + 2);
	
	protected class PSliderPInput implements PInput {
		protected final Key key;
		protected final boolean ctrlDown;
		public PSliderPInput(Key key) {
			this(key, false);
		}
		public PSliderPInput(Key key, boolean requiresCtrl) {
			this.key = key;
			ctrlDown = requiresCtrl;
		}
		public Key getInputKey() {
			return key;
		}
		public KeyInputType getKeyInputType() {
			return KeyInputType.PRESS;
		}
		public boolean canBeUsed(PKeyboard keyboard) {
			return isEnabled() && getModel() != null 
					&& keyboard.isModifierToggled(Modifier.CTRL) == ctrlDown;
		}
	}
	protected final PInput pressUpInput = new PSliderPInput(Key.UP);
	protected final PInput pressRightInput = new PSliderPInput(Key.RIGHT);
	protected final PInput pressDownInput = new PSliderPInput(Key.DOWN);
	protected final PInput pressLeftInput = new PSliderPInput(Key.LEFT);
	protected final PInput pressCtrlUpInput = new PSliderPInput(Key.UP, true);
	protected final PInput pressCtrlRightInput = new PSliderPInput(Key.RIGHT, true);
	protected final PInput pressCtrlDownInput = new PSliderPInput(Key.DOWN, true);
	protected final PInput pressCtrlLeftInput = new PSliderPInput(Key.LEFT, true);
	protected final Runnable addReaction = new Runnable() {
		public void run() {
			getModel().setValue(getModel().getValue() + 1);
		}
	};
	protected final Runnable subReaction = new Runnable() {
		public void run() {
			getModel().setValue(getModel().getValue() - 1);
		}
	};
	protected final Runnable addFastReaction = new Runnable() {
		public void run() {
			PSliderModel model = getModel();
			int max = model.getMaxValue();
			int add = (int) Math.ceil(max * 0.1);
			model.setValue(model.getValue() + add);
		}
	};
	protected final Runnable subFastReaction = new Runnable() {
		public void run() {
			PSliderModel model = getModel();
			int max = model.getMaxValue();
			int sub = (int) Math.ceil(max * 0.1);
			model.setValue(model.getValue() - sub);
		}
	};
	
	private final PMouseObs mouseObs = new PMouseObs() {
		public void onMouseMoved(PMouse mouse) {
			if (mouse.isPressed(MouseButton.LEFT) && getModel().isPressed()) {
				updatePosition(mouse);
			}
		}
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && !getModel().isPressed()
					&& isMouseWithinClippedBounds()) {
				
				getModel().setPressed(true);
				takeFocus();
				updatePosition(mouse);
			}
		}
		public void onButtonReleased(PMouse mouse, MouseButton btn) {
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
	protected final ObserverList<PSliderModelObs> modelObsList
		= PCompUtil.createDefaultObserverList();
	protected final PSliderModelObs modelObs = new PSliderModelObs() {
		public void onRangeChanged(PSliderModel model) {
			fireReRenderEvent();
		}
		public void onValueChanged(PSliderModel model) {
			fireReRenderEvent();
		}
	};
	protected PSliderModel model;
	
	public PSlider() {
		super();
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PSliderModel defaultModel = new DefaultPSliderModel();
		if (modelFac != null) {
			defaultModel = (PSliderModel) modelFac.getModelFor(this, defaultModel);
		}
		
		setModel(defaultModel);
		
		addObs(mouseObs);
		
		defineInput("up",			pressUpInput,			addReaction);
		defineInput("right",		pressRightInput,		addReaction);
		defineInput("down",			pressDownInput,			subReaction);
		defineInput("left",			pressLeftInput,			subReaction);
		defineInput("ctrlUp",		pressCtrlUpInput,		addFastReaction);
		defineInput("ctrlRight",	pressCtrlRightInput,	addFastReaction);
		defineInput("ctrlDown",		pressCtrlDownInput,		subFastReaction);
		defineInput("ctrlLeft",		pressCtrlLeftInput,		subFastReaction);
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
			renderer.setRenderMode(renderer.getRenderModeOutlineDashed());
			renderer.drawQuad(x, y, fx, fy);
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
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PSliderModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
}