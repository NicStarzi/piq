package edu.udo.piq.components.popup;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.PButton;
import edu.udo.piq.components.PIconLabel;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PPopupButton_BACK extends PButton implements PPopupComponent {
	
	public static final PColor DEFAULT_HIGHLIGHT_COLOR = PColor.BLUE;
	
	protected final ObserverList<PPopupComponentObs> obsList
			= PCompUtil.createDefaultObserverList();
	private boolean highlighted;
	
	public PPopupButton_BACK(Object iconImgID, Object labelModelValue) {
		this(new PIconLabel(new PPicture(iconImgID), 
				new PPopupLabel(labelModelValue)));
	}
	
	public PPopupButton_BACK(Object labelModelValue) {
		this(new PPopupLabel(labelModelValue));
	}
	
	public PPopupButton_BACK(PPopupComponent content) {
		this();
		setContent(content);
	}
	
	public PPopupButton_BACK() {
		super();
		getLayoutInternal().setInsets(new ImmutablePInsets(1));
	}
	
	protected void highlightContent() {
		PComponent content = getContent();
		if (content != null && content instanceof PPopupComponent) {
			PPopupComponent popupContent = (PPopupComponent) content;
			popupContent.setHighlighted(isHighlighted());
		}
	}
	
	public void setContent(PComponent component) {
		super.setContent(component);
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
	
	protected void fireClickEvent() {
		super.fireClickEvent();
		firePopupCloseEvent();
	}
	
	protected void onMouseMoved(PMouse mouse) {
		setHighlighted(isEnabled() && isMouseOverThisOrChild());
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		if (child instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) child;
			for (PPopupComponentObs obs : obsList) {
				popupChild.addObs(obs);
			}
		}
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		if (child instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) child;
			for (PPopupComponentObs obs : obsList) {
				popupChild.removeObs(obs);
			}
		}
	}
	
	public void addObs(PPopupComponentObs obs) {
		obsList.add(obs);
		if (getContent() != null && getContent() instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) getContent();
			popupChild.addObs(obs);
		}
	}
	
	public void removeObs(PPopupComponentObs obs) {
		if (getContent() != null && getContent() instanceof PPopupComponent) {
			PPopupComponent popupChild = (PPopupComponent) getContent();
			popupChild.removeObs(obs);
		}
		obsList.remove(obs);
	}
	
	protected void firePopupCloseEvent() {
		obsList.fireEvent((obs) -> obs.onClosePopup(this));
	}
	
}