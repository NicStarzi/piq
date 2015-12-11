package edu.udo.piq.components.collections;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import edu.udo.piq.components.defaults.DefaultPCellFactory;
import edu.udo.piq.components.defaults.DefaultPDnDSupport;
import edu.udo.piq.components.defaults.DefaultPTreeModel;
import edu.udo.piq.layouts.PTreeLayout2;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public class PTree extends AbstractPInputLayoutOwner 
	implements PDropComponent 
{
	
	public static final Comparator<PModelIndex> TREE_INDEX_DEPTH_COMPARATOR = 
	new Comparator<PModelIndex>() {
		public int compare(PModelIndex o1, PModelIndex o2) {
			PTreeIndex ti1 = (PTreeIndex) o1;
			PTreeIndex ti2 = (PTreeIndex) o2;
			int depthCompare = ti2.getDepth() - ti1.getDepth();
			if (depthCompare == 0) {
				return ti2.getLastIndex() - ti1.getLastIndex();
			}
			return depthCompare;
		}
	};
	
	protected static final PColor BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor FOCUS_COLOR = PColor.GREY25;
	protected static final PColor DROP_HIGHLIGHT_COLOR = PColor.RED;
	protected static final int DRAG_AND_DROP_DISTANCE = 20;
	
	protected final ObserverList<PModelObs> modelObsList
		= PCompUtil.createDefaultObserverList();
	protected final ObserverList<PSelectionObs> selectionObsList
		= PCompUtil.createDefaultObserverList();
	private final PSelectionObs selectionObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			selectionAdded((PTreeIndex) index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			selectionRemoved((PTreeIndex) index);
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
			contentAdded((PTreeIndex) index, newContent);
			
			System.out.println(getModel());
			getLayoutInternal().debug();
		}
		public void onContentRemoved(PModel model, PModelIndex index, Object oldContent) {
			contentRemoved((PTreeIndex) index, oldContent);
			getSelection().removeSelection(index);
			
			System.out.println(getModel());
			getLayoutInternal().debug();
		}
		public void onContentChanged(PModel model, PModelIndex index, Object oldContent) {
			contentChanged((PTreeIndex) index, oldContent);
		}
	};
	private PTreeSelection selection;
	private PTreeModel model;
	private PCellFactory cellFactory;
	private PDnDSupport dndSup;
	private PModelIndex currentDnDHighlightIndex;
	private PCellComponent currentDnDHighlightComponent;
	private int lastDragX = -1;
	private int lastDragY = -1;
	private boolean isDragTagged = false;
	
	public PTree() {
		this(new DefaultPTreeModel());
	}
	
	public PTree(PTreeModel model) {
		super();
		setLayout(new PTreeLayout2(this));
		setDragAndDropSupport(new DefaultPDnDSupport());
		setSelection(new PTreeSingleSelection());
		setCellFactory(new DefaultPCellFactory());
		setModel(model);
		
		addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PTree.this.onMouseButtonTriggred(mouse, btn);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				PTree.this.onMouseReleased(mouse, btn);
			}
			public void onMouseMoved(PMouse mouse) {
				PTree.this.onMouseMoved(mouse);
			}
		});
		addObs(new PFocusObs() {
			public void focusGained(PComponent oldOwner, PComponent newOwner) {
				if (newOwner == PTree.this && getSelection() != null) {
					fireReRenderEvent();
				}
			}
			public void focusLost(PComponent oldOwner) {
				if (oldOwner == PTree.this && getSelection() != null) {
					fireReRenderEvent();
				}
			}
		});
	}
	
	protected void onMouseButtonTriggred(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && isMouseOverThisOrChild()) {
			PTreeIndex index = getIndexAt(mouse.getX(), mouse.getY());
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
	
	protected PTreeLayout2 getLayoutInternal() {
		return (PTreeLayout2) super.getLayout();
	}
	
	public void setSelection(PTreeSelection listSelection) {
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
	
	public PTreeSelection getSelection() {
		return selection;
	}
	
	public void setModel(PTreeModel listModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
			for (PModelObs obs : modelObsList) {
				getModel().removeObs(obs);
			}
		}
		model = listModel;
		getLayoutInternal().clearChildren();
		if (getModel() != null) {
			PTreeModel model = getModel();
			
			model.addObs(modelObs);
			for (PModelObs obs : modelObsList) {
				model.addObs(obs);
			}
			for (PModelIndex index : model) {
				contentAdded((PTreeIndex) index, model.get(index));
			}
		}
	}
	
	public PTreeModel getModel() {
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
		
		PTreeModel model = getModel();
		if (model != null) {
			for (PModelIndex index : model) {
				contentAdded((PTreeIndex) index, model.get(index));
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
	
	public PTreeIndex getIndexAt(int x, int y) {
		if (getModel() == null) {
			return null;
		}
		PTreeLayout2 layout = getLayoutInternal();
		PComponent child = layout.getChildAt(x, y);
		if (child == null) {
			return null;
		}
		return (PTreeIndex) layout.getChildConstraint(child);
	}
	
	public PTreeIndex getDropIndex(int x, int y) {
		if (getModel() == null) {
			return null;
		}
		if (getModel().getRoot() == null) {
			return new PTreeIndex();
		}
		PTreeLayout2 layout = getLayoutInternal();
		PComponent child = layout.getChildAt(x, y);
		if (child == null) {
			return null;
		}
		if (child == layout.getRootComponent()) {
			return new PTreeIndex(0);
		}
		PTreeIndex childIndex = (PTreeIndex) layout.getChildConstraint(child);
		PBounds childBnds = layout.getChildBounds(child);
		int cy = childBnds.getY();
		int margin = childBnds.getHeight() / 2;
		int aboveLine = cy + margin / 2;
		int belowLine = cy + margin + margin / 2;
		if (y < aboveLine) {
			return childIndex;
		} else if (y > belowLine) {
			int depth = childIndex.getDepth() - 1;
			int newIndex = childIndex.getLastIndex() + 1;
			return childIndex.replaceIndex(depth, newIndex);
		} else {
			int childCount = getModel().getChildCount(childIndex);
			return childIndex.append(childCount);
		}
	}
	
	public List<PModelIndex> getDragIndices() {
		PSelection selection = getSelection();
		if (getModel() == null || selection == null 
				|| selection.getAllSelected().isEmpty()) 
		{
			return Collections.emptyList();
		}
		List<PModelIndex> selectedIndices = selection.copyAllSelected();
		selectedIndices.sort(TREE_INDEX_DEPTH_COMPARATOR);
		Set<PModelIndex> dragIndexSet = new HashSet<>();
		
		for (PModelIndex index : selectedIndices) {
			if (!dragIndexSet.contains(index)) {
				addAllSubIndices(dragIndexSet, (PTreeIndex) index);
			}
		}
		List<PModelIndex> dragIndices = new ArrayList<>(dragIndexSet);
		dragIndices.sort(TREE_INDEX_DEPTH_COMPARATOR);
		return dragIndices;
	}
	
	protected void addAllSubIndices(Set<PModelIndex> toFill, PTreeIndex index) {
		Deque<PTreeIndex> stack = new ArrayDeque<>();
		stack.push(index);
		while (!stack.isEmpty()) {
			PTreeIndex current = stack.pop();
			toFill.add(current);
			
			int childCount = getModel().getChildCount(current);
			for (int i = 0; i < childCount; i++) {
				stack.push(current.append(i));
			}
		}
	}
	
	public void setDropHighlight(PModelIndex index) {
		if (currentDnDHighlightComponent != null) {
			currentDnDHighlightComponent.setDropHighlighted(false);
		}
		currentDnDHighlightIndex = index;
		if (index != null) {
			currentDnDHighlightComponent = getCellComponent((PTreeIndex) index);
			if (currentDnDHighlightComponent != null) {
				currentDnDHighlightComponent.setDropHighlighted(true);
			}
		}
	}
	
	public boolean isDropHighlighted() {
		return currentDnDHighlightComponent != null 
				|| currentDnDHighlightIndex != null;
	}
	
	protected void contentAdded(PTreeIndex index, Object newContent) {
		PCellComponent cell = getCellFactory().makeCellComponent(getModel(), index);
		getLayoutInternal().addChild(cell, index);
	}
	
	protected void contentRemoved(PTreeIndex index, Object oldContent) {
		getLayoutInternal().removeChild(getCellComponent(index));
	}
	
	protected void contentChanged(PTreeIndex index, Object oldContent) {
		getCellComponent(index).setElement(getModel(), index);
	}
	
	protected void selectionAdded(PTreeIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(true);
		}
	}
	
	protected void selectionRemoved(PTreeIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(false);
		}
	}
	
	public PCellComponent getCellComponent(PTreeIndex index) {
		return (PCellComponent) getLayoutInternal().
				getComponentAt(index, index.getDepth());
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		
		// Draw black lines connecting parent and child nodes
		renderer.setColor(PColor.BLACK);
		
		PTreeLayout2 layout = getLayoutInternal();
		PComponent root = layout.getRootComponent();
		if (root == null) {
			return;
		}
		Deque<PComponent> stack = new ArrayDeque<>();
		stack.push(root);
		while (!stack.isEmpty()) {
			PComponent current = stack.pop();
			PBounds parentBnds = current.getBounds();
			int ph = parentBnds.getHeight();
			int px = parentBnds.getX() - 6;
			if (current == layout.getRootComponent() && px < x) {
				px = x + 1;
			}
			int py = parentBnds.getY() + ph / 2;
			
			for (PComponent child : layout.getChildrenOf(current)) {
				PBounds childBnds = child.getBounds();
				int ch = childBnds.getHeight();
				int cx = childBnds.getX() - 2;
				int cy = childBnds.getY() + ch / 2;
				
				renderer.drawLine(px, py, px, cy, 1);
				renderer.drawLine(px, cy, cx, cy, 1);
				
				stack.push(child);
			}
		}
		// Draw drop highlights for not existing nodes (new indices)
		if (currentDnDHighlightIndex != null 
				&& currentDnDHighlightComponent == null) 
		{
			PTreeIndex childIndex = (PTreeIndex) currentDnDHighlightIndex;
			PTreeIndex parentIndex = childIndex.createParentIndex();
			// check if parent has children or not
			int childCount = getModel().getChildCount(parentIndex);
			if (childCount == 0) {
				// no children => highlight position that does not exist yet
				PComponent parentCell = layout.getChildForConstraint(parentIndex);
				if (parentCell != null) {
					PBounds cellBounds = parentCell.getBounds();
					int cx = cellBounds.getFinalX() + 1;
					int cy = cellBounds.getY();
					int cfx = cellBounds.getFinalX() + 3;
					int cfy = cellBounds.getFinalY();
					
					renderer.setColor(DROP_HIGHLIGHT_COLOR);
					renderer.drawQuad(cx, cy, cfx, cfy);
				}
			} else {
				// highlight below the last child of parent
				PTreeIndex sibblingIndex = parentIndex.append(childCount - 1);
				PComponent sibblingCell = layout.getChildForConstraint(sibblingIndex);
				PBounds cellBounds = sibblingCell.getBounds();
				int cx = cellBounds.getX();
				int cy = cellBounds.getFinalY();
				int cfx = cellBounds.getFinalX();
				int cfy = cellBounds.getFinalY() + 2;
				
				renderer.setColor(DROP_HIGHLIGHT_COLOR);
				renderer.drawQuad(cx, cy, cfx, cfy);
			}
		}
//		if (hasFocus() && getSelection() != null) {
//			PListIndex lastSelectedIndex = getSelection().getLastSelected();
//			if (lastSelectedIndex != null) {
//				PCellComponent cellComp = getCellComponent(lastSelectedIndex);
//				PBounds cellBounds = cellComp.getBounds();
//				int cx = cellBounds.getX() - 1;
//				int cy = cellBounds.getY() - 1;
//				int cfx = cellBounds.getFinalX() + 1;
//				int cfy = cellBounds.getFinalY() + 1;
//				
//				renderer.setColor(FOCUS_COLOR);
//				renderer.drawQuad(cx, cy, cfx, cfy);
//			}
//		}
	}
	
	public boolean isFocusable() {
		return true;
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