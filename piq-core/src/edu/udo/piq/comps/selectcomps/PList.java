package edu.udo.piq.comps.selectcomps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PList extends AbstractPLayoutOwner 
	implements PDropComponent 
{
	
	protected static final PColor BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor DROP_HIGHLIGHT_COLOR = PColor.RED;
	protected static final int DRAG_AND_DROP_DISTANCE = 16;
	
	private final PSelectionObs selectionObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			selectionAdded((PListIndex) index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			selectionRemoved((PListIndex) index);
		}
	};
	private final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PModel model, PModelIndex index, Object newContent) {
			getSelection().clearSelection();
			contentAdded((PListIndex) index, newContent);
		}
		public void onContentRemoved(PModel model, PModelIndex index, Object oldContent) {
			contentRemoved((PListIndex) index, oldContent);
			getSelection().removeSelection(index);
		}
		public void onContentChanged(PModel model, PModelIndex index, Object oldContent) {
			contentChanged((PListIndex) index, oldContent);
		}
	};
	private PListSelection selection;
	private PListModel model;
	private PCellFactory cellFactory;
	private PDnDSupport dndSup;
	private PModelIndex currentDnDHighlightIndex;
	private PCellComponent currentDnDHighlightComponent;
	private int lastDragY = -1;
	private boolean isDragTagged = false;
	
	public PList() {
		this(new DefaultPListModel());
	}
	
	public PList(PListModel model) {
		super();
		setLayout(new PListLayout(this, ListAlignment.FROM_TOP, 1));
		setSelection(new PListMultiSelection());
		setCellFactory(new DefaultPCellFactory());
		setModel(model);
		
		addObs(new PMouseObs() {
			public void buttonTriggered(PMouse mouse, MouseButton btn) {
				PList.this.onMouseButtonTriggred(mouse, btn);
			}
			public void buttonReleased(PMouse mouse, MouseButton btn) {
				PList.this.onMouseReleased(mouse, btn);
			}
			public void mouseMoved(PMouse mouse) {
				PList.this.onMouseMoved(mouse);
			}
		});
	}
	
	protected void onMouseButtonTriggred(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOverThisOrChild()) {
			PListIndex index = getIndexAt(mouse.getX(), mouse.getY());
			if (index != null) {
				if (mouse.isPressed(MouseButton.DRAG_AND_DROP)) {
					lastDragY = mouse.getY();
					isDragTagged = true;
				}
				
				PKeyboard keyBoard = getKeyboard();
				if (keyBoard == null || !keyBoard.isModifierToggled(Modifier.CTRL)) {
					getSelection().clearSelection();
				}
				getSelection().addSelection(index);
			}
		}
	}
	
	protected void onMouseReleased(PMouse mouse, MouseButton btn) {
		if (isDragTagged && btn == MouseButton.DRAG_AND_DROP) {
			isDragTagged = false;
		}
	}
	
	protected void onMouseMoved(PMouse mouse) {
		PDnDSupport dndSup = getDragAndDropSupport();
		if (dndSup != null && isDragTagged 
				&& mouse.isPressed(MouseButton.DRAG_AND_DROP)) 
		{
			int mx = mouse.getX();
			int my = mouse.getY();
			int disY = Math.abs(lastDragY - my);
			if (disY >= DRAG_AND_DROP_DISTANCE) {
				if (dndSup.canDrag(this, mx, my)) {
					dndSup.startDrag(this, mx, my);
				}
			} else {
//				dndSup.abortDrag(this, getDragAndDropManager().getActiveTransfer());
			}
		}
	}
	
	protected PListLayout getLayoutInternal() {
		return (PListLayout) super.getLayout();
	}
	
	public void setSelection(PListSelection listSelection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
		}
		selection = listSelection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
		}
	}
	
	public PListSelection getSelection() {
		return selection;
	}
	
	public void setModel(PListModel listModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		model = listModel;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		getLayoutInternal().clearChildren();
		if (model != null) {
			for (int i = 0; i < model.getSize(); i++) {
				contentAdded(new PListIndex(i), model.get(i));
			}
		}
	}
	
	public PListModel getModel() {
		return model;
	}
	
	public List<Object> getAllSelectedContent() {
		PSelection select = getSelection();
		PModel model = getModel();
		List<PModelIndex> indices = select.getAllSelected();
		if (indices.isEmpty()) {
			return Collections.emptyList();
		}
		List<Object> result = new ArrayList<>(indices.size());
		for (PModelIndex index : indices) {
			result.add(model.get(index));
		}
		return result;
	}
	
	public void setCellFactory(PCellFactory listCellFactory) {
		cellFactory = listCellFactory;
	}
	
	public PCellFactory getCellFactory() {
		return cellFactory;
	}
	
	public void setDragAndDropSupport(PDnDSupport support) {
		dndSup = support;
	}
	
	public PDnDSupport getDragAndDropSupport() {
		return dndSup;
	}
	
	public PListIndex getIndexAt(int x, int y) {
		if (getModel() == null) {
			return null;
		}
		PListLayout layout = getLayoutInternal();
		Collection<PComponent> children = layout.getChildren();
		for (int i = 0; i < children.size(); i++) {
			PComponent child = getLayoutInternal().getChild(i);
			if (layout.getChildBounds(child).contains(x, y)) {
				return new PListIndex(i);
			}
		}
		return null;
	}
	
	public PListIndex getDropIndex(int x, int y) {
		PListIndex index = getIndexAt(x, y);
		if (index == null && getModel() != null) {
			if (getBounds().contains(x, y)) {
				return new PListIndex(getModel().getSize());
			}
		}
		return index;
	}
	
	public void setDropHighlight(PModelIndex index) {
		if (currentDnDHighlightComponent != null) {
			currentDnDHighlightComponent.setDropHighlighted(false);
		}
		currentDnDHighlightIndex = index;
		if (index != null) {
			currentDnDHighlightComponent = getCellComponent((PListIndex) index);
			if (currentDnDHighlightComponent != null) {
				currentDnDHighlightComponent.setDropHighlighted(true);
			}
		}
	}
	
	public boolean isDropHighlighted() {
		return currentDnDHighlightComponent != null 
				|| currentDnDHighlightIndex != null;
	}
	
	protected void contentAdded(PListIndex index, Object newContent) {
		PCellComponent cell = getCellFactory().makeCellComponent(getModel(), index);
		getLayoutInternal().addChild(cell, Integer.valueOf(index.getIndexValue()));
	}
	
	protected void contentRemoved(PListIndex index, Object oldContent) {
		getLayoutInternal().removeChild(getCellComponent(index));
	}
	
	protected void contentChanged(PListIndex index, Object oldContent) {
		getCellComponent(index).setElement(getModel(), index);
	}
	
	protected void selectionAdded(PListIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(true);
		}
	}
	
	protected void selectionRemoved(PListIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(false);
		}
	}
	
	protected PCellComponent getCellComponent(PListIndex index) {
		return (PCellComponent) getLayoutInternal().
				getChild(index.getIndexValue());
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		
		// If highlighted but no cell component is highlighted => highlight the end of the list
		if (isDropHighlighted() && currentDnDHighlightComponent == null) {
			renderer.setColor(DROP_HIGHLIGHT_COLOR);
			
			// Check whether there are any cell components in the list
			int lastIndex = getModel().getSize() - 1;
			if (lastIndex == -1) {
				// No components in the list, highlight top of the list
				renderer.drawQuad(x, y, fx, y + 2);
			} else {
				// Highlight bottom of the last cell component in the list
				PListIndex lastListIndex = new PListIndex(lastIndex);
				PCellComponent lastCellComp = getCellComponent(lastListIndex);
				PBounds lastCellBounds = lastCellComp.getBounds();
				int cx = lastCellBounds.getX();
				int cy = lastCellBounds.getFinalY();
				int cfx = lastCellBounds.getFinalX();
				int cfy = cy + 2;
				
				renderer.drawQuad(cx, cy, cfx, cfy);
			}
		}
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	public void addObs(PModelObs obs) {
	}
	
	public void removeObs(PModelObs obs) {
	}
	
	public void addObs(PSelectionObs obs) {
	}
	
	public void removeObs(PSelectionObs obs) {
	}
	
}