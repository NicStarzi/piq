package edu.udo.piq.components;

import edu.udo.piq.PColor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.util.PPopupComponent;
import edu.udo.piq.components.util.PPopupComponentObs;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PPopupButton extends PButton implements PPopupComponent {
	
	public static final PColor DEFAULT_HIGHLIGHT_COLOR = PColor.BLUE;
	
	protected final ObserverList<PPopupComponentObs> obsList
			= PCompUtil.createDefaultObserverList();
	private boolean highlighted;
	
	public PPopupButton() {
		super();
		getLayoutInternal().setInsets(new ImmutablePInsets(1));
	}
	
	protected void setHighlighted(boolean value) {
		if (highlighted != value) {
			highlighted = value;
			if (isEnabled()) {
				fireReRenderEvent();
			}
		}
	}
	
	public boolean isHighlighted() {
		return highlighted;
	}
	
	public void defaultRender(PRenderer renderer) {
		if (isHighlighted()) {
			PColor highlightColor = getHighlightColor();
			renderer.setColor(highlightColor);
			renderer.setRenderMode(renderer.getRenderModeFill());
			renderer.drawQuad(getBounds());
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	protected PColor getHighlightColor() {
		return DEFAULT_HIGHLIGHT_COLOR;
	}
	
	protected void fireClickEvent() {
		super.fireClickEvent();
		firePopupCloseEvent();
	}
	
	public void addObs(PPopupComponentObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PPopupComponentObs obs) {
		obsList.remove(obs);
	}
	
	protected void firePopupCloseEvent() {
		obsList.fireEvent((obs) -> obs.onClosePopup(this));
	}
	
	protected void onMouseMoved(PMouse mouse) {
		setHighlighted(isMouseOverThisOrChild());
	}
	
}