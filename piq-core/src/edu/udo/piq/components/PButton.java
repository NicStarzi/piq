package edu.udo.piq.components;

import edu.udo.piq.CallSuper;
import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PTimer;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.actions.FocusOwnerAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.StandardComponentActionKey;
import edu.udo.piq.borders.PButtonBorder;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.textbased.PLabel;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PModelFactory;
import edu.udo.piq.util.PiqUtil;

public class PButton extends AbstractPInteractiveLayoutOwner implements PInteractiveComponent, PClickable {
	
	public static final PActionKey KEY_PRESS_ENTER = StandardComponentActionKey.INTERACT;
	public static final PAccelerator ACCELERATOR_PRESS_ENTER = new PAccelerator(
			ActualKey.ENTER, FocusPolicy.THIS_HAS_FOCUS, KeyInputType.PRESS);
	public static final PComponentAction ACTION_PRESS_ENTER = new FocusOwnerAction<>(
			PButton.class, false,
			ACCELERATOR_PRESS_ENTER,
			self -> !self.isPressed() && self.isEnabled(),
			self -> self.getModel().setPressed(true));
	
	public static final PActionKey KEY_RELEASE_ENTER = new PActionKey("RELEASE_ENTER");
	public static final PAccelerator ACCELERATOR_RELEASE_ENTER = new PAccelerator(
			ActualKey.ENTER, FocusPolicy.THIS_HAS_FOCUS, KeyInputType.RELEASE);
	public static final PComponentAction ACTION_RELEASE_ENTER = new FocusOwnerAction<>(
			PButton.class, false,
			ACCELERATOR_PRESS_ENTER,
			self -> self.isPressed() && self.isEnabled(),
			self -> {
				self.getModel().setPressed(false);
				self.fireClickEvent();
			});
	
	protected final ObserverList<PSingleValueModelObs> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final ObserverList<PClickObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final PSingleValueModelObs modelObs = this::onModelChange;
	protected PButtonModel model;
	protected PTimer repeatTimer;
	protected boolean ignoreClickOnChildren = false;
	protected double repeatTimerInitialDelay;
	protected double repeatTimerDelay;
	
	public PButton(Object initialLabelValue, PClickObs clickObs) {
		this(initialLabelValue);
		addObs(clickObs);
	}
	
	public PButton(PTextModel initialLabelModel, PClickObs clickObs) {
		this(initialLabelModel);
		addObs(clickObs);
	}
	
	public PButton(PComponent content, PClickObs clickObs) {
		this(content);
		addObs(clickObs);
	}
	
	public PButton(PClickObs clickObs) {
		this();
		addObs(clickObs);
	}
	
	public PButton(Object initialLabelValue) {
		this(new PLabel(initialLabelValue));
	}
	
	public PButton(PTextModel initialLabelModel) {
		this(new PLabel(initialLabelModel));
	}
	
	public PButton(PComponent content) {
		this();
		setContent(content);
	}
	
	public PButton() {
		super();
		setBorder(new PButtonBorder());
		
		PButtonModel defaultModel = PModelFactory.createModelFor(this,
				DefaultPButtonModel::new, PButtonModel.class);
		PEnableModel defaultEnableModel = PModelFactory.createModelFor(this,
				DefaultPEnableModel::new, PEnableModel.class);
		
		PAnchorLayout defaultLayout = new PAnchorLayout(this);
		defaultLayout.setInsets(new ImmutablePInsets(8));
		setLayout(defaultLayout);
		setModel(defaultModel);
		setEnableModel(defaultEnableModel);
		addObs(new PMouseObs() {
			@Override
			public void onMouseMoved(PMouse mouse) {
				PButton.this.onMouseMoved(mouse);
			}
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PButton.this.onMouseButtonTriggered(mouse, btn);
			}
			@Override
			public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
				PButton.this.onMouseButtonPressed(mouse, btn);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PButton.this.onMouseButtonReleased(mouse, btn);
			}
		});
		addObs(new ReRenderPFocusObs());
		
		addActionMapping(KEY_PRESS_ENTER, ACTION_PRESS_ENTER);
		addActionMapping(KEY_RELEASE_ENTER, ACTION_RELEASE_ENTER);
	}
	
	@Override
	protected PAnchorLayout getLayoutInternal() {
		return (PAnchorLayout) super.getLayout();
	}
	
	public PInsets getDefaultFocusInsets() {
		return getLayoutInternal().getInsets();
	}
	
	public void setContent(PComponent component) {
		getLayoutInternal().clearChildren();
		getLayoutInternal().addChild(component, null);
	}
	
	public PComponent getContent() {
		return getLayoutInternal().getContent();
	}
	
	public void setIgnoreClickOnChildren(boolean isIgnoreClickOnChildren) {
		ignoreClickOnChildren = isIgnoreClickOnChildren;
	}
	
	public boolean isIgnoreClickOnChildren() {
		return ignoreClickOnChildren;
	}
	
	public void setRepeatTimer(double initialDelay, double delayBetweenEvents) {
		repeatTimerInitialDelay = initialDelay;
		repeatTimerDelay = delayBetweenEvents;
		if (repeatTimer == null) {
			repeatTimer = new PTimer(this, (deltaTime) -> {
				repeatTimer.setDelay(repeatTimerDelay);
				if (isEnabled()) {
					fireClickEvent();
				} else {
					repeatTimer.stop();
				}
			});
			repeatTimer.setRepeating(true);
		}
		if (repeatTimer.isStarted()) {
			repeatTimer.setDelay(repeatTimerDelay);
		} else {
			repeatTimer.setDelay(repeatTimerInitialDelay);
		}
	}
	
	public void setModel(PButtonModel model) {
		PButtonModel oldModel = getModel();
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
	
	public PButtonModel getModel() {
		return model;
	}
	
	@Override
	public boolean isFocusable() {
		return isEnabled();
	}
	
	@Override
	public boolean isStrongFocusOwner() {
		return false;
	}
	
	public void simulateClick() {
		if (isEnabled() && getModel() != null) {
			takeFocus();
			getModel().setPressed(true);
			getModel().setPressed(false);
			fireClickEvent();
		}
	}
	
	public boolean isPressed() {
		if (getModel() == null) {
			return false;
		}
		return isEnabled() && getModel().isPressed();
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBoundsWithoutBorder();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(x, y, fx, fy);
		
		if (hasFocus()) {
			PInsets insets = getDefaultFocusInsets();
			int innerX = x + insets.getFromLeft() - 1;
			int innerY = y + insets.getFromTop() - 1;
			int innerFx = fx - insets.getFromRight() + 1;
			int innerFy = fy - insets.getFromBottom() + 1;
			
			renderer.setRenderMode(renderer.getRenderModeOutlineDashed());
			renderer.setColor(PColor.GREY50);
			renderer.drawQuad(innerX, innerY, innerFx, innerFy);
		}
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	@TemplateMethod
	@CallSuper
	protected void onModelChange(PSingleValueModel model, Object oldVal, Object newVal) {
		if (repeatTimer != null) {
			repeatTimer.setDelay(repeatTimerInitialDelay);
			repeatTimer.setStarted(getModel().isPressed());
		}
		fireReRenderEvent();
	}
	
	@TemplateMethod
	protected void onMouseMoved(PMouse mouse) {}
	
	@TemplateMethod
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {}
	
	@TemplateMethod
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (isEnabled() && btn == MouseButton.LEFT && getModel() != null
				&& isMouseOverThisOrChild(mouse))
		{
			getModel().setPressed(true);
		}
	}
	
	@TemplateMethod
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		boolean oldPressed = isPressed();
		if (btn == MouseButton.LEFT && oldPressed) {
			getModel().setPressed(false);
			if (isEnabled() && isIgnoreClickOnChildren() ?
					isMouseOver(mouse) : isMouseOverThisOrChild(mouse))
			{
				takeFocus();
				fireClickEvent();
			}
		}
	}
	
	@Override
	public void addObs(PClickObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PClickObs obs) {
		obsList.remove(obs);
	}
	
	public void addObs(PSingleValueModelObs obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PSingleValueModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	protected void fireClickEvent() {
		obsList.fireEvent((obs) -> obs.onClick(this));
	}
	
}