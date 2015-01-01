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
import edu.udo.piq.tools.AbstractPKeyboardObs;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.AbstractPMouseObs;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.PRenderUtil;

public class PButton extends AbstractPLayoutOwner {
	
	protected final List<PButtonObs> obsList = new CopyOnWriteArrayList<>();
	private final PKeyboardObs keyObs = new AbstractPKeyboardObs() {
		public void keyTriggered(PKeyboard keyboard, Key key) {
			if (!PCompUtil.hasFocus(PButton.this)) {
				return;
			}
			if (key == Key.ENTER) {
				model.setPressed(true);
			}
		}
		public void keyReleased(PKeyboard keyboard, Key key) {
			if (!PCompUtil.hasFocus(PButton.this)) {
				return;
			}
			if (key == Key.ENTER) {
				model.setPressed(false);
				fireClickEvent();
			}
		}
	};
	private final PMouseObs mouseObs = new AbstractPMouseObs() {
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && PCompUtil.isWithinClippedBounds(PButton.this, mouse.getX(), mouse.getY())) {
				model.setPressed(true);
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT) {
				model.setPressed(false);
				if (PCompUtil.isWithinClippedBounds(PButton.this, mouse.getX(), mouse.getY())) {
					PCompUtil.takeFocus(PButton.this);
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
	protected PButtonModel model = new DefaultPButtonModel();
	
	public PButton() {
		setLayout(new PCentricLayout(this));
		setModel(model);
	}
	
	public void setContent(PComponent component) {
		getLayout().clearChildren();
		getLayout().addChild(component, null);
	}
	
	public PComponent getContent() {
		return getLayout().getContent();
	}
	
	public PCentricLayout getLayout() {
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
		firePreferredSizeChangedEvent();
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
			PRenderUtil.strokeQuad(renderer, x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.BLACK);
			PRenderUtil.strokeBottom(renderer, x, y, fx, fy);
			PRenderUtil.strokeRight(renderer, x, y, fx, fy);
			renderer.setColor(PColor.WHITE);
			PRenderUtil.strokeTop(renderer, x, y, fx, fy);
			PRenderUtil.strokeLeft(renderer, x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
		if (PCompUtil.hasFocus(this)) {
			PInsets insets = getLayout().getInsets();
			int innerX = x + insets.getFromLeft();
			int innerY = y + insets.getFromTop();
			int innerFx = fx - insets.getFromRight();
			int innerFy = fy - insets.getFromBottom();
			renderer.setColor(PColor.GREY50);
			PRenderUtil.strokeQuad(renderer, innerX, innerY, innerFx, innerFy);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		PSize layoutSize = getLayout().getPreferredSize();
		int w = layoutSize.getWidth() + 8;
		int h = layoutSize.getHeight() + 8;
		return new ImmutablePSize(w, h);
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	protected PKeyboardObs getKeyboardObs() {
		return keyObs;
	}
	
	protected PMouseObs getMouseObs() {
		return mouseObs;
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