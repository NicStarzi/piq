package edu.udo.piq.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPSplitPanelModel;
import edu.udo.piq.layouts.PSplitLayout;
import edu.udo.piq.layouts.PSplitLayout.Constraint;
import edu.udo.piq.layouts.PSplitLayout.Orientation;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PSplitPanel extends AbstractPLayoutOwner {
	
	protected final PDivider divider;
	private final List<PSplitPanelObs> obsList = new CopyOnWriteArrayList<>();
	private final PMouseObs mouseObs = new PMouseObs() {
		public void mouseMoved(PMouse mouse) {
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
					fireDividerMovedEvent();
				} else {
					pressed = false;
					fireDividerReleasedEvent();
				}
			}
		}
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (btn == MouseButton.LEFT && divider.isMouseWithinClippedBounds()) {
				pressed = true;
				fireDividerTouchedEvent();
			}
		}
	};
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
		divider = new PDivider();
		getLayout().addChild(divider, Constraint.DIVIDER);
		addObs(mouseObs);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bnds = getBounds();
		int x = bnds.getX();
		int y = bnds.getY();
		int fx = bnds.getFinalX();
		int fy = bnds.getFinalY();
		renderer.setColor(PColor.BLACK);
		renderer.drawQuad(x, y, fx, fy);
	}
	
	public boolean isFocusable() {
		return true;
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
	
	protected void fireDividerTouchedEvent() {
		for (PSplitPanelObs obs : obsList) {
			obs.dividerTouched(this);
		}
	}
	
	protected void fireDividerReleasedEvent() {
		for (PSplitPanelObs obs : obsList) {
			obs.dividerReleased(this);
		}
	}
	
	protected void fireDividerMovedEvent() {
		for (PSplitPanelObs obs : obsList) {
			obs.dividerMoved(this);
		}
	}
	
}