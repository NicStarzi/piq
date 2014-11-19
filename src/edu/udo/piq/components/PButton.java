package edu.udo.piq.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPButtonModel;
import edu.udo.piq.layouts.PCentricLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.PCompUtil;

public class PButton extends AbstractPLayoutOwner {
	
	protected final List<PButtonObs> obsList = new CopyOnWriteArrayList<>();
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
	
	protected void onUpdate() {
		PMouse mouse = PCompUtil.getMouseOf(this);
		if (mouse == null) {
			model.setPressed(false);
			return;
		}
		PComponent mouseOwner = mouse.getOwner();
		if (mouseOwner != null && mouseOwner != this) {
			model.setPressed(false);
			return;
		}
		
		if (model.isPressed()) {
			if (!mouse.isPressed(MouseButton.LEFT)) {
				model.setPressed(false);
				mouse.setOwner(null);
				if (PCompUtil.isWithinClippedBounds(this, mouse.getX(), mouse.getY())) {
					fireClickEvent();
				}
			}
		} else {
			if (mouse.isTriggered(MouseButton.LEFT) 
					&& PCompUtil.isWithinClippedBounds(this, mouse.getX(), mouse.getY())) {
				model.setPressed(true);
				mouse.setOwner(this);
			}
		}
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
			renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		} else {
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
			renderer.setColor(PColor.WHITE);
			renderer.drawQuad(x + 0, y + 0, fx - 1, fy - 1);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
	}
	
	public PSize getDefaultPreferredSize() {
		PSize layoutSize = getLayout().getPreferredSize();
		int w = layoutSize.getWidth() + 8;
		int h = layoutSize.getHeight() + 8;
		return new ImmutablePSize(w, h);
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