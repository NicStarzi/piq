package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentAction;
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
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.components.util.AbstractPKeyInput;
import edu.udo.piq.components.util.PKeyInput;
import edu.udo.piq.components.util.PKeyInput.KeyInputType;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class PButton extends AbstractPInputLayoutOwner implements PClickable, PGlobalEventGenerator {
	
	public static final PKeyInput INPUT_PRESS_ENTER = new AbstractPKeyInput(
			KeyInputType.TRIGGER, Key.ENTER, (comp) -> 
	{
		PButton btn = (PButton) comp;
		return btn.isEnabled() && btn.getModel() != null;
	});
	public static final PKeyInput INPUT_RELEASE_ENTER = new AbstractPKeyInput(
			KeyInputType.RELEASE, Key.ENTER, INPUT_PRESS_ENTER.getOptionalCondition());
	public static final PComponentAction REACTION_PRESS_ENTER = (comp) -> {
		PButton btn = ThrowException.ifTypeCastFails(comp, PButton.class, 
				"!(comp instanceof PButton)");
		btn.getModel().setPressed(true);
	};
	public static final PComponentAction REACTION_RELEASE_ENTER = (comp) -> {
		PButton btn = ThrowException.ifTypeCastFails(comp, PButton.class, 
				"!(comp instanceof PButton)");
		if (btn.isPressed() && btn.isEnabled()) {
			btn.getModel().setPressed(false);
			btn.fireClickEvent();
		}
	};
	public static final String INPUT_IDENTIFIER_PRESS_ENTER = "pressEnter";
	public static final String INPUT_IDENTIFIER_RELEASE_ENTER = "releaseEnter";
	
	protected final ObserverList<PButtonModelObs> modelObsList
		= PCompUtil.createDefaultObserverList();
	protected final ObserverList<PClickObs> obsList
		= PCompUtil.createDefaultObserverList();
	protected final PButtonModelObs modelObs = (mdl) -> onModelChange();
	protected PTimer repeatTimer;
	protected PButtonModel model;
	protected PGlobalEventProvider globEvProv;
	protected boolean ignoreClickOnChildren = false;
	protected int repeatTimerInitialDelay;
	protected int repeatTimerDelay;
	
	public PButton(PComponent content) {
		this();
		setContent(content);
	}
	
	public PButton() {
		super();
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PButtonModel defaultModel = new DefaultPButtonModel();
		if (modelFac != null) {
			defaultModel = (PButtonModel) modelFac.getModelFor(this, defaultModel);
		}
		
		PCentricLayout defaultLayout = new PCentricLayout(this);
		defaultLayout.setInsets(new ImmutablePInsets(8));
		setLayout(defaultLayout);
		setModel(defaultModel);
		addObs(new PMouseObs() {
			public void onMouseMoved(PMouse mouse) {
				PButton.this.onMouseMoved(mouse);
			}
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PButton.this.onMouseButtonTriggered(mouse, btn);
			}
			public void onButtonPressed(PMouse mouse, MouseButton btn) {
				PButton.this.onMouseButtonPressed(mouse, btn);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				PButton.this.onMouseButtonReleased(mouse, btn);
			}
		});
		addObs(new ReRenderPFocusObs());
		
		defineInput(INPUT_IDENTIFIER_PRESS_ENTER, INPUT_PRESS_ENTER, REACTION_PRESS_ENTER);
		defineInput(INPUT_IDENTIFIER_RELEASE_ENTER, INPUT_RELEASE_ENTER, REACTION_RELEASE_ENTER);
	}
	
	public void setGlobalEventProvider(PGlobalEventProvider provider) {
		globEvProv = provider;
	}
	
	public PGlobalEventProvider getGlobalEventProvider() {
		return globEvProv;
	}
	
	protected PCentricLayout getLayoutInternal() {
		return (PCentricLayout) super.getLayout();
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
	
	public void setRepeatTimer(int initialDelay, int delayBetweenEvents) {
		repeatTimerInitialDelay = initialDelay;
		repeatTimerDelay = delayBetweenEvents;
		if (repeatTimer == null) {
			repeatTimer = new PTimer(this, () -> {
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
			for (PButtonModelObs obs : modelObsList) {
				oldModel.removeObs(obs);
			}
		}
		this.model = model;
		if (model != null) {
			model.addObs(modelObs);
			for (PButtonModelObs obs : modelObsList) {
				model.addObs(obs);
			}
		}
		fireReRenderEvent();
	}
	
	public PButtonModel getModel() {
		return model;
	}
	
	public boolean isPressed() {
		if (getModel() == null) {
			return false;
		}
		return isEnabled() && getModel().isPressed();
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		if (isPressed()) {
			renderer.setRenderMode(renderer.getRenderModeOutline());
			renderer.setColor(PColor.GREY25);
			renderer.drawQuad(x, y, fx, fy);
			renderer.setRenderMode(renderer.getRenderModeFill());
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.BLACK);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(PColor.WHITE);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
		if (hasFocus()) {
			PInsets insets = getLayoutInternal().getInsets();
			int innerX = x + insets.getFromLeft() - 1;
			int innerY = y + insets.getFromTop() - 1;
			int innerFx = fx - insets.getFromRight() + 1;
			int innerFy = fy - insets.getFromBottom() + 1;
			
			renderer.setRenderMode(renderer.getRenderModeOutlineDashed());
			renderer.setColor(PColor.GREY50);
			renderer.drawQuad(innerX, innerY, innerFx, innerFy);
		}
	}
	
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
		if (isEnabled() && btn == MouseButton.LEFT 
//				&& getModel() != null && isMouseOver())
				&& getModel() != null && isMouseOverThisOrChild())
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