package edu.udo.piq.components.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PSize;
import edu.udo.piq.components.defaults.DefaultPCellFactory;
import edu.udo.piq.components.defaults.DefaultPDnDSupport;
import edu.udo.piq.layouts.PTableLayout3;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PTable extends AbstractPInputLayoutOwner 
	implements PDropComponent 
{
	
	protected static final PColor BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor FOCUS_COLOR = PColor.GREY50;
	protected static final PColor DROP_HIGHLIGHT_COLOR = PColor.RED;
	protected static final int DRAG_AND_DROP_DISTANCE = 20;
	
//	private final PInput moveUpInput = new PInput() {
//		public Key getInputKey() {
//			return Key.UP;
//		}
//		public KeyInputType getKeyInputType() {
//			return KeyInputType.PRESS;
//		}
//		public boolean canBeUsed(PKeyboard keyboard) {
//			return isEnabled() && getModel() != null 
//					&& getSelection() != null 
//					&& getSelection().getLastSelected() != null;
//		}
//	};
//	private final Runnable moveUpReaction = new Runnable() {
//		public void run() {
//			onUpKeyTriggered();
//		}
//	};
//	private final PInput moveDownInput = new PInput() {
//		public Key getInputKey() {
//			return Key.DOWN;
//		}
//		public KeyInputType getKeyInputType() {
//			return KeyInputType.PRESS;
//		}
//		public boolean canBeUsed(PKeyboard keyboard) {
//			return isEnabled() && getModel() != null 
//					&& getSelection() != null 
//					&& getSelection().getLastSelected() != null;
//		}
//	};
//	private final Runnable moveDownReaction = new Runnable() {
//		public void run() {
//			onDownKeyTriggered();
//		}
//	};
	
	protected final ObserverList<PModelObs> modelObsList
		= PCompUtil.createDefaultObserverList();
	protected final ObserverList<PSelectionObs> selectionObsList
		= PCompUtil.createDefaultObserverList();
	private final PSelectionObs selectionObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			selectionAdded((PTableIndex) index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			selectionRemoved((PTableIndex) index);
		}
		public void onLastSelectedChanged(PSelection selection,
				PModelIndex prevLastSelected, PModelIndex newLastSelected) 
		{
			if (hasFocus()) {
				fireReRenderEvent();
			}
		}
	};
	private final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PModel model, PModelIndex index, Object newContent) {
			getSelection().clearSelection();
			contentAdded((PTableIndex) index, newContent);
		}
		public void onContentRemoved(PModel model, PModelIndex index, Object oldContent) {
			contentRemoved((PTableIndex) index, oldContent);
			getSelection().removeSelection(index);
		}
		public void onContentChanged(PModel model, PModelIndex index, Object oldContent) {
			contentChanged((PTableIndex) index, oldContent);
		}
	};
	private PTableSelection selection;
	private PTableModel model;
	private PCellFactory cellFactory;
	private PDnDSupport dndSup;
	private PModelIndex currentDnDHighlightIndex;
	private PCellComponent currentDnDHighlightComponent;
	private int lastDragX = -1;
	private int lastDragY = -1;
	private boolean isDragTagged = false;
	
	public PTable() {
		//TODO Change to default model
		this(new FixedSizePTableModel(0, 0));
	}
	
	public PTable(PTableModel model) {
		super();
		setLayout(new PTableLayout3(this));
		setDragAndDropSupport(new DefaultPDnDSupport());
		setSelection(new PTableSingleSelection());
		setCellFactory(new DefaultPCellFactory());
		setModel(model);
		
		addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PTable.this.onMouseButtonTriggred(mouse, btn);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				PTable.this.onMouseReleased(mouse, btn);
			}
			public void onMouseMoved(PMouse mouse) {
				PTable.this.onMouseMoved(mouse);
			}
		});
		addObs(new PFocusObs() {
			public void onFocusGained(PComponent oldOwner, PComponent newOwner) {
				if (newOwner == PTable.this && getSelection() != null) {
					fireReRenderEvent();
				}
			}
			public void onFocusLost(PComponent oldOwner) {
				if (oldOwner == PTable.this && getSelection() != null) {
					fireReRenderEvent();
				}
			}
		});
		
//		defineInput(moveUpInput.getDefaultIdentifier(), moveUpInput, moveUpReaction);
//		defineInput(moveDownInput.getDefaultIdentifier(), moveDownInput, moveDownReaction);
	}
	
	protected void onMouseButtonTriggred(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOverThisOrChild()) {
			PTableIndex index = getIndexAt(mouse.getX(), mouse.getY());
			if (index != null) {
				if (mouse.isPressed(MouseButton.DRAG_AND_DROP)) {
					lastDragX = mouse.getX();
					lastDragY = mouse.getY();
					isDragTagged = true;
				}
				
				PKeyboard keyBoard = getKeyboard();
				if (keyBoard == null || !keyBoard.isModifierToggled(Modifier.CTRL)) {
					getSelection().clearSelection();
				}
				getSelection().addSelection(index);
				takeFocus();
			}
		}
	}
	
	protected void onMouseReleased(PMouse mouse, MouseButton btn) {
		if (isDragTagged && mouse.isReleased(MouseButton.DRAG_AND_DROP)) {
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
			int disX = Math.abs(lastDragX - mx);
			int disY = Math.abs(lastDragY - my);
			int dis = Math.max(disX, disY);
			if (dis >= DRAG_AND_DROP_DISTANCE) {
				if (dndSup.canDrag(this, mx, my)) {
					dndSup.startDrag(this, mx, my);
				}
			}
		}
	}
	
//	protected void onUpKeyTriggered() {
//		onMoveSelection(-1);
//	}
//	
//	protected void onDownKeyTriggered() {
//		onMoveSelection(1);
//	}
//	
//	protected void onMoveSelection(int moveOffset) {
//		PTableIndex lastSelected = getSelection().getLastSelected();
//		int nextSelectedVal = lastSelected.getIndexValue() + moveOffset;
//		if (nextSelectedVal >= 0 && nextSelectedVal < getModel().getSize()) {
//			PTableIndex nextSelected = new PTableIndex(nextSelectedVal);
//			
//			PKeyboard keyBoard = getKeyboard();
//			if (keyBoard == null || !keyBoard.isModifierToggled(Modifier.CTRL)) {
//				getSelection().clearSelection();
//			}
//			
//			getSelection().addSelection(nextSelected);
//		}
//	}
	
	protected PTableLayout3 getLayoutInternal() {
		return (PTableLayout3) super.getLayout();
	}
	
	public void setSelection(PTableSelection listSelection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
			for (PSelectionObs obs : selectionObsList) {
				getSelection().removeObs(obs);
			}
		}
		selection = listSelection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
			for (PSelectionObs obs : selectionObsList) {
				getSelection().addObs(obs);
			}
		}
	}
	
	public PTableSelection getSelection() {
		return selection;
	}
	
	public void setModel(PTableModel listModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
			for (PModelObs obs : modelObsList) {
				getModel().removeObs(obs);
			}
		}
		model = listModel;
//		getLayoutInternal().clearChildren();
		resizeLayoutTable();
		if (getModel() != null) {
			PTableModel model = getModel();
			
			model.addObs(modelObs);
			for (PModelObs obs : modelObsList) {
				model.addObs(obs);
			}
			for (PModelIndex index : model) {
				contentAdded((PTableIndex) index, model.get(index));
			}
		}
	}
	
	protected void resizeLayoutTable() {
		PTableLayout3 layout = getLayoutInternal();
		layout.removeAllColumnsAndRows();
		PTableModel model = getModel();
		for (int c = 0; c < model.getColumnCount(); c++) {
			layout.addColumn();
		}
		for (int r = 0; r < model.getRowCount(); r++) {
			layout.addRow();
		}
	}
	
	public PTableModel getModel() {
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
		getLayoutInternal().clearChildren();
		
		PTableModel model = getModel();
		if (model != null) {
			for (PModelIndex index : model) {
				contentAdded((PTableIndex) index, model.get(index));
			}
		}
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
	
	public PTableIndex getIndexAt(int x, int y) {
		if (getModel() == null) {
			return null;
		}
		PTableLayout3 layout = getLayoutInternal();
		Collection<PComponent> children = layout.getChildren();
		for (PComponent child : children) {
			if (layout.getChildBounds(child).contains(x, y)) {
				return (PTableIndex) layout.getChildConstraint(child);
			}
		}
		return null;
	}
	
	public PTableIndex getDropIndex(int x, int y) {
		return getIndexAt(x, y);
	}
	
	public void setDropHighlight(PModelIndex index) {
		if (currentDnDHighlightComponent != null) {
			currentDnDHighlightComponent.setDropHighlighted(false);
		}
		currentDnDHighlightIndex = index;
		if (index != null) {
			currentDnDHighlightComponent = getCellComponent((PTableIndex) index);
			if (currentDnDHighlightComponent != null) {
				currentDnDHighlightComponent.setDropHighlighted(true);
			}
		}
	}
	
	public boolean isDropHighlighted() {
		return currentDnDHighlightComponent != null 
				|| currentDnDHighlightIndex != null;
	}
	
	protected void contentAdded(PTableIndex index, Object newContent) {
		PCellComponent cell = getCellFactory().makeCellComponent(getModel(), index);
		getLayoutInternal().addChild(cell, index);
	}
	
	protected void contentRemoved(PTableIndex index, Object oldContent) {
		getLayoutInternal().removeChild(getCellComponent(index));
	}
	
	protected void contentChanged(PTableIndex index, Object oldContent) {
		getCellComponent(index).setElement(getModel(), index);
	}
	
	protected void selectionAdded(PTableIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(true);
		}
	}
	
	protected void selectionRemoved(PTableIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(false);
		}
	}
	
	public PCellComponent getCellComponent(PTableIndex index) {
		return (PCellComponent) getLayoutInternal().getChildForConstraint(index);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.BLACK);
		
		PTableLayout3 layout = getLayoutInternal();
		int colGapW = layout.getCellGapWidth();
		int rowGapH = layout.getCellGapHeight();
		
		PSize prefSize = layout.getPreferredSize();
		int sizeFx = x + prefSize.getWidth();
		int sizeFy = y + prefSize.getHeight();
		renderer.strokeQuad(x, y, sizeFx + colGapW, sizeFy + rowGapH);
		
		for (int c = 1; c < layout.getColumnCount(); c++) {
			int colW = layout.getColumnWidth(c);
			
			for (int r = 1; r < layout.getRowCount(); r++) {
				int cx = x + colW + (colW + colGapW) * (c - 1);
				int cfx = cx + colGapW;
				renderer.drawQuad(cx, y, cfx, sizeFy);
				
				int rowH = layout.getRowHeight(r);
				int cy = y + rowH + (rowH + rowGapH) * (r - 1);
				int cfy = cy + rowGapH;
				renderer.drawQuad(x, cy, sizeFx, cfy);
			}
		}
		if (hasFocus() && getSelection() != null) {
			PTableIndex lastSelectedIndex = getSelection().getLastSelected();
			if (lastSelectedIndex != null) {
				PCellComponent cellComp = getCellComponent(lastSelectedIndex);
				PBounds cellBounds = cellComp.getBounds();
				int cx = cellBounds.getX() - 1;
				int cy = cellBounds.getY() - 1;
				int cfx = cellBounds.getFinalX() + 1;
				int cfy = cellBounds.getFinalY() + 1;
				
				renderer.setColor(FOCUS_COLOR);
				renderer.drawQuad(cx, cy, cfx, cfy);
			}
		}
	}
	
	public void addObs(PModelObs obs) {
		modelObsList.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PModelObs obs) {
		modelObsList.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	public void addObs(PSelectionObs obs) {
		selectionObsList.add(obs);
		if (getSelection() != null) {
			getSelection().addObs(obs);
		}
	}
	
	public void removeObs(PSelectionObs obs) {
		selectionObsList.remove(obs);
		if (getSelection() != null) {
			getSelection().removeObs(obs);
		}
	}

}