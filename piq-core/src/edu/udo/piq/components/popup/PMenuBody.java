package edu.udo.piq.components.popup;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.popup.AbstractPMenuItem.MenuEntryPart;
import edu.udo.piq.components.popup.DelegationPGridListLayout.PDelegateRowIndex;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePInsets;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public class PMenuBody extends AbstractPLayoutOwner {
	
	public static final PInsets DEFAULT_INSETS = new ImmutablePInsets(1);
	
	protected final ObserverList<PMenuBodyObs> obsList
		= PiqUtil.createDefaultObserverList();
	
	public PMenuBody() {
		setFocusTraversal(new PMenuArrowKeyAndMouseOverFocusTraversal(this));
		setBorder(new PMenuBodyBorder());
		addActionMapping(ActionRemoveOnEscape.DEFAULT_KEY, ActionRemoveOnEscape.INSTANCE);
		
		DelegationPGridListLayout layout = new DelegationPGridListLayout(this, MenuEntryPart.COUNT);
		layout.setListAlignment(ListAlignment.TOP_TO_BOTTOM);
		layout.setInsets(DEFAULT_INSETS);
		
		for (MenuEntryPart col : MenuEntryPart.ALL) {
			int colIdx = col.getDefaultIndex().getColumn();
			layout.setColumnAlignmentX(colIdx, col.getDefaultColumnAlignmentX());
			layout.setColumnAlignmentY(colIdx, col.getDefaultColumnAlignmentY());
			layout.setColumnGrowth(colIdx, col.getDefaultColumnGrowth());
		}
		setLayout(layout);
		
		// mouse support, both focus and clicking
		addObs(new PMouseObs() {
			@Override
			public void onMouseMoved(PMouse mouse) {
				PMenuBody.this.onMouseMove(mouse);
			}
			@Override
			public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
				PMenuBody.this.onMousePressed(mouse, btn, clickCount);
			}
			@Override
			public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
				PMenuBody.this.onMouseReleased(mouse, btn, clickCount);
			}
			@Override
			public void onButtonTriggered(PMouse mouse, MouseButton btn, int clickCount) {
				PMenuBody.this.onMouseTriggered(mouse, btn, clickCount);
			}
		});
	}
	
	@Override
	protected DelegationPGridListLayout getLayoutInternal() {
		return (DelegationPGridListLayout) super.getLayoutInternal();
	}
	
	public void addMenuItem(int rowIndex, PComponent item) {
		PDelegateRowIndex constraint = new PDelegateRowIndex(rowIndex);
		getLayoutInternal().addChild(item, constraint);
	}
	
	public void removeAllItems() {
		getLayoutInternal().clearChildren();
	}
	
	public PComponent getMenuItemAtIndex(int index) {
		PDelegateRowIndex constraint = new PDelegateRowIndex(index);
		return getLayoutInternal().getChildForConstraint(constraint);
	}
	
	public int getMenuItemIndex(PComponent item) {
		Object constraint = item.getConstraintAtParent();
		if (constraint instanceof PDelegateRowIndex) {
			int rowIdx = ((PDelegateRowIndex) constraint).getRow();
			return rowIdx;
		}
		return -1;
	}
	
	public int getMenuItemCount() {
		return getLayoutInternal().getChildCount();
	}
	
	protected void onMouseMove(PMouse mouse) {
		PComponent comp = mouse.getComponentAtMouse();
		if (comp == null) {
			return;
		}
		if (comp.isDescendantOf(this)) {
			PComponent current = comp;
			while (current != this && !current.isFocusable()) {
				current = current.getParent();
			}
			PComponent focusComp = current;
			if (focusComp == this) {
				return;
			}
			focusComp.tryToTakeFocus();
		}
	}
	
	protected void onMousePressed(PMouse mouse, MouseButton btn, int clickCount) {
	}
	
	protected void onMouseReleased(PMouse mouse, MouseButton btn, int clickCount) {
	}
	
	protected void onMouseTriggered(PMouse mouse, MouseButton btn, int clickCount) {
		PComponent comp = mouse.getComponentAtMouse();
		if (comp == null) {
			return;
		}
		if (!comp.isDescendantOf(this)) {
			return;
		}
		AbstractPMenuItem item = (AbstractPMenuItem) comp.getFirstAncestorMatchingCondition(
			anc -> anc.getParent() == PMenuBody.this && anc instanceof AbstractPMenuItem
		);
		if (item == null) {
			return;
		}
		item.onMouseClick(mouse, btn, clickCount);
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
	
	@Override
	public boolean isFocusable() {
		return true;
	}
	
	public void addObs(PMenuBodyObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PMenuBodyObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireActionEvent(AbstractPMenuItem item) {
		int itemIndex = getMenuItemIndex(item);
		obsList.fireEvent(obs -> obs.onMenuItemAction(this, item, itemIndex));
	}
	
	protected void fireCloseRequestEvent() {
		obsList.fireEvent(obs -> obs.onCloseRequest(this));
	}
	
}