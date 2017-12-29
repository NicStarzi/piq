package edu.udo.piq.components.popup2;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouse.VirtualMouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.components.collections.PSelectionComponent;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

public class PPopup {
	
	public static final boolean DEFAULT_IS_ENABLED = true;
	
	protected final PMouseObs mouseObs = new PMouseObs() {
		@Override
		public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
			PPopup.this.onMouseTrigger(mouse, btn, clickCount);
		}
		@Override
		public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
			PPopup.this.onMousePress(mouse, btn, clickCount);
		}
		@Override
		public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
			PPopup.this.onMouseRelease(mouse, btn, clickCount);
		}
		@Override
		public void onMouseMoved(PMouse mouse) {
			PPopup.this.onMouseMove(mouse);
		}
	};
	protected final ObserverList<PPopupObs> obsList
		= PiqUtil.createDefaultObserverList();
	protected final PMenuBodyObs bodyObs = new PMenuBodyObs() {
		@Override
		public void onMenuItemAction(PMenuBody body, AbstractPMenuItem item, int itemIndex) {
			PPopup.this.onMenuItemAction(body, item, itemIndex);
		}
		@Override
		public void onCloseRequest(PMenuBody body) {
			PPopup.this.onMenuCloseRequet(body);
		}
	};
	protected final List<PComponent> items = new ArrayList<>();
	protected final PComponent owner;
	protected PMenuBody curBody = null;
	protected int popupClickX = Integer.MIN_VALUE;// no useful initial value
	protected int popupClickY = Integer.MIN_VALUE;// no useful initial value
	protected boolean enabled = false;
	
	public PPopup(PComponent component) {
		owner = component;
		setEnabled(DEFAULT_IS_ENABLED);
	}
	
	public PComponent getOwner() {
		return owner;
	}
	
	public Object getClickedOwnerContent() {
		if (!isShown()) {
			throw new IllegalStateException("This method can only be called while the popup is shown.");
		}
		PComponent owner = getOwner();
		if (owner instanceof PSelectionComponent) {
			PSelectionComponent collectionComp = (PSelectionComponent) owner;
			return collectionComp.getContentAt(getLastPopupClickX(), getLastPopupClickY());
		}
		return null;
	}
	
	public PMenuBody getPopupBodyComponent() {
		return curBody;
	}
	
	public int getLastPopupClickX() {
		return popupClickX;
	}
	
	public int getLastPopupClickY() {
		return popupClickY;
	}
	
	public void addItem(PComponentActionIndicator actionIndicator) {
		addItem(new PMenuItem(actionIndicator));
	}
	
	public void addItem(PComponent item) {
		ThrowException.ifNull(item, "item == null");
		ThrowException.ifIncluded(items, item, "items.contains(item) == true");
		items.add(item);
	}
	
	public void removeItem(AbstractPMenuItem item) {
		ThrowException.ifNull(item, "item == null");
		ThrowException.ifExcluded(items, item, "items.contains(item) == false");
		items.remove(item);
	}
	
	public void setEnabled(boolean isEnabled) {
		if (enabled != isEnabled) {
			enabled = isEnabled;
			if (isEnabled()) {
				getOwner().addObs(mouseObs);
			} else {
				getOwner().removeObs(mouseObs);
				if (isShown()) {
					hidePopup();
				}
			}
		}
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public boolean isShown() {
		return getPopupBodyComponent() != null;
	}
	
	protected void onMouseTrigger(PMouse mouse, MouseButton btn, int clickCount) {
		if (!isEnabled()) {
			return;
		}
		if (isShown() && getPopupBodyComponent().isMouseOverThisOrChild()) {
			return;
		}
		if (mouse.isTriggered(VirtualMouseButton.POPUP_TRIGGER)
				&& getOwner().isMouseOverThisOrChild())
		{
			hidePopup();
			showPopup(mouse.getX(), mouse.getY());
		} else {
			hidePopup();
		}
	}
	
	protected void onMousePress(PMouse mouse, MouseButton btn, int clickCount) {
		// intentionally left blank. Can be overwritten by subclasses as needed.
	}
	
	protected void onMouseRelease(PMouse mouse, MouseButton btn, int clickCount) {
		// intentionally left blank. Can be overwritten by subclasses as needed.
	}
	
	protected void onMouseMove(PMouse mouse) {
		// intentionally left blank. Can be overwritten by subclasses as needed.
	}
	
	protected void onMenuItemAction(PMenuBody body, AbstractPMenuItem item, int itemIndex) {
		hidePopup();
	}
	
	protected void onMenuCloseRequet(PMenuBody body) {
		hidePopup();
	}
	
	protected void showPopup(int x, int y) {
		if (isShown()) {
			return;
		}
		popupClickX = x;
		popupClickY = y;
		PComponent owner = getOwner();
		PRoot root = owner.getRoot();
		if (root == null) {
			/*
			 * This should not be possible since the showPopup method
			 * is called from a mouse event.
			 * If we reach this line of code, a virtual mouse event must 
			 * have been created by the user.
			 * We do nothing in this case because there is nothing to do.
			 */
			return;
		}
		curBody = new PMenuBody();
		curBody.addObs(bodyObs);
		for (int i = 0; i < items.size(); i++) {
			curBody.addMenuItem(i, items.get(i));
		}
		/*
		 * We add the popup temporarily so that any components that need a
		 * PRoot to correctly calculate their size (for example PLabels)
		 * can do so. We need this to calculate the correct position for
		 * the popup body.
		 */
		PRootOverlay overlay = root.getOverlay();
		overlay.getLayout().addChild(curBody, new FreeConstraint(x, y));
		
		PSize popupSize = curBody.getPreferredSize();
		PBounds overlayBnds = overlay.getBounds();
		
		int popupX = Math.min(x, overlayBnds.getFinalX() - popupSize.getWidth());
		int popupY = Math.min(y, overlayBnds.getFinalY() - popupSize.getHeight());
		popupX = Math.max(0, popupX);
		popupY = Math.max(0, popupY);
		
		overlay.getLayout().updateConstraint(curBody, new FreeConstraint(popupX, popupY));
		root.setFocusOwner(null);
		fireShowEvent();
		
		curBody.tryToTakeFocus();
	}
	
	protected void hidePopup() {
		if (isShown()) {
			PMenuBody popupBodyComp = getPopupBodyComponent();
			popupBodyComp.getRoot().getOverlay().getLayout().removeChild(popupBodyComp);
			popupBodyComp.removeAllItems();
			curBody.removeObs(bodyObs);
			curBody = null;
			fireHideEvent(popupBodyComp);
		}
	}
	
	public void addObs(PPopupObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PPopupObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireShowEvent() {
		PComponent popupBody = getPopupBodyComponent();
		int clickX = getLastPopupClickX();
		int clickY = getLastPopupClickY();
		obsList.fireEvent((obs) -> obs.onPopupShown(this, clickX, clickY, popupBody));
	}
	
	protected void fireHideEvent(PComponent popupBody) {
		int clickX = getLastPopupClickX();
		int clickY = getLastPopupClickY();
		obsList.fireEvent((obs) -> obs.onPopupHidden(this, clickX, clickY, popupBody));
	}
	
}