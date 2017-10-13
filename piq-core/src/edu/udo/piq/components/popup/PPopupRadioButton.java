package edu.udo.piq.components.popup;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PRadioButtonTuple;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PPopupRadioButton extends PRadioButtonTuple implements PPopupComponent {
	
	public static final PColor DEFAULT_HIGHLIGHT_COLOR = PColor.BLUE;
	
	protected final ObserverList<PPopupComponentObs> obsList
			= PiqUtil.createDefaultObserverList();
	private boolean highlighted;
	
	public PPopupRadioButton(Object labelModelValue) {
		this(new PPopupLabel(labelModelValue));
	}
	
	public PPopupRadioButton(PComponent secondComponent) {
		super(secondComponent);
	}
	
	public PPopupRadioButton() {
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
	
	protected void onRadioBtnClick() {
		super.onRadioBtnClick();
		firePopupCloseEvent();
	}
	
	protected void onMouseMoved(PMouse mouse) {
		setHighlighted(isEnabled() && isMouseOverThisOrChild());
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		if (child instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) child;
			obsList.forEach(obs -> popupChild.addObs(obs));
		}
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		if (child instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) child;
			obsList.forEach(obs -> popupChild.removeObs(obs));
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