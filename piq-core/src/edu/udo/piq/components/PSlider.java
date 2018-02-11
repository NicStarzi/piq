package edu.udo.piq.components;

import java.util.function.Consumer;
import java.util.function.Predicate;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.actions.FocusOwnerAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.StandardComponentActionKey;
import edu.udo.piq.components.defaults.DefaultPSliderModel;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;

public class PSlider extends AbstractPInteractiveComponent {
	
	protected static final int DEFAULT_SLIDER_KNOB_WIDTH = 8;
	protected static final int DEFAULT_SLIDER_KNOB_HEIGHT = 12;
	protected static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(100, DEFAULT_SLIDER_KNOB_HEIGHT + 2);
	
	public static final Predicate<PSlider> ACTION_CONDITION = self -> self.getModel() != null && self.isEnabled();
	public static final Consumer<PSlider> ACTION_ADD_1 = new AddToSliderModel(+1);
	public static final Consumer<PSlider> ACTION_SUB_1 = new AddToSliderModel(-1);
	public static final Consumer<PSlider> ACTION_ADD_10 = new AddToSliderModel(+10);
	public static final Consumer<PSlider> ACTION_SUB_10 = new AddToSliderModel(-10);
	public static final PAccelerator ACCELERATOR_UP = new PAccelerator(ActualKey.UP, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_RIGHT = new PAccelerator(ActualKey.RIGHT, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_DOWN = new PAccelerator(ActualKey.DOWN, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_LEFT = new PAccelerator(ActualKey.LEFT, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_CTRL_UP = new PAccelerator(ActualKey.UP, Modifier.CTRL, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_CTRL_RIGHT = new PAccelerator(ActualKey.RIGHT, Modifier.CTRL, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_CTRL_DOWN = new PAccelerator(ActualKey.DOWN, Modifier.CTRL, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_CTRL_LEFT = new PAccelerator(ActualKey.LEFT, Modifier.CTRL, KeyInputType.PRESS);
	public static final PComponentAction ACTION_UP = new FocusOwnerAction<>(PSlider.class, false, ACCELERATOR_UP, ACTION_CONDITION, ACTION_ADD_1);
	public static final PComponentAction ACTION_RIGHT = new FocusOwnerAction<>(PSlider.class, false, ACCELERATOR_RIGHT, ACTION_CONDITION, ACTION_ADD_1);
	public static final PComponentAction ACTION_DOWN = new FocusOwnerAction<>(PSlider.class, false, ACCELERATOR_DOWN, ACTION_CONDITION, ACTION_SUB_1);
	public static final PComponentAction ACTION_LEFT = new FocusOwnerAction<>(PSlider.class, false, ACCELERATOR_LEFT, ACTION_CONDITION, ACTION_SUB_1);
	public static final PComponentAction ACTION_CTRL_UP = new FocusOwnerAction<>(PSlider.class, false, ACCELERATOR_CTRL_UP, ACTION_CONDITION, ACTION_ADD_10);
	public static final PComponentAction ACTION_CTRL_RIGHT = new FocusOwnerAction<>(PSlider.class, false, ACCELERATOR_CTRL_RIGHT, ACTION_CONDITION, ACTION_ADD_10);
	public static final PComponentAction ACTION_CTRL_DOWN = new FocusOwnerAction<>(PSlider.class, false, ACCELERATOR_CTRL_DOWN, ACTION_CONDITION, ACTION_SUB_10);
	public static final PComponentAction ACTION_CTRL_LEFT = new FocusOwnerAction<>(PSlider.class, false, ACCELERATOR_CTRL_LEFT, ACTION_CONDITION, ACTION_SUB_10);
	public static final PActionKey KEY_UP = StandardComponentActionKey.MOVE_NEXT;
	public static final PActionKey KEY_RIGHT = new PActionKey("RIGHT");
	public static final PActionKey KEY_DOWN = StandardComponentActionKey.MOVE_PREV;
	public static final PActionKey KEY_LEFT = new PActionKey("LEFT");
	public static final PActionKey KEY_CTRL_UP = new PActionKey("CTRL_UP");
	public static final PActionKey KEY_CTRL_RIGHT = new PActionKey("CTRL_RIGHT");
	public static final PActionKey KEY_CTRL_DOWN = new PActionKey("CTRL_DOWN");
	public static final PActionKey KEY_CTRL_LEFT = new PActionKey("CTRL_LEFT");
	
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
		
		addActionMapping(KEY_UP, ACTION_UP);
		addActionMapping(KEY_RIGHT, ACTION_RIGHT);
		addActionMapping(KEY_DOWN, ACTION_DOWN);
		addActionMapping(KEY_LEFT, ACTION_LEFT);
		addActionMapping(KEY_CTRL_UP, ACTION_CTRL_UP);
		addActionMapping(KEY_CTRL_RIGHT, ACTION_CTRL_RIGHT);
		addActionMapping(KEY_CTRL_DOWN, ACTION_CTRL_DOWN);
		addActionMapping(KEY_CTRL_LEFT, ACTION_CTRL_LEFT);
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
	
	public int getModelValue() {
		PSliderModel model = getModel();
		if (model == null) {
			return 0;
		}
		return getModel().getValue();
	}
	
	public boolean isPressed() {
		return getModel().isPressed();
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBoundsWithoutBorder();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		int centerY = y + bnds.getHeight() / 2;
		
		boolean enabled = isEnabled();
		renderer.setColor(enabled ? PColor.BLACK : PColor.GREY50);
		renderer.drawQuad(x, centerY - 1, fx, centerY + 1);
		
		int sldX = x + (int) (getModel().getValuePercent() * bnds.getWidth()) - DEFAULT_SLIDER_KNOB_WIDTH / 2;
		int sldY = y + 1;
		int sldFx = sldX + DEFAULT_SLIDER_KNOB_WIDTH;
		int sldFy = fy - 1;
		
		renderer.setColor(enabled ? PColor.BLACK : PColor.GREY50);
		renderer.strokeBottom(sldX, sldY, sldFx, sldFy);
		renderer.strokeRight(sldX, sldY, sldFx, sldFy);
		renderer.setColor(enabled ? PColor.WHITE : PColor.GREY875);
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
	public boolean isStrongFocusOwner() {
		return false;
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
	
	protected void updateModelValue(PMouse mouse) {
		int mx = mouse.getX();
		PBounds bnds = getBounds();
		double valuePercent = (mx - bnds.getX()) / (double) bnds.getWidth();
		getModel().setValuePercent(valuePercent);
	}
	
	@TemplateMethod
	protected void onMouseMoved(PMouse mouse) {
		if (mouse.isPressed(MouseButton.LEFT) && isEnabled() && getModel().isPressed()) {
			updateModelValue(mouse);
		}
	}
	
	@TemplateMethod
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isEnabled() && !getModel().isPressed()
				&& isMouseWithinClippedBounds())
		{
			getModel().setPressed(true);
			takeFocus();
			updateModelValue(mouse);
		}
	}
	
	@TemplateMethod
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {}
	
	@TemplateMethod
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isEnabled() && getModel().isPressed()) {
			getModel().setPressed(false);
		}
	}
	
	@TemplateMethod
	protected void onModelRangeChanged() {
		fireReRenderEvent();
	}
	
	@TemplateMethod
	protected void onModelValueChanged() {
		fireReRenderEvent();
	}
	
	protected static class AddToSliderModel implements Consumer<PSlider> {
		protected final int bonus;
		public AddToSliderModel(int modValue) {bonus = modValue;}
		@Override
		public void accept(PSlider comp) {
			PSliderModel model = comp.getModel();
			model.setValue(model.getValue() + bonus);
		}
	}
	
}