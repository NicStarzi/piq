package edu.udo.piq.scroll2;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;
import edu.udo.piq.components.PClickObs;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.components.PColoredShape;
import edu.udo.piq.components.PColoredShape.Shape;
import edu.udo.piq.layouts.AbstractEnumPLayout;
import edu.udo.piq.layouts.Axis;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

public class PScrollBar extends AbstractPLayoutOwner {
	
	protected final Axis axis;
	protected PColoredShape background = new PColoredShape(Shape.RECTANGLE, PColor.GREY50);
	protected PScrollKnob knob = new PScrollKnob();
	protected PScrollButton btnDecr;
	protected PScrollButton btnIncr;
	protected int scroll = 0;
	protected int scrollSpeed = 4;
	protected int bodyPrefSize;
	protected int bodyActualSize;
	protected int dragOffset = 0;
	protected boolean isDrag = false;
	
	public PScrollBar(Axis axis) {
		super();
		ThrowException.ifNull(axis, "axis == null");
		this.axis = axis;
		btnDecr = new PScrollButton(axis, false);
		btnIncr = new PScrollButton(axis, true);
		setLayout(new PScrollBarLayout(this));
		getLayoutInternal().addChild(background, Part.BACKGROUND);
		getLayoutInternal().addChild(btnDecr, Part.BTN_DECREMENT);
		getLayoutInternal().addChild(btnIncr, Part.BTN_INCREMENT);
		getLayoutInternal().addChild(knob, Part.KNOB);
		addObs(new PMouseObs() {
			@Override
			public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
				PScrollBar.this.onMousePress(mouse, btn);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PScrollBar.this.onMouseRelease(mouse, btn);
			}
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PScrollBar.this.onMouseTrigger(mouse, btn, clickCount);
			}
			@Override
			public void onMouseMoved(PMouse mouse) {
				PScrollBar.this.onMouseMove(mouse);
			}
		});
		btnDecr.addObs(this::onScrollButtonClick);
		btnIncr.addObs(this::onScrollButtonClick);
	}
	
	protected PScrollBarLayout getLayoutInternal() {
		return (PScrollBarLayout) super.getLayout();
	}
	
	@Override
	protected void onParentChanged(PComponent oldParent) {
		setScroll(0);
	}
	
	protected void setBodyPrefSize(int value) {
		if (bodyPrefSize != value) {
			bodyPrefSize = value;
			setScroll(getScroll());
			fireReLayOutEvent();
			fireReRenderEvent();
		}
	}
	
	protected int getBodyPrefSize() {
		return bodyPrefSize;
	}
	
	protected void setBodyActualSize(int value) {
		if (bodyActualSize != value) {
			bodyActualSize = value;
			setScroll(getScroll());
			fireReLayOutEvent();
			fireReRenderEvent();
		}
	}
	
	protected int getBodyActualSize() {
		return bodyActualSize;
	}
	
	protected void onMousePress(PMouse mouse, MouseButton btn) {
		if (!isDrag && btn == MouseButton.LEFT && knob.isMouseOver()) {
			isDrag = true;
			dragOffset = mouse.getOffsetToComponent(knob, getAxis());
		}
	}
	
	protected void onMouseRelease(PMouse mouse, MouseButton btn) {
		if (isDrag && btn == MouseButton.LEFT) {
			isDrag = false;
			dragOffset = 0;
		}
	}
	
	protected void onMouseTrigger(PMouse mouse, MouseButton btn, int clickCount) {
		if (!isDrag && btn == MouseButton.LEFT && clickCount == 2
				&& background.isMouseOver() && !knob.isMouseOver()) {
			double scrollPercent = getMousePosPercent(mouse);
			setScrollPercent(scrollPercent);
		}
	}
	
	protected void onMouseMove(PMouse mouse) {
		if (isDrag) {
			Axis axis = getAxis();
			PBounds scrollBounds = background.getBounds();
			int pos = (axis.getCoordinate(mouse) - axis.getFirstCoordinate(scrollBounds)) - dragOffset;
			int size = axis.getSize(scrollBounds) - axis.getSize(knob);
			double scrollPercent = pos / (double) size;
			setScrollPercent(scrollPercent);
		}
	}
	
	protected void onScrollButtonClick(PClickable button) {
		int scrollAmount = scrollSpeed;
		if (getKeyboard().isModifierToggled(Modifier.CTRL)) {
			scrollAmount *= 2;
		}
		if (button == btnDecr) {
			setScroll(getScroll() - scrollAmount);
		} else if (button == btnIncr) {
			setScroll(getScroll() + scrollAmount);
		}
	}
	
	protected double getMousePosPercent(PMouse mouse) {
		ThrowException.ifFalse(background.isMouseOver(), "background.isMouseOver() == false");
		PBounds scrollBounds = background.getBounds();
		PBounds knobBounds = knob.getBounds();
		Axis axis = getAxis();
		int offset = (axis.getCoordinate(mouse) - (axis.getSize(knobBounds) / 2)) - axis.getFirstCoordinate(scrollBounds);
		int size = axis.getSize(scrollBounds) - axis.getSize(knobBounds);
		return offset / (double) size;
	}
	
	public void setScrollPercent(double value) {
		setScroll((int) (getMaxScroll() * value + 0.5));
	}
	
	public void setScroll(int value) {
		int maxVal = getMaxScroll();
		if (value < 0) {
			value = 0;
		} else if (value > maxVal) {
			value = maxVal;
		}
		if (scroll != value) {
			scroll = value;
			fireReLayOutEvent();
			fireReRenderEvent();
			
			PScrollPanel pnl = getPanel();
			if (pnl != null) {
				pnl.onScrollChanged(getAxis());
			}
		}
	}
	
	public int getScroll() {
		return scroll;
	}
	
	public double getScrollPercent() {
		return scroll / (double) getMaxScroll();
	}
	
	public int getMaxScroll() {
		PScrollPanel scrollPnl = getPanel();
		if (scrollPnl == null) {
			return 0;
		}
		if (scrollPnl.getBody() == null) {
			return 0;
		}
		if (bodyActualSize >= bodyPrefSize) {
			return 0;
		}
		return bodyPrefSize - bodyActualSize;
	}
	
	public Axis getAxis() {
		return axis;
	}
	
	protected PScrollPanel getPanel() {
		return (PScrollPanel) getParent();
	}
	
	public static class PScrollButton extends AbstractPComponent implements PClickable {
		
		public static final double DEFAULT_REPEAT_TIMER_DELAY = 25;
		public static final double DEFAULT_REPEAT_TIMER_INITIAL_DELAY = 250;
		
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
		
		protected final ObserverList<PClickObs> obsList
			= PiqUtil.createDefaultObserverList();
		protected final PTimer repeatTimer;
		protected final Axis scrollBarAxis;
		protected final boolean thisIsIncr;
		protected double repeatTimerInitialDelay = DEFAULT_REPEAT_TIMER_INITIAL_DELAY;
		protected double repeatTimerDelay = DEFAULT_REPEAT_TIMER_DELAY;
		protected boolean pressed = false;
		
		public PScrollButton(Axis axis, boolean isIncrementButton) {
			scrollBarAxis = axis;
			thisIsIncr = isIncrementButton;
			addObs(new PMouseObs() {
				@Override
				public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
					PScrollButton.this.onMouseButtonTriggered(mouse, btn);
				}
				@Override
				public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
					PScrollButton.this.onMouseButtonReleased(mouse, btn);
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
				repeatTimer.setStarted(pressed);
			}
			fireReRenderEvent();
		}
		
		protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && isMouseOver()) {
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
			if (repeatTimer.isStarted()) {
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
			PScrollButton.renderTriangle(renderer, scrollBarAxis, thisIsIncr, x + 4, y + 4, fx - 4, fy - 4);
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
			prefSize.set(PScrollKnob.DEFAULT_SIZE);
			return prefSize;
		}
		
		public void addObs(PClickObs obs) {
			obsList.add(obs);
		}
		
		public void removeObs(PClickObs obs) {
			obsList.remove(obs);
		}
		
		protected void fireClickEvent() {
			obsList.fireEvent(obs -> obs.onClick(this));
		}
		
	}
	
	public static class PScrollKnob extends AbstractPComponent {
		
		public static final PSize DEFAULT_SIZE = new ImmutablePSize(18, 18);
		
		@Override
		public void defaultRender(PRenderer renderer) {
			PBounds bnds = getBounds();
			int x = bnds.getX();
			int y = bnds.getY();
			int w = bnds.getWidth();
			int h = bnds.getHeight();
			
			renderer.setRenderMode(renderer.getRenderModeFill());
			renderer.setColor(PColor.GREY75);
			renderer.drawEllipse(x + 2, y + 2, w - 4, h - 4);
			renderer.setRenderMode(renderer.getRenderModeOutline());
			renderer.setColor(PColor.WHITE);
			renderer.drawEllipse(x + 1, y + 1, w - 3, h - 3);
			renderer.setColor(PColor.BLACK);
			renderer.drawEllipse(x, y, w - 1, h - 1);
		}
		
		@Override
		protected PSize getConstantDefaultPreferredSize() {
			return DEFAULT_SIZE;
		}
		
		@Override
		public boolean defaultFillsAllPixels() {
			return false;
		}
		
	}
	
	public class PScrollBarLayout extends AbstractEnumPLayout<Part> {
		
		protected PScrollBarLayout(PComponent component) {
			super(component, Part.class);
		}
		
		@Override
		protected void onInvalidated() {
			prefSize.set(PScrollKnob.DEFAULT_SIZE);
		}
		
		@Override
		protected void layOutInternal() {
			PBounds ob = getOwner().getBoundsWithoutBorder();
			int x = ob.getX();
			int y = ob.getY();
			int w = ob.getWidth();
			int h = ob.getHeight();
			int fx = x + w;
			int fy = y + h;
			
			int bgX = x;
			int bgY = y;
			int bgW = w;
			int bgH = h;
			
			int knobX;
			int knobY;
			PComponent knob = getChildForConstraint(Part.KNOB);
			PSize knobSize = getPreferredSizeOf(knob);
			int knobW = knobSize.getWidth();
			int knobH = knobSize.getHeight();
			
			PComponent btnUp = getChildForConstraint(Part.BTN_DECREMENT);
			PComponent btnDown = getChildForConstraint(Part.BTN_INCREMENT);
			PSize btnUpSize = getPreferredSizeOf(btnUp);
			int btnW;
			int btnH;
			int btnDownX;
			int btnDownY;
			double scrollPercent = getScrollPercent();
			if (getAxis() == Axis.X) {
				btnW = btnUpSize.getWidth();
				btnH = h;
				btnDownX = fx - btnW;
				btnDownY = y;
				
				bgX += btnW;
				bgW -= btnW * 2;
				
				knobX = x + btnW + knobW / 2 + (int) ((bgW - knobW) * scrollPercent) - knobW / 2;
				knobY = y;
			} else {
				btnW = w;
				btnH = btnUpSize.getHeight();
				btnDownX = x;
				btnDownY = fy - btnH;
				
				bgY += btnH;
				bgH -= btnH * 2;
				
				knobX = x;
				knobY = y + btnH + knobH / 2 + (int) ((bgH - knobH) * scrollPercent) - knobH / 2;
			}
			setChildCellFilled(btnUp, x, y, btnW, btnH);
			setChildCellFilled(btnDown, btnDownX, btnDownY, btnW, btnH);
			
			PComponent bg = getChildForConstraint(Part.BACKGROUND);
			setChildCellFilled(bg, bgX, bgY, bgW, bgH);
			
			setChildCellFilled(knob, knobX, knobY, knobW, knobH);
		}
		
	}
	
	private static enum Part {
		BTN_DECREMENT, BTN_INCREMENT, KNOB, BACKGROUND,
		;
	}
}