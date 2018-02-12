package edu.udo.piq.components.popup;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.layouts.PWrapLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;
import edu.udo.piq.util.ThrowException;

public class PMenuBar extends AbstractPLayoutOwner {
	
	protected final ObserverList<PMenuBarObs> obsList = PiqUtil.createDefaultObserverList();
	protected final PMenuBodyObs bodyObs = new PMenuBodyObs() {
		@Override
		public void onMenuItemAction(PMenuBody body, AbstractPMenuItem item, int itemIndex) {
			PMenuBar.this.onMenuItemAction(body, item, itemIndex);
		}
		@Override
		public void onCloseRequest(PMenuBody body) {
			PMenuBar.this.onMenuCloseRequet(body);
		}
	};
	protected PMenuBarItem activeItem = null;
	protected PMenuBody curBody = null;
	protected boolean armed = false;
	
	public PMenuBar() {
		super();
		setLayout(new PWrapLayout(this, ListAlignment.LEFT_TO_RIGHT));
		
		addObs(new PMouseObs() {
			@Override
			public void onMouseMoved(PMouse mouse) {
				PMenuBar.this.onMouseMoved(mouse);
			}
			@Override
			public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
				PMenuBar.this.onMouseButtonPressed(mouse, btn, clickCount);
			}
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PMenuBar.this.onMouseButtonTriggered(mouse, btn, clickCount);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PMenuBar.this.onMouseButtonReleased(mouse, btn, clickCount);
			}
		});
	}
	
	protected void onMouseMoved(PMouse mouse) {
		PComponent hoverComp = mouse.getComponentAtMouse();
		if (hoverComp == null) {
			if (!isArmed()) {
				clearActiveItem();
			}
			return;
		}
		PComponent itemComp = hoverComp.getAncestors().getNextMatching(anc -> anc.getParent() == PMenuBar.this);
		if (itemComp == null) {
			if (!isArmed()) {
				clearActiveItem();
			}
			return;
		}
		if (itemComp instanceof PMenuBarItem) {
			setActiveItem((PMenuBarItem) itemComp);
			getActiveItem().setMouseHover(true);
		}
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
		if (btn != MouseButton.LEFT) {
			return;
		}
		if (isMenuShown() && getMenuBodyComponent().isMouseOverThisOrChild(mouse)) {
			return;
		}
		PMenuBarItem item = getActiveItem();
		if (item != null && item.isMouseOverThisOrChild(mouse)) {
			fireItemClickEvent(item);
			setArmed(!isArmed());
		} else {
			setArmed(false);
		}
		onMouseMoved(mouse);
	}
	
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
		// intentionally left blank
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
		// intentionally left blank
	}
	
	protected void onMenuItemAction(PMenuBody body, AbstractPMenuItem item, int itemIndex) {
		hideMenu();
	}
	
	protected void onMenuCloseRequet(PMenuBody body) {
		hideMenu();
	}
	
	protected void setActiveItem(PMenuBarItem item) {
		if (getActiveItem() != item) {
			clearActiveItem();
			activeItem = item;
			getActiveItem().setArmed(isArmed());
			if (activeItem != null && isMenuShown()) {
				updateMenu();
			}
		}
	}
	
	protected PMenuBarItem getActiveItem() {
		return activeItem;
	}
	
	protected void clearActiveItem() {
		PMenuBarItem item = getActiveItem();
		if (item != null) {
			item.setMouseHover(false);
			item.setArmed(false);
			activeItem = null;
		}
	}
	
	protected void setArmed(boolean value) {
		if (getActiveItem() == null) {
			value = false;
		}
		if (armed != value) {
			armed = value;
			getActiveItem().setArmed(isArmed());
			fireArmedChangeEvent();
			if (isArmed() && !isMenuShown()) {
				showMenu();
			}
		}
		if (isMenuShown() && !isArmed()) {
			hideMenu();
		}
	}
	
	public boolean isArmed() {
		return armed;
	}
	
	public PMenuBody getMenuBodyComponent() {
		return curBody;
	}
	
	public boolean isMenuShown() {
		return getMenuBodyComponent() != null;
	}
	
	protected void updateMenu() {
		ThrowException.ifFalse(isMenuShown(), "isMenuShown() == false");
		curBody.getLayoutInternal().clearChildren();
		
		PMenuBarItem item = getActiveItem();
		PBounds itemBounds = item.getBounds();
		int itemX = itemBounds.getX();
		int itemY = itemBounds.getFinalY();
		
		for (int i = 0; i < item.getMenuItemCount(); i++) {
			curBody.addMenuItem(i, item.getMenuItem(i));
		}
		
		PRootOverlay overlay = curBody.getRoot().getOverlay();
		PBounds overlayBnds = overlay.getBounds();
		PSize menuPrefSize = curBody.getPreferredSize();
		
		int menuX = Math.min(itemX, overlayBnds.getFinalX() - menuPrefSize.getWidth());
		int menuY = Math.min(itemY, overlayBnds.getFinalY() - menuPrefSize.getHeight());
		menuX = Math.max(0, menuX);
		menuY = Math.max(0, menuY);
		
		overlay.getLayout().updateConstraint(curBody, new FreeConstraint(menuX, menuY));
	}
	
	protected void showMenu() {
		if (isMenuShown()) {
			return;
		}
		PRoot root = getRoot();
		if (root == null) {
			/*
			 * Can not add menu body without a root.
			 */
			return;
		}
		PMenuBarItem item = getActiveItem();
		PBounds itemBounds = item.getBounds();
		int itemX = itemBounds.getX();
		int itemY = itemBounds.getFinalY();
		
		curBody = new PMenuBody();
		curBody.addObs(bodyObs);
		for (int i = 0; i < item.getMenuItemCount(); i++) {
			curBody.addMenuItem(i, item.getMenuItem(i));
		}
		/*
		 * We add the popup temporarily so that any components that need a
		 * PRoot to correctly calculate their size (for example PLabels)
		 * can do so. We need this to calculate the correct position for
		 * the popup body.
		 */
		PRootOverlay overlay = root.getOverlay();
		PBounds overlayBnds = overlay.getBounds();
		overlay.getLayout().addChild(curBody, new FreeConstraint(itemX, itemY));
		
		PSize menuPrefSize = curBody.getPreferredSize();
		
		int menuX = Math.min(itemX, overlayBnds.getFinalX() - menuPrefSize.getWidth());
		int menuY = Math.min(itemY, overlayBnds.getFinalY() - menuPrefSize.getHeight());
		menuX = Math.max(0, menuX);
		menuY = Math.max(0, menuY);
		
		overlay.getLayout().updateConstraint(curBody, new FreeConstraint(menuX, menuY));
		root.setFocusOwner(null);
		fireMenuShownEvent(item);
		
		curBody.tryToTakeFocus();
	}
	
	protected void hideMenu() {
		if (isMenuShown()) {
			PMenuBody menuBodyComp = getMenuBodyComponent();
			menuBodyComp.getRoot().getOverlay().getLayout().removeChild(menuBodyComp);
			menuBodyComp.removeAllItems();
			curBody.removeObs(bodyObs);
			curBody = null;
			fireMenuHiddenEvent(activeItem, menuBodyComp);
			setArmed(false);
		}
	}
	
	public void addMenuItem(PMenuBarItem item) {
		ThrowException.ifNull(item, "item == null");
		ThrowException.ifNotNull(item.getParent(), "item.getParent() != null");
		getLayoutInternal().addChild(item, null);
	}
	
	public void removeMenuItem(PMenuBarItem item) {
		ThrowException.ifNull(item, "item == null");
		ThrowException.ifNotEqual(this, item.getParent(), "item.getParent() != this");
		getLayoutInternal().removeChild(item);
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(PColor.GREY75);
		renderer.drawQuad(getBoundsWithoutBorder());
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public void addObs(PMenuBarObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PMenuBarObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireArmedChangeEvent() {
		obsList.fireEvent(obs -> obs.onArmedChanged(this));
	}
	
	protected void fireItemClickEvent(PMenuBarItem item) {
		obsList.fireEvent(obs -> obs.onItemClicked(this, item));
	}
	
	protected void fireMenuShownEvent(PMenuBarItem item) {
		PMenuBody body = getMenuBodyComponent();
		obsList.fireEvent(obs -> obs.onMenuShown(this, item, body));
	}
	
	protected void fireMenuHiddenEvent(PMenuBarItem item, PMenuBody body) {
		obsList.fireEvent(obs -> obs.onMenuHidden(this, item, body));
	}
	
}