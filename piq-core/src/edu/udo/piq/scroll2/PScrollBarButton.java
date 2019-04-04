package edu.udo.piq.scroll2;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.layouts.Axis;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PScrollBarButton extends AbstractPComponent implements PClickable {
	
	public static final double DEFAULT_REPEAT_TIMER_DELAY = 25;
	public static final double DEFAULT_REPEAT_TIMER_INITIAL_DELAY = 250;
	
	protected final ObserverList<PClickObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final PTimer repeatTimer;
	protected final Axis scrollBarAxis;
	protected final boolean thisIsIncr;
	protected double repeatTimerInitialDelay = DEFAULT_REPEAT_TIMER_INITIAL_DELAY;
	protected double repeatTimerDelay = DEFAULT_REPEAT_TIMER_DELAY;
	protected boolean pressed = false;
	
	public PScrollBarButton(Axis axis, boolean isIncrementButton) {
		scrollBarAxis = axis;
		thisIsIncr = isIncrementButton;
		addObs(new PMouseObs() {
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PScrollBarButton.this.onMouseButtonTriggered(mouse, btn);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PScrollBarButton.this.onMouseButtonReleased(mouse, btn);
			}
		});
		repeatTimer = new PTimer(this, this::onTimerTick);
		repeatTimer.setDelay(repeatTimerInitialDelay);
		repeatTimer.setRepeating(true);
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	protected void setPressed(boolean value) {
		if (pressed == value) {
			return;
		}
		pressed = value;
		if (repeatTimer != null) {
			repeatTimer.setDelay(repeatTimerInitialDelay);
			repeatTimer.setRunning(pressed);
		}
		fireReRenderEvent();
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOver(mouse)) {
			setPressed(true);
			fireClickEvent();
		}
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		boolean oldPressed = isPressed();
		if (btn == MouseButton.LEFT && oldPressed) {
			setPressed(false);
		}
	}
	
	protected void onTimerTick(double deltaTimeMillis) {
		repeatTimer.setDelay(repeatTimerDelay);
		fireClickEvent();
	}
	
	public void setRepeatTimerDelay(double initialDelay, double delayBetweenEvents) {
		repeatTimerInitialDelay = initialDelay;
		repeatTimerDelay = delayBetweenEvents;
		if (repeatTimer.isRunning()) {
			repeatTimer.setDelay(repeatTimerDelay);
		} else {
			repeatTimer.setDelay(repeatTimerInitialDelay);
		}
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
		renderer.setColor(PColor.BLACK);
		PScrollBarButton.renderTriangle(renderer, scrollBarAxis, thisIsIncr, x + 4, y + 4, fx - 4, fy - 4);
		if (isPressed()) {
			renderer.setColor(PColor.GREY875);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(PColor.GREY50);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			
			renderer.setColor(PColor.GREY50);
			renderer.strokeBottom(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeRight(x + 1, y + 1, fx - 1, fy - 1);
			renderer.setColor(PColor.GREY25);
			renderer.strokeTop(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeLeft(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.BLACK);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.strokeRight(x, y, fx, fy);
			renderer.setColor(PColor.WHITE);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			
			renderer.setColor(PColor.GREY25);
			renderer.strokeBottom(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeRight(x + 1, y + 1, fx - 1, fy - 1);
			renderer.setColor(PColor.GREY875);
			renderer.strokeTop(x + 1, y + 1, fx - 1, fy - 1);
			renderer.strokeLeft(x + 1, y + 1, fx - 1, fy - 1);
		}
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	@Override
	public PSize getDefaultPreferredSize() {
		prefSize.set(PScrollBarKnob.DEFAULT_SIZE);
		return prefSize;
	}
	
	@Override
	public void addObs(PClickObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PClickObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireClickEvent() {
		obsList.fireEvent(obs -> obs.onClick(this));
	}
	
	protected static void renderTriangle(PRenderer renderer, Axis axis, boolean isIncr,
			float x, float y, float fx, float fy)
	{
		float w = fx - x;
		float h = fy - y;
		float x1, y1, x2, y2, x3, y3;
		x1 = x;
		y2 = fy;
		if (axis == Axis.X) {
			x3 = fx;
			if (isIncr) {
				y1 = y;
				x2 = x;
				y3 = y + h / 2;
			} else {
				y1 = y + h / 2;
				x2 = fx;
				y3 = y;
			}
		} else {
			y3 = y;
			if (isIncr) {
				y1 = y;
				x2 = x + w / 2;
				x3 = fx;
			} else {
				y1 = fy;
				x2 = fx;
				x3 = x + w / 2;
			}
		}
		renderer.drawTriangle(x1, y1, x2, y2, x3, y3);
	}
	
}