package edu.udo.piq.scroll2;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.components.PClickable;
import edu.udo.piq.components.PColoredShape;
import edu.udo.piq.components.PColoredShape.Shape;
import edu.udo.piq.layouts.Axis;
import edu.udo.piq.scroll2.PScrollBarLayout.ScrollBarPart;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ThrowException;

public class PScrollBar extends AbstractPLayoutOwner {
	
	protected final Axis axis;
	protected PColoredShape background = new PColoredShape(Shape.RECTANGLE, PColor.GREY50);
	protected PScrollBarKnob knob = new PScrollBarKnob();
	protected PScrollBarButton btnDecr;
	protected PScrollBarButton btnIncr;
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
		btnDecr = new PScrollBarButton(axis, false);
		btnIncr = new PScrollBarButton(axis, true);
		setLayout(new PScrollBarLayout(this));
		getLayoutInternal().addChild(background, ScrollBarPart.BACKGROUND);
		getLayoutInternal().addChild(btnDecr, ScrollBarPart.BTN_DECREMENT);
		getLayoutInternal().addChild(btnIncr, ScrollBarPart.BTN_INCREMENT);
		getLayoutInternal().addChild(knob, ScrollBarPart.KNOB);
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
	
	@Override
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
		if (!isDrag && btn == MouseButton.LEFT && knob.isMouseOver(mouse)) {
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
				&& background.isMouseOver(mouse) && !knob.isMouseOver(mouse)) {
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
		ThrowException.ifFalse(background.isMouseOver(mouse), "background.isMouseOver() == false");
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
}