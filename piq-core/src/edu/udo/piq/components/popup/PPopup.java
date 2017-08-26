package edu.udo.piq.components.popup;

import java.util.List;

import edu.udo.piq.PBorder;
import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouse.VirtualMouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.borders.PBevelBorder;
import edu.udo.piq.components.containers.PListPanel;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class PPopup {
	
	public static final PPopupBorderProvider DEFAULT_BORDER_PROVIDER
		= (comp) -> new PBevelBorder();
	
	public static final PPopupBodyProvider DEFAULT_BODY_PROVIDER
		= (comp) -> new PListPanel();
	
	protected final ObserverList<PPopupObs> obsList
		= PCompUtil.createDefaultObserverList();
	protected final PMouseObs mouseObs = new PMouseObs() {
		@Override
		public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
			PPopup.this.onMouseTrigger(mouse, btn);
		}
	};
	protected final PPopupComponentObs compObs = (cmp) -> hidePopup();
	protected final PComponent owner;
	protected PPopupBodyProvider bodyProvider;
	protected PPopupBorderProvider borderProvider;
	protected PPopupOptionsProvider optionsProvider;
	protected PListPanel popupComp;
	protected boolean enabled;
	
	public PPopup(PComponent component) {
		owner = component;
		setBodyProvider(DEFAULT_BODY_PROVIDER);
		setBorderProvider(DEFAULT_BORDER_PROVIDER);
	}
	
	public PComponent getOwner() {
		return owner;
	}
	
	public void setBodyProvider(PPopupBodyProvider provider) {
		bodyProvider = provider;
	}
	
	public PPopupBodyProvider getBodyProvider() {
		return bodyProvider;
	}
	
	public void setBorderProvider(PPopupBorderProvider provider) {
		borderProvider = provider;
	}
	
	public PPopupBorderProvider getBorderProvider() {
		return borderProvider;
	}
	
	public void setOptionsProvider(PPopupOptionsProvider provider) {
		optionsProvider = provider;
	}
	
	public PPopupOptionsProvider getOptionsProvider() {
		return optionsProvider;
	}
	
	public void setEnabled(boolean isEnabled) {
		if (enabled != isEnabled) {
			enabled = isEnabled;
			if (enabled) {
				getOwner().addObs(mouseObs);
			} else {
				getOwner().removeObs(mouseObs);
			}
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isShown() {
		return popupComp != null;
	}
	
	protected void onMouseTrigger(PMouse mouse, MouseButton btn) {
		if (!isEnabled()) {
			return;
		}
		if (isShown() && popupComp.isMouseOverThisOrChild()) {
			return;
		}
		if (mouse.isPressed(VirtualMouseButton.POPUP_TRIGGER)
				&& getOwner().isMouseOverThisOrChild())
		{
			hidePopup();
			showPopup(mouse.getX(), mouse.getY());
		} else {
			hidePopup();
		}
	}
	
	protected void showPopup(int x, int y) {
		if (isShown()) {
			return;
		}
		PComponent owner = getOwner();
		PRoot root = owner.getRoot();
		if (root == null) {
			/*
			 * This should not be possible since the showPopup method
			 * is called from a mouse event.
			 * If this happened a virtual mouse event was somehow
			 * created by the user.
			 */
			return;
		}
		ThrowException.ifNull(getBodyProvider(), "getBodyProvider() == null");
		ThrowException.ifNull(getBorderProvider(), "getBorderProvider() == null");
		ThrowException.ifNull(getOptionsProvider(), "getPopupProvider() == null");
		
		PListPanel body = getBodyProvider().createBody(owner);
		if (body == null) {
			return;
		}
		List<PComponent> options = getOptionsProvider().createOptions(owner);
		if (options.isEmpty()) {
			return;
		}
		
		PListLayout listLayout = body.getLayout();
		listLayout.setAlignment(ListAlignment.TOP_TO_BOTTOM);
		listLayout.setInsets(new ImmutablePInsets(1));
		
		for (int i = 0; i < options.size(); i++) {
			PComponent optionsComp = options.get(i);
			body.addChild(optionsComp, Integer.valueOf(i));
			if (optionsComp instanceof PPopupComponent) {
				((PPopupComponent) optionsComp).addObs(compObs);
			}
		}
		PRootOverlay overlay = root.getOverlay();
		
		popupComp = body;
		PBorder border = getBorderProvider().createBorder(owner);
		if (border != null) {
			popupComp.setBorder(border);
		}
		/*
		 * We add the popup temporarily so that any components that need a
		 * PRoot to correctly calculate their size (for example PLabels)
		 * can do so. We need this to calculate the correct position for
		 * the popup.
		 */
		overlay.getLayout().addChild(popupComp, new FreeConstraint(x, y));
		
		PSize popupSize = popupComp.getPreferredSize();
		PBounds overlayBnds = overlay.getBounds();
		
		int popupX = Math.min(x, overlayBnds.getFinalX() - popupSize.getWidth());
		int popupY = Math.min(y, overlayBnds.getFinalY() - popupSize.getHeight());
		
		overlay.getLayout().updateConstraint(popupComp, new FreeConstraint(popupX, popupY));
		root.setFocusOwner(null);
		fireShowEvent();
	}
	
	protected void hidePopup() {
		if (isShown()) {
			for (PComponent optionsComp : popupComp.getChildren()) {
				if (optionsComp instanceof PPopupComponent) {
					((PPopupComponent) optionsComp).removeObs(compObs);
				}
			}
			popupComp.getRoot().getOverlay().getLayout().removeChild(popupComp);
			popupComp = null;
			fireHideEvent();
		}
	}
	
	public void addObs(PPopupObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PPopupObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireShowEvent() {
		obsList.fireEvent((obs) -> obs.onPopupShown(this, popupComp));
	}
	
	protected void fireHideEvent() {
		obsList.fireEvent((obs) -> obs.onPopupHidden(this, popupComp));
	}
	
}