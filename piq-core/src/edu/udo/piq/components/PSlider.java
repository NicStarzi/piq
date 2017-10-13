package edu.udo.piq.components;

import java.util.function.Consumer;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PGlobalEventGenerator;
import edu.udo.piq.PGlobalEventProvider;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPSliderModel;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.util.PKeyInput;
import edu.udo.piq.tools.AbstractPInputComponent;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PSlider extends AbstractPInputComponent implements PGlobalEventGenerator {
	
	protected static final int DEFAULT_SLIDER_WIDTH = 8;
	protected static final int DEFAULT_SLIDER_HEIGHT = 12;
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, DEFAULT_SLIDER_HEIGHT + 2);
	
	public static final PKeyInput<PSlider> INPUT_PRESS_UP = new PSliderKeyInput(Key.UP);
	public static final PKeyInput<PSlider> INPUT_PRESS_RIGHT = new PSliderKeyInput(Key.RIGHT);
	public static final PKeyInput<PSlider> INPUT_PRESS_DOWN = new PSliderKeyInput(Key.DOWN);
	public static final PKeyInput<PSlider> INPUT_PRESS_LEFT = new PSliderKeyInput(Key.LEFT);
	public static final PKeyInput<PSlider> INPUT_PRESS_CTRL_UP = new PSliderKeyInput(Key.UP, true);
	public static final PKeyInput<PSlider> INPUT_PRESS_CTRL_RIGHT = new PSliderKeyInput(Key.RIGHT, true);
	public static final PKeyInput<PSlider> INPUT_PRESS_CTRL_DOWN = new PSliderKeyInput(Key.DOWN, true);
	public static final PKeyInput<PSlider> INPUT_PRESS_CTRL_LEFT = new PSliderKeyInput(Key.LEFT, true);
	public static final Consumer<PSlider> REACTION_ADD = new PSliderAction(1, 0);
	public static final Consumer<PSlider> REACTION_SUB = new PSliderAction(-1, 0);
	public static final Consumer<PSlider> REACTION_ADD_FAST = new PSliderAction(0, 0.1);
	public static final Consumer<PSlider> REACTION_SUB_FAST = new PSliderAction(0, -0.1);
	public static final String INPUT_ID_PRESS_UP = "up";
	public static final String INPUT_ID_PRESS_DOWN = "down";
	public static final String INPUT_ID_PRESS_LEFT = "left";
	public static final String INPUT_ID_PRESS_RIGHT = "right";
	public static final String INPUT_ID_PRESS_CTRL_UP = "ctrlUp";
	public static final String INPUT_ID_PRESS_CTRL_DOWN = "ctrlDown";
	public static final String INPUT_ID_PRESS_CTRL_LEFT = "ctrlLeft";
	public static final String INPUT_ID_PRESS_CTRL_RIGHT = "ctrlRight";
	
	protected final ObserverList<PSliderModelObs> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final PSliderModelObs modelObs = new PSliderModelObs() {
		@Override
		public void onRangeChanged(PSliderModel model) {
			PSlider.this.onModelRangeChanged();
		}
		@Override
		public void onValueChanged(PSliderModel model) {
			PSlider.this.onModelValueChanged();
		}
	};
	protected PSliderModel model;
	protected PGlobalEventProvider globEvProv;
	
	public PSlider() {
		super();
		setModel(PModelFactory.createModelFor(this, DefaultPSliderModel::new, PSliderModel.class));
		
		addObs(new PMouseObs() {
			@Override
			public void onMouseMoved(PMouse mouse) {
				PSlider.this.onMouseMoved(mouse);
			}
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PSlider.this.onMouseButtonTriggered(mouse, btn);
			}
			@Override
			public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
				PSlider.this.onMouseButtonPressed(mouse, btn);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PSlider.this.onMouseButtonReleased(mouse, btn);
			}
		});
		addObs(new ReRenderPFocusObs());
		
		defineInput(INPUT_ID_PRESS_UP, INPUT_PRESS_UP, REACTION_ADD);
		defineInput(INPUT_ID_PRESS_RIGHT, INPUT_PRESS_RIGHT, REACTION_ADD);
		defineInput(INPUT_ID_PRESS_DOWN, INPUT_PRESS_DOWN, REACTION_SUB);
		defineInput(INPUT_ID_PRESS_LEFT, INPUT_PRESS_LEFT, REACTION_SUB);
		defineInput(INPUT_ID_PRESS_CTRL_UP, INPUT_PRESS_CTRL_UP, REACTION_ADD_FAST);
		defineInput(INPUT_ID_PRESS_CTRL_RIGHT, INPUT_PRESS_CTRL_RIGHT, REACTION_ADD_FAST);
		defineInput(INPUT_ID_PRESS_CTRL_DOWN, INPUT_PRESS_CTRL_DOWN, REACTION_SUB_FAST);
		defineInput(INPUT_ID_PRESS_CTRL_LEFT, INPUT_PRESS_CTRL_LEFT, REACTION_SUB_FAST);
	}
	
	public PSlider(PSliderModel model) {
		this();
		setModel(model);
	}
	
	public PSlider(int value, int min, int max) {
		this();
		getModel().setMinValue(min);
		getModel().setMaxValue(max);
		getModel().setValue(value);
	}
	
	@Override
	public void setGlobalEventProvider(PGlobalEventProvider provider) {
		globEvProv = provider;
	}
	
	@Override
	public PGlobalEventProvider getGlobalEventProvider() {
		return globEvProv;
	}
	
	public void setModel(PSliderModel model) {
		PSliderModel oldModel = getModel();
		if (oldModel != null) {
			oldModel.removeObs(modelObs);
			modelObsList.forEach(obs -> oldModel.removeObs(obs));
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			modelObsList.forEach(obs -> model.addObs(obs));
		}
		fireReRenderEvent();
	}
	
	public PSliderModel getModel() {
		return model;
	}
	
	public boolean isPressed() {
		return getModel().isPressed();
	}
	
	@Override
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
	
	@Override
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return false;
	}
	
	@Override
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
	
	protected void onMouseMoved(PMouse mouse) {
		if (mouse.isPressed(MouseButton.LEFT) && getModel().isPressed()) {
			updateModelValue(mouse);
		}
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && !getModel().isPressed()
				&& isMouseWithinClippedBounds())
		{
			getModel().setPressed(true);
			takeFocus();
			updateModelValue(mouse);
		}
	}
	
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && getModel().isPressed()) {
			getModel().setPressed(false);
		}
	}
	
	protected void updateModelValue(PMouse mouse) {
		int mx = mouse.getX();
		PBounds bnds = getBounds();
		double valuePercent = (mx - bnds.getX()) / (double) bnds.getWidth();
		getModel().setValuePercent(valuePercent);
		fireGlobalEvent();
	}
	
	protected void onModelRangeChanged() {
		fireReRenderEvent();
	}
	
	protected void onModelValueChanged() {
		fireReRenderEvent();
	}
	
	protected static class PSliderKeyInput implements PKeyInput<PSlider> {
		
		protected final Key key;
		protected final boolean ctrlDown;
		
		public PSliderKeyInput(Key key) {
			this(key, false);
		}
		
		public PSliderKeyInput(Key key, boolean requiresCtrl) {
			this.key = key;
			ctrlDown = requiresCtrl;
		}
		
		@Override
		public Key getKey() {
			return key;
		}
		
		@Override
		public KeyInputType getKeyInputType() {
			return KeyInputType.PRESS;
		}
		
		@Override
		public Condition<PSlider> getCondition() {
			return self -> self.isEnabled() && self.getModel() != null;
		}
		
		@Override
		public int getModifierCount() {
			return ctrlDown ? 1 : 0;
		}
		
		@Override
		public Modifier getModifier(int index) {
			return Modifier.CTRL;
		}
	}
	
	protected static class PSliderAction implements Consumer<PSlider> {
		
		protected final int bonus;
		protected final double percent;
		
		public PSliderAction(int modValue, double factor) {
			bonus = modValue;
			percent = factor;
		}
		
		@Override
		public void accept(PSlider comp) {
			PSliderModel model = comp.getModel();
			
			int oldVal = model.getValue();
			int newValPercent = (int) Math.ceil(oldVal * percent);
			int newValBonus = oldVal + bonus;
			int newVal = Math.max(newValPercent, newValBonus);
			model.setValue(newVal);
		}
	}
	
}