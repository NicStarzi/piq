package edu.udo.piq.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;

public class PButton extends AbstractPLayoutOwner {
	
	protected final List<PButtonObs> obsList = new CopyOnWriteArrayList<>();
	private final PKeyboardObs keyObs = new PKeyboardObs() {
		public void keyTriggered(PKeyboard keyboard, Key key) {
			if (!hasFocus()) {
				return;
			}
			if (key == Key.ENTER) {
				model.setPressed(true);
			}
		}
		public void keyReleased(PKeyboard keyboard, Key key) {
			if (!hasFocus()) {
				return;
			}
			if (key == Key.ENTER && model.isPressed()) {
				model.setPressed(false);
				fireClickEvent();
			}
		}
	};
	private final PMouseObs mouseObs = new PMouseObs() {
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && isMouseOverThisOrChild()) {
				model.setPressed(true);
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT) {
				boolean oldPressed = model.isPressed();
				model.setPressed(false);
				if (oldPressed && isMouseOverThisOrChild()) {
					takeFocus();
					fireClickEvent();
				}
			}
		}
	};
	protected final PButtonModelObs modelObs = new PButtonModelObs() {
		public void onChange(PButtonModel model) {
			fireReRenderEvent();
		}
	};
	protected PButtonModel model;
	
	public PButton() {
		super();
		PCentricLayout defaultLayout = new PCentricLayout(this);
		defaultLayout.setInsets(new ImmutablePInsets(8));
		setLayout(defaultLayout);
		setModel(new DefaultPButtonModel());
		addObs(keyObs);
		addObs(mouseObs);
	}
	
	public void setContent(PComponent component) {
		getLayoutInternal().clearChildren();
		getLayoutInternal().addChild(component, null);
	}
	
	public PComponent getContent() {
		return getLayoutInternal().getContent();
	}
	
//	public PCentricLayout getLayout() {
//		return (PCentricLayout) super.getLayout();
//	}
	
	protected PCentricLayout getLayoutInternal() {
		return (PCentricLayout) super.getLayout();
	}
	
	public void setModel(PButtonModel model) {
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
	
	public boolean isPressed() {
		if (getModel() == null) {
			return false;
		}
		return getModel().isPressed();
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		
		if (isPressed()) {
			renderer.setColor(PColor.GREY25);
			renderer.strokeQuad(x, y, fx, fy);
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
			int innerX = x + insets.getFromLeft();
			int innerY = y + insets.getFromTop();
			int innerFx = fx - insets.getFromRight();
			int innerFy = fy - insets.getFromBottom();
			renderer.setColor(PColor.GREY50);
			renderer.strokeQuad(innerX, innerY, innerFx, innerFy);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		return getLayout().getPreferredSize();
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	public void addObs(PButtonObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PButtonObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireClickEvent() {
		for (PButtonObs obs : obsList) {
			obs.onClick(this);
		}
	}
	
}