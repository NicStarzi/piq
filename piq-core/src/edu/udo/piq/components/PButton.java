package edu.udo.piq.components;

import java.util.function.Consumer;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PGlobalEventGenerator;
import edu.udo.piq.PGlobalEventProvider;
import edu.udo.piq.PInsets;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PTimer;
import edu.udo.piq.borders.PButtonBorder;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.util.DefaultPKeyInput;
import edu.udo.piq.components.util.PKeyInput;
import edu.udo.piq.components.util.PKeyInput.KeyInputType;
import edu.udo.piq.layouts.PAnchorLayout;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PButton extends AbstractPInputLayoutOwner implements PClickable, PGlobalEventGenerator {
	
	/*
	 * Input: Press Enter
	 * If the ENTER key is pressed while the button has focus, a model and
	 * is enabled, the button will become pressed.
	 */
	
	public static final String INPUT_IDENTIFIER_PRESS_ENTER = "pressEnter";
	public static final PKeyInput<PButton> INPUT_PRESS_ENTER =
			new DefaultPKeyInput<>(KeyInputType.TRIGGER, Key.ENTER, PButton::canTriggerEnter);
	public static final Consumer<PButton> REACTION_PRESS_ENTER = PButton::onTriggerEnter;
	
	/*
	 * Input: Release Enter
	 * When ENTER is released while button has focus, is enabled and pressed,
	 * the button will become unpressed.
	 */
	
	public static final String INPUT_IDENTIFIER_RELEASE_ENTER = "releaseEnter";
	public static final PKeyInput<PButton> INPUT_RELEASE_ENTER =
			new DefaultPKeyInput<>(KeyInputType.RELEASE, Key.ENTER, PButton::canTriggerEnter);
	public static final Consumer<PButton> REACTION_RELEASE_ENTER = PButton::onReleaseEnter;
	
	protected static boolean canTriggerEnter(PButton self) {
		return self.isEnabled() && self.getModel() != null;
	}
	
	protected static void onTriggerEnter(PButton self) {
		self.getModel().setPressed(true);
	}
	
	protected static void onReleaseEnter(PButton self) {
		if (self.isPressed()) {
			self.getModel().setPressed(false);
			self.fireClickEvent();
		}
	}
	
	protected final ObserverList<PButtonModelObs> modelObsList
		= PiqUtil.createDefaultObserverList();
	protected final ObserverList<PClickObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final PButtonModelObs modelObs = (mdl) -> onModelChange();
	protected PTimer repeatTimer;
	protected PButtonModel model;
	protected PGlobalEventProvider globEvProv;
	protected boolean ignoreClickOnChildren = false;
	protected double repeatTimerInitialDelay;
	protected double repeatTimerDelay;
	
	public PButton(PComponent content) {
		this();
		setContent(content);
	}
	
	public PButton() {
		super();
		setBorder(new PButtonBorder());
		
		PButtonModel defaultModel = PModelFactory.createModelFor(this,
				DefaultPButtonModel::new, PButtonModel.class);
		
		PAnchorLayout defaultLayout = new PAnchorLayout(this);
		defaultLayout.setInsets(new ImmutablePInsets(8));
		setLayout(defaultLayout);
		setModel(defaultModel);
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
		
		defineInput(INPUT_IDENTIFIER_PRESS_ENTER, INPUT_PRESS_ENTER, REACTION_PRESS_ENTER);
		defineInput(INPUT_IDENTIFIER_RELEASE_ENTER, INPUT_RELEASE_ENTER, REACTION_RELEASE_ENTER);
	}
	
	@Override
	public void setGlobalEventProvider(PGlobalEventProvider provider) {
		globEvProv = provider;
	}
	
	@Override
	public PGlobalEventProvider getGlobalEventProvider() {
		return globEvProv;
	}
	
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
	
	public void simulateClick() {
		if (isEnabled() && getModel() != null) {
			getModel().setPressed(true);
			getModel().setPressed(false);
			takeFocus();
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
	
	protected void onModelChange() {
		if (repeatTimer != null) {
			repeatTimer.setDelay(repeatTimerInitialDelay);
			repeatTimer.setStarted(getModel().isPressed());
		}
		fireReRenderEvent();
	}
	
	protected void onMouseMoved(PMouse mouse) {
	}
	
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (isEnabled() && btn == MouseButton.LEFT && getModel() != null
				&& isMouseOverThisOrChild())
		{
			getModel().setPressed(true);
		}
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		boolean oldPressed = isPressed();
		if (btn == MouseButton.LEFT && oldPressed) {
			getModel().setPressed(false);
			if (isEnabled() && isIgnoreClickOnChildren() ?
					isMouseOver() : isMouseOverThisOrChild())
			{
				takeFocus();
				fireClickEvent();
			}
		}
	}
	
	public void addObs(PClickObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PClickObs obs) {
		obsList.remove(obs);
	}
	
	public void addObs(PButtonModelObs obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PButtonModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	protected void fireClickEvent() {
		obsList.fireEvent((obs) -> obs.onClick(this));
		fireGlobalEvent();
	}
	
}