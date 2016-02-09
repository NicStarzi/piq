package edu.udo.piq.components.popup;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PCheckBoxTuple;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PPopupCheckBox extends PCheckBoxTuple implements PPopupComponent {
	
	public static final PColor DEFAULT_HIGHLIGHT_COLOR = PColor.BLUE;
	
	protected final ObserverList<PPopupComponentObs> obsList
			= PCompUtil.createDefaultObserverList();
	private boolean highlighted;
	
	public PPopupCheckBox(Object labelModelValue) {
		this(new PPopupLabel(labelModelValue));
	}
	
	public PPopupCheckBox(PComponent secondComponent) {
		super(secondComponent);
	}
	
	public PPopupCheckBox() {
		super();
	}
	
	public void setSecondComponent(PComponent component) {
		super.setSecondComponent(component);
		highlightContent();
	}
	
	public void setHighlighted(boolean value) {
		if (highlighted != value) {
			highlighted = value;
			if (isEnabled()) {
				fireReRenderEvent();
			}
			highlightContent();
		}
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}
	
	protected void highlightContent() {
		PComponent scndComp = getSecondComponent();
		if (scndComp != null && scndComp instanceof PPopupComponent) {
			PPopupComponent popupContent = (PPopupComponent) scndComp;
			popupContent.setHighlighted(isHighlighted());
		}
	}
	
	public void defaultRender(PRenderer renderer) {
		if (isHighlighted()) {
			renderer.setColor(getHighlightColor());
			renderer.setRenderMode(renderer.getRenderModeFill());
			renderer.drawQuad(getBounds());
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return isHighlighted();
	}
	
	protected PColor getHighlightColor() {
		return DEFAULT_HIGHLIGHT_COLOR;
	}
	
	protected void onCheckBoxClick() {
		super.onCheckBoxClick();
		firePopupCloseEvent();
	}
	
	protected void onMouseMoved(PMouse mouse) {
		setHighlighted(isEnabled() && isMouseOverThisOrChild());
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		if (obsList != null && child instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) child;
			for (PPopupComponentObs obs : obsList) {
				popupChild.addObs(obs);
			}
		}
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		if (obsList != null && child instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) child;
			for (PPopupComponentObs obs : obsList) {
				popupChild.removeObs(obs);
			}
		}
	}
	
	public void addObs(PPopupComponentObs obs) {
		obsList.add(obs);
		if (getSecondComponent() != null 
				&& getSecondComponent() instanceof PPopupComponent) 
		{
			PPopupComponent popupChild = (PPopupComponent) getSecondComponent();
			popupChild.addObs(obs);
		}
	}
	
	public void removeObs(PPopupComponentObs obs) {
		if (getSecondComponent() != null 
				&& getSecondComponent() instanceof PPopupComponent) 
		{
			PPopupComponent popupChild = (PPopupComponent) getSecondComponent();
			popupChild.removeObs(obs);
		}
		obsList.remove(obs);
	}
	
	protected void firePopupCloseEvent() {
		obsList.fireEvent((obs) -> obs.onClosePopup(this));
	}
}