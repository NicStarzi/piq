package edu.udo.piq.components.util;

import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.components.containers.PLineBorder;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class PPopup {
	
	protected final ObserverList<PPopupObs> obsList
		= PCompUtil.createDefaultObserverList();
	private final PMouseObs mouseObs = new PMouseObs() {
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			onMouseTrigger(mouse, btn);
		}
	};
	private final PComponent owner;
	private PPopupProvider provider;
	private PComponent popupComp;
	private boolean enabled;
	
	public PPopup(PComponent component) {
		owner = component;
	}
	
	public PComponent getOwner() {
		return owner;
	}
	
	public void setPopupProvider(PPopupProvider popupProvider) {
		provider = popupProvider;
	}
	
	public PPopupProvider getPopupProvider() {
		return provider;
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
	
	private void onMouseTrigger(PMouse mouse, MouseButton btn) {
		if (isShown() && popupComp.isMouseOverThisOrChild()) {
			return;
		}
		if (mouse.isPressed(MouseButton.POPUP_TRIGGER) 
				&& getOwner().isMouseOverThisOrChild()) 
		{
			hidePopup();
			showPopup(mouse.getX(), mouse.getY());
		} else {
			hidePopup();
		}
	}
	
	private void showPopup(int x, int y) {
		if (isShown()) {
			return;
		}
		PRoot root = getOwner().getRoot();
		if (root == null) {
			/*
			 * This should not be possible since the showPopup method
			 * is called from a mouse event.
			 * If this happened a virtual mouse event was somehow
			 * created by the user.
			 */
			return;
		}
		ThrowException.ifNull(getPopupProvider(), "getPopupProvider() == null");
		List<PComponent> options = getPopupProvider().createOptions(getOwner());
		
		PPanel listPnl = new PPanel();
		PListLayout listLayout = new PListLayout(listPnl);
		listLayout.setAlignment(ListAlignment.FROM_TOP);
		listLayout.setInsets(new ImmutablePInsets(1));
		listPnl.setLayout(listLayout);
		
		for (int i = 0; i < options.size(); i++) {
			listPnl.addChild(options.get(i), Integer.valueOf(i));
		}
		PRootOverlay overlay = root.getOverlay();
		
		popupComp = new PLineBorder(listPnl, 1);
		/*
		 * We add the popup temporarily so that any components that need a
		 * PRoot to correctly calculate their size (for example PLabels)
		 * can do so. We need this to calculate the correct position for
		 * the popup. 
		 */
		overlay.getLayout().addChild(popupComp, new FreeConstraint(x, y));
		
		PSize popupSize = PCompUtil.getPreferredSizeOf(popupComp);
		PBounds overlayBnds = overlay.getBounds();
		
		int popupX = Math.min(x, overlayBnds.getFinalX() - popupSize.getWidth());
		int popupY = Math.min(y, overlayBnds.getFinalY() - popupSize.getHeight());
		
		overlay.getLayout().updateConstraint(popupComp, new FreeConstraint(popupX, popupY));
		root.setFocusOwner(null);
		fireShowEvent();
	}
	
	private void hidePopup() {
		if (isShown()) {
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
		for (PPopupObs obs : obsList) {
			obs.onPopupShown(this, popupComp);
		}
	}
	
	protected void fireHideEvent() {
		for (PPopupObs obs : obsList) {
			obs.onPopupHidden(this, popupComp);
		}
	}
	
}