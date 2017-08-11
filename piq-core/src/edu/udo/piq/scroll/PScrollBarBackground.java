package edu.udo.piq.scroll;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;
import edu.udo.piq.PTimerCallback;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.PButtonModel;
import edu.udo.piq.components.PButtonModelObs;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.ImmutablePSize;

public class PScrollBarBackground extends AbstractPComponent {
	
	private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(12, 12);
	private static final int CLICK_REPEAT_TIMER_INITIAL_DELAY = 40;
	private static final int CLICK_REPEAT_TIMER_REPEAT_DELAY = 4;
	
	private final List<PScrollBarBackgroundObs> obsList = new CopyOnWriteArrayList<>();
	private final PTimer clickRepeatTimer = new PTimer(this, new PTimerCallback() {
		public void onTimerEvent(double deltaTime) {
			clickRepeatTimer.setDelay(CLICK_REPEAT_TIMER_REPEAT_DELAY);
			fireClickEvent();
		}
	});
	private final PMouseObs mouseObs = new PMouseObs() {
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			if (isActive() && btn == MouseButton.LEFT && isMouseOver()) {
				getModel().setPressed(true);
				clickRepeatTimer.setDelay(CLICK_REPEAT_TIMER_INITIAL_DELAY);
				clickRepeatTimer.start();
				fireClickEvent();
			}
		}
		public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
			if (isActive() && btn == MouseButton.LEFT) {
				clickRepeatTimer.stop();
				getModel().setPressed(false);
			}
		}
	};
	protected final PButtonModelObs modelObs = new PButtonModelObs() {
		public void onChange(PButtonModel model) {
			fireReRenderEvent();
		}
	};
	protected PButtonModel model;
	protected boolean active;
	
	public PScrollBarBackground() {
		super();
		clickRepeatTimer.setRepeating(true);
		setModel(new DefaultPButtonModel());
		addObs(mouseObs);
	}
	
	protected void setModel(PButtonModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		fireReRenderEvent();
	}
	
	public PButtonModel getModel() {
		return model;
	}
	
	public void setActive(boolean value) {
		active = value;
		clickRepeatTimer.stop();
		getModel().setPressed(false);
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isPressed() {
		if (getModel() == null) {
			return false;
		}
		return isActive() && getModel().isPressed();
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		if (isPressed()) {
			renderer.setColor(PColor.BLACK);
		} else {
			renderer.setColor(PColor.GREY875);
		}
		renderer.drawQuad(bnds);
	}
	
	public PSize getDefaultPreferredSize() {
		return DEFAULT_PREFERRED_SIZE;
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public void addObs(PScrollBarBackgroundObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PScrollBarBackgroundObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireClickEvent() {
		for (PScrollBarBackgroundObs obs : obsList) {
			obs.onClick(this);
		}
	}
	
}