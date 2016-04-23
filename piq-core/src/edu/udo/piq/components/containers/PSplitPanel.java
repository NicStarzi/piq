package edu.udo.piq.components.containers;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPSplitPanelModel;
import edu.udo.piq.layouts.PSplitLayout;
import edu.udo.piq.layouts.PSplitLayout.Constraint;
import edu.udo.piq.layouts.PSplitLayout.Orientation;
import edu.udo.piq.tools.AbstractPComponent;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePSize;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PSplitPanel extends AbstractPLayoutOwner {
	
	protected final PSplitPanelDivider divider;
	protected final ObserverList<PSplitPanelObs> obsList
		= PCompUtil.createDefaultObserverList();
	protected final PMouseObs mouseObs = new PMouseObs() {
		public void onMouseMoved(PMouse mouse) {
			PSplitPanel.this.onMouseMoved(mouse);
		}
		public void onButtonReleased(PMouse mouse, MouseButton btn) {
			PSplitPanel.this.onMouseButtonReleased(mouse, btn);
		}
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			PSplitPanel.this.onMouseButtonTriggered(mouse, btn);
		}
	};
	protected final PSplitPanelModelObs modelObs = (mdl) -> PSplitPanel.this.onModelChange();
	protected PSplitPanelModel model;
	protected boolean pressed;
	
	public PSplitPanel(Orientation orientation, PComponent first, PComponent second) {
		this(orientation);
		setFirstComponent(first);
		setSecondComponent(second);
	}
	
	public PSplitPanel(PComponent first, PComponent second) {
		this();
		setFirstComponent(first);
		setSecondComponent(second);
	}
	
	public PSplitPanel(Orientation orientation) {
		this();
		setOrientation(orientation);
	}
	
	public PSplitPanel() {
		super();
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PSplitPanelModel defaultModel = new DefaultPSplitPanelModel();
		if (modelFac != null) {
			defaultModel = (PSplitPanelModel) modelFac.getModelFor(this, defaultModel);
		}
		
		setModel(defaultModel);
		
		setLayout(new PSplitLayout(this));
		divider = new PSplitPanelDivider();
		getLayoutInternal().addChild(divider, Constraint.DIVIDER);
		addObs(mouseObs);
	}
	
	public void defaultRender(PRenderer renderer) {
//		PComponent first = getFirstComponent();
//		if (first == null || !PCompUtil.fillsAllPixels(first)) {
//			// This is faster then getting the bounds directly from the component 
//			// because it is just an array lookup with the constraints ordinal as 
//			// array-index.
//			PBounds bndsFirst = getLayoutInternal().getChildBounds(Constraint.FIRST);
//			defaultRenderFillBounds(renderer, bndsFirst);
//		}
//		PComponent second = getSecondComponent();
//		if (second == null || !PCompUtil.fillsAllPixels(second)) {
//			// see above
//			PBounds bndsSecond = getLayoutInternal().getChildBounds(Constraint.SECOND);
//			defaultRenderFillBounds(renderer, bndsSecond);
//		}
		if (!PCompUtil.fillsAllPixels(divider)) {
			// see above
			PBounds bndsDivider = getLayoutInternal().getChildBounds(Constraint.DIVIDER);
			defaultRenderFillBounds(renderer, bndsDivider);
		}
		defaultRenderFillBounds(renderer, getBounds());
	}
	
	private void defaultRenderFillBounds(PRenderer renderer, PBounds bnds) {
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(bnds);
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
		obsList.fireEvent((obs) -> obs.onDividerTouched(this));
	}
	
	protected void fireDividerReleasedEvent() {
		obsList.fireEvent((obs) -> obs.onDividerReleased(this));
	}
	
	protected void fireDividerMovedEvent() {
		obsList.fireEvent((obs) -> obs.onDividerMoved(this));
	}
	
	protected void onModelChange() {
		getLayoutInternal().setSplitPosition(model.getSplitPosition());
		fireReRenderEvent();
	}
	
	protected void onMouseMoved(PMouse mouse) {
		if (pressed) {
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
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		if (pressed && btn == MouseButton.LEFT) {
			pressed = false;
			fireDividerReleasedEvent();
		}
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
		if (!pressed && btn == MouseButton.LEFT && divider.isMouseOver()) {
			pressed = true;
			fireDividerTouchedEvent();
		}
	}
	
	public static class PSplitPanelDivider extends AbstractPComponent {
		
		private static final PSize DEFAULT_PREFERRED_SIZE = new ImmutablePSize(6, 6);
		
		public PCursor getMouseOverCursor(PMouse mouse) {
			return mouse.getCursorScroll();
		}
		
		public void defaultRender(PRenderer renderer) {
			PBounds bounds = getBounds();
			int x = bounds.getX();
			int y = bounds.getY();
			int fx = bounds.getFinalX();
			int fy = bounds.getFinalY();
			
			renderer.setColor(PColor.WHITE);
			renderer.strokeTop(x, y, fx, fy);
			renderer.strokeLeft(x, y, fx, fy);
			renderer.setColor(PColor.BLACK);
			renderer.strokeRight(x, y, fx, fy);
			renderer.strokeBottom(x, y, fx, fy);
			renderer.setColor(PColor.GREY75);
			renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		}
		
		public PSize getDefaultPreferredSize() {
			return DEFAULT_PREFERRED_SIZE;
		}
		
		public boolean isFocusable() {
			return false;
		}
		
	}
	
}