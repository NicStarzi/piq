package edu.udo.piq.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPSplitPanelModel;
import edu.udo.piq.layouts.PSplitLayout;
import edu.udo.piq.layouts.PSplitLayout.Constraint;
import edu.udo.piq.layouts.PSplitLayout.Orientation;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.PCompUtil;

public class PSplitPanel extends AbstractPLayoutOwner {
	
	protected final PPanel slider;
	private final List<PSplitPanelObs> obsList = new CopyOnWriteArrayList<>();
	private final PSplitPanelModelObs modelObs = new PSplitPanelModelObs() {
		public void positionChanged(PSplitPanelModel model) {
			getLayout().setSplitPosition(model.getSplitPosition());
			fireReRenderEvent();
		}
	};
	protected PSplitPanelModel model;
	protected boolean pressed;
	
	public PSplitPanel() {
		setModel(new DefaultPSplitPanelModel());
		setLayout(new PSplitLayout(this));
		slider = new PPanel();
		getLayout().addChild(slider, Constraint.SLIDER);
	}
	
	protected void onUpdate() {
		PMouse mouse = PCompUtil.getMouseOf(this);
		if (mouse == null) {
			if (pressed) {
				pressed = false;
				fireSliderReleasedEvent();
			}
			return;
		}
		PComponent mouseOwner = mouse.getOwner();
		if (mouseOwner != null && mouseOwner != this) {
			if (pressed) {
				pressed = false;
				fireSliderReleasedEvent();
			}
			return;
		}
		if (pressed) {
			if (mouse.isPressed(MouseButton.LEFT)) {
				PBounds bounds = getBounds();
				int mousePos;
				double maxPos;
				if (getOrientation() == Orientation.HORIZONTAL) {
					mousePos = mouse.getX() - bounds.getX();
					maxPos = bounds.getWidth();
				} else {
					mousePos = mouse.getY() - bounds.getY();
					maxPos = bounds.getHeight();
				}
				double newPos = mousePos / maxPos;;
				getModel().setSplitPosition(newPos);
				fireSliderMovedEvent();
			} else {
				pressed = false;
				mouse.setOwner(null);
				fireSliderReleasedEvent();
			}
		} else {
			if (mouse.isTriggered(MouseButton.LEFT) 
					&& PCompUtil.isMouseContained(slider, PCompUtil.getClippedBoundsOf(slider))) {
				
				pressed = true;
				mouse.setOwner(this);
				fireSliderTouchedEvent();
			}
		}
	}
	
	public PSplitLayout getLayout() {
		return (PSplitLayout) super.getLayout();
	}
	
	public void setFirstComponent(PComponent component) {
		if (component == null) {
			getLayout().removeChild(Constraint.FIRST);
		} else {
			getLayout().addChild(component, Constraint.FIRST);
		}
	}
	
	public PComponent getFirstComponent() {
		return getLayout().getAt(Constraint.FIRST);
	}
	
	public void setSecondComponent(PComponent component) {
		if (component == null) {
			getLayout().removeChild(Constraint.SECOND);
		} else {
			getLayout().addChild(component, Constraint.SECOND);
		}
	}
	
	public PComponent getSecondComponent() {
		return getLayout().getAt(Constraint.SECOND);
	}
	
	public Orientation getOrientation() {
		return getLayout().getOrientation();
	}
	
	public double getSplitPosition() {
		return getLayout().getSplitPosition();
	}
	
	public void setModel(PSplitPanelModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		fireReRenderEvent();
	}
	
	public PSplitPanelModel getModel() {
		return model;
	}
	
	public void addObs(PSplitPanelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PSplitPanelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireSliderTouchedEvent() {
		for (PSplitPanelObs obs : obsList) {
			obs.sliderTouched(this);
		}
	}
	
	protected void fireSliderReleasedEvent() {
		for (PSplitPanelObs obs : obsList) {
			obs.sliderReleased(this);
		}
	}
	
	protected void fireSliderMovedEvent() {
		for (PSplitPanelObs obs : obsList) {
			obs.sliderMoved(this);
		}
	}
	
}