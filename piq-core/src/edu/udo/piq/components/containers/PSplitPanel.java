package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.defaults.DefaultPSplitPanelModel;
import edu.udo.piq.layouts.PSplitLayout;
import edu.udo.piq.layouts.PSplitLayout.Constraint;
import edu.udo.piq.layouts.PSplitLayout.Orientation;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PSplitPanel extends AbstractPLayoutOwner {
	
	protected final PDivider divider;
	protected final ObserverList<PSplitPanelObs> obsList
		= PCompUtil.createDefaultObserverList();
	private final PMouseObs mouseObs = new PMouseObs() {
		public void onMouseMoved(PMouse mouse) {
			if (pressed) {
//				mouse.setCursor(PCursorType.SCROLL);
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
			}
		}
		public void onButtonReleased(PMouse mouse, MouseButton btn) {
			if (pressed && btn == MouseButton.LEFT) {
//				mouse.setCursor(PCursorType.NORMAL);
				pressed = false;
				fireDividerReleasedEvent();
			}
		}
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			if (!pressed && btn == MouseButton.LEFT && divider.isMouseOver()) {
//				mouse.setCursor(PCursorType.SCROLL);
				pressed = true;
				fireDividerTouchedEvent();
			}
		}
	};
	private final PSplitPanelModelObs modelObs = new PSplitPanelModelObs() {
		public void positionChanged(PSplitPanelModel model) {
			getLayoutInternal().setSplitPosition(model.getSplitPosition());
			fireReRenderEvent();
		}
	};
	protected PSplitPanelModel model;
	protected boolean pressed;
	
	public PSplitPanel() {
		super();
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PSplitPanelModel defaultModel = new DefaultPSplitPanelModel();
		if (modelFac != null) {
			defaultModel = (PSplitPanelModel) modelFac.getModelFor(this, defaultModel);
		}
		
		setModel(defaultModel);
		
		setLayout(new PSplitLayout(this));
		divider = new PDivider();
		getLayoutInternal().addChild(divider, Constraint.DIVIDER);
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
		return false;
	}
	
	protected PSplitLayout getLayoutInternal() {
		return (PSplitLayout) super.getLayout();
	}
	
	public void setFirstComponent(PComponent component) {
		if (component == null) {
			getLayoutInternal().removeChild(Constraint.FIRST);
		} else {
			getLayoutInternal().addChild(component, Constraint.FIRST);
		}
	}
	
	public PComponent getFirstComponent() {
		return getLayoutInternal().getChildForConstraint(Constraint.FIRST);
	}
	
	public void setSecondComponent(PComponent component) {
		if (component == null) {
			getLayoutInternal().removeChild(Constraint.SECOND);
		} else {
			getLayoutInternal().addChild(component, Constraint.SECOND);
		}
	}
	
	public PComponent getSecondComponent() {
		return getLayoutInternal().getChildForConstraint(Constraint.SECOND);
	}
	
	public void setOrientation(Orientation orientation) {
		getLayoutInternal().setOrientation(orientation);
	}
	
	public Orientation getOrientation() {
		return getLayoutInternal().getOrientation();
	}
	
	public void setSplitPosition(double value) {
		getLayoutInternal().setSplitPosition(value);
	}
	
	public double getSplitPosition() {
		return getLayoutInternal().getSplitPosition();
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