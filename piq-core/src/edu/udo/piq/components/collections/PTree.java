package edu.udo.piq.components.collections;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PModelFactory;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.defaults.PTreePCellFactory;
import edu.udo.piq.components.defaults.DefaultPTreeModel;
import edu.udo.piq.components.defaults.PTreePDnDSupport;
import edu.udo.piq.components.defaults.ReRenderPFocusObs;
import edu.udo.piq.layouts.PTreeLayout;
import edu.udo.piq.tools.AbstractPInputLayoutOwner;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class PTree extends AbstractPInputLayoutOwner 
	implements PDropComponent 
{
	
	protected static final PColor BACKGROUND_COLOR = PColor.WHITE;
	protected static final PColor PARENT_CHILD_LINE_COLOR = PColor.GREY75;
	protected static final PColor FOCUS_COLOR = PColor.GREY25;
	protected static final PColor DROP_HIGHLIGHT_COLOR = PColor.RED;
	protected static final int DRAG_AND_DROP_DISTANCE = 20;
	
	protected final ObserverList<PModelObs> obsListModel
		= PCompUtil.createDefaultObserverList();
	protected final ObserverList<PSelectionObs> obsListSelection
		= PCompUtil.createDefaultObserverList();
	protected final Set<PTreeIndex> hiddenIdxSet = new HashSet<>();
	protected final PSelectionObs selectionObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			PTree.this.onSelectionAdded((PTreeIndex) index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			PTree.this.onSelectionRemoved((PTreeIndex) index);
		}
		public void onLastSelectedChanged(PSelection sel, PModelIndex oldIdx, PModelIndex newIdx) {
			PTree.this.onLastSelectedChanged(oldIdx, newIdx);
		}
	};
	protected final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PModel model, PModelIndex index, Object newContent) {
			PTree.this.onContentAdded((PTreeIndex) index, newContent);
		}
		public void onContentRemoved(PModel model, PModelIndex index, Object oldContent) {
			PTree.this.onContentRemoved((PTreeIndex) index, oldContent);
		}
		public void onContentChanged(PModel model, PModelIndex index, Object oldContent) {
			PTree.this.onContentChanged((PTreeIndex) index, oldContent);
		}
	};
	protected PTreeSelection	selection;
	protected PTreeModel		model;
	protected PCellFactory		cellFact;
	protected PDnDSupport		dndSup;
	protected PTreeIndex		currentDnDHighlightIdx;
	protected PCellComponent	currentDnDHighlightCmp;
	protected int				lastDragX = -1;
	protected int				lastDragY = -1;
	protected boolean			isDragTagged = false;
	
	public PTree(PTreeModel model) {
		this();
		setModel(model);
	}
	
	public PTree() {
		super();
		
		PModelFactory modelFac = PModelFactory.getGlobalModelFactory();
		PTreeModel defaultModel = new DefaultPTreeModel();
		if (modelFac != null) {
			defaultModel = (PTreeModel) modelFac.getModelFor(this, defaultModel);
		}
		
		setLayout(new PTreeLayout(this));
		setDragAndDropSupport(new PTreePDnDSupport());
		setSelection(new PTreeSingleSelection());
		setCellFactory(new PTreePCellFactory());
		setModel(defaultModel);
		
		addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PTree.this.onMouseButtonTriggred(mouse, btn);
			}
			public void onButtonPressed(PMouse mouse, MouseButton btn) {
				PTree.this.onMouseButtonPressed(mouse, btn);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				PTree.this.onMouseReleased(mouse, btn);
			}
			public void onMouseMoved(PMouse mouse) {
				PTree.this.onMouseMoved(mouse);
			}
		});
		addObs(new ReRenderPFocusObs());
	}
	
	protected PTreeLayout getLayoutInternal() {
		return (PTreeLayout) super.getLayout();
	}
	
	public void setSelection(PTreeSelection selection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
			for (PSelectionObs obs : obsListSelection) {
				getSelection().removeObs(obs);
			}
		}
		this.selection = selection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
			for (PSelectionObs obs : obsListSelection) {
				getSelection().addObs(obs);
			}
		}
	}
	
	public PTreeSelection getSelection() {
		return selection;
	}
	
	public void setModel(PTreeModel treeModel) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
			for (PModelObs obs : obsListModel) {
				getModel().removeObs(obs);
			}
		}
		this.model = treeModel;
		getLayoutInternal().clearChildren();
		if (getModel() != null) {
			PTreeModel model = getModel();
			
			model.addObs(modelObs);
			for (PModelObs obs : obsListModel) {
				model.addObs(obs);
			}
			for (PModelIndex index : model.createBreadthOrderIterator()) {
				addContent((PTreeIndex) index, model.get(index));
			}
		}
	}
	
	public PTreeModel getModel() {
		return model;
	}
	
	public void setCellFactory(PCellFactory listCellFactory) {
		cellFact = listCellFactory;
		getLayoutInternal().clearChildren();
		
		PTreeModel model = getModel();
		if (model != null) {
			for (PModelIndex index : model.createBreadthOrderIterator()) {
				addContent((PTreeIndex) index, model.get(index));
			}
		}
	}
	
	public PCellFactory getCellFactory() {
		return cellFact;
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
		PTreeLayout layout = getLayoutInternal();
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
		PTreeLayout layout = getLayoutInternal();
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
		return selection.getAllSelected();
	}
	
	public void setAllIndicesExpanded(boolean isExpanded) {
		if (isExpanded) {
			if (!hiddenIdxSet.isEmpty()) {
				hiddenIdxSet.clear();
				addSubTree(PTreeIndex.ROOT);				
			}
		} else {
			setIndexExpanded(PTreeIndex.ROOT, false);
		}
	}
	
	public void makeIndexVisible(PTreeIndex index) {
		for (PTreeIndex ancestorIndex : getModel().createAncestorIterator(index)) {
			setIndexExpanded(ancestorIndex, true);
		}
	}
	
	public boolean isIndexVisible(PTreeIndex index) {
		for (PTreeIndex ancestorIndex : getModel().createAncestorIterator(index)) {
			if (!isIndexExpanded(ancestorIndex)) {
				return false;
			}
		}
		return true;
	}
	
	public void setIndexExpanded(PTreeIndex index, boolean isExpanded) {
		if (isExpanded) {
			if (hiddenIdxSet.remove(index)) {
				addSubTree(index);
			}
		} else {
			if (hiddenIdxSet.add(index)) {
				removeSubTree(index);
			}
		}
	}
	
	public boolean isIndexExpanded(PTreeIndex index) {
		return !hiddenIdxSet.contains(index);
	}
	
	public void setDropHighlight(PModelIndex index) {
		if (currentDnDHighlightCmp != null) {
			currentDnDHighlightCmp.setDropHighlighted(false);
		}
		if (index == null) {
			currentDnDHighlightIdx = null;
		} else {
			currentDnDHighlightIdx = ThrowException.ifTypeCastFails(index, PTreeIndex.class, 
					"!(index instanceof PTreeIndex)");
			
			currentDnDHighlightCmp = getCellComponent((PTreeIndex) index);
			if (currentDnDHighlightCmp != null) {
				currentDnDHighlightCmp.setDropHighlighted(true);
			}
		}
	}
	
	public boolean isDropHighlighted() {
		return currentDnDHighlightCmp != null 
				|| currentDnDHighlightIdx != null;
	}
	
	public PCellComponent getCellComponent(PTreeIndex index) {
		return (PCellComponent) getLayoutInternal().getComponentAt(index);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(BACKGROUND_COLOR);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		
		PTreeLayout layout = getLayoutInternal();
		PComponent root = layout.getRootComponent();
		if (root == null) {
			return;
		}
		defaultRenderParentChildConnections(renderer, x);
		defaultRenderDropHighlighting(renderer);
		defaultRenderFocus(renderer);
	}
	
	protected void defaultRenderParentChildConnections(PRenderer renderer, int boundsX) {
		// Draw black lines connecting parent and child nodes
		renderer.setColor(PARENT_CHILD_LINE_COLOR);
		PTreeLayout layout = getLayoutInternal();
		PComponent root = layout.getRootComponent();
		
		Deque<PComponent> stack = new ArrayDeque<>();
		stack.push(root);
		while (!stack.isEmpty()) {
			PComponent current = stack.pop();
			PBounds parentBnds = current.getBounds();
			int ph = parentBnds.getHeight();
			int px = parentBnds.getX() - 6;
			if (current == layout.getRootComponent() && px < boundsX) {
				px = boundsX + 1;
			}
			int py = parentBnds.getY() + ph / 2;
			
			for (PComponent child : layout.getChildNodesOf(current)) {
				PBounds childBnds = child.getBounds();
				int ch = childBnds.getHeight();
				int cx = childBnds.getX() - 2;
				int cy = childBnds.getY() + ch / 2;
				
				renderer.drawLine(px, py, px, cy, 1);
				renderer.drawLine(px, cy, cx, cy, 1);
				
				stack.push(child);
			}
		}
	}
	
	protected void defaultRenderDropHighlighting(PRenderer renderer) {
		// The drop highlight above a node is already handled by the DefaultPCellComponent.
		// This method only has to draw the drop highlight for nodes that do not exist yet.
		// Those nodes are always the last children of their parent. 
		if (currentDnDHighlightIdx != null 
				&& currentDnDHighlightCmp == null) 
		{
			PTreeIndex childIndex = currentDnDHighlightIdx;
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
					int cfx = cx + 2;
					int cfy = cellBounds.getFinalY();
					
					renderer.setColor(DROP_HIGHLIGHT_COLOR);
					renderer.drawQuad(cx, cy, cfx, cfy);
				} else {
					System.out.println("DROP="+childIndex);
					System.out.println("PARENT="+parentIndex);
					System.out.println();
				}
			} else {
				// highlight below the last child of parent
				PTreeIndex sibblingIndex = parentIndex.append(childCount - 1);
				PComponent sibblingCell = layout.getChildForConstraint(sibblingIndex);
				if (sibblingCell != null) {
					PBounds cellBounds = sibblingCell.getBounds();
					int cx = cellBounds.getX();
					int cy = cellBounds.getFinalY();
					int cfx = cellBounds.getFinalX();
					int cfy = cy + 2;
					
					renderer.setColor(DROP_HIGHLIGHT_COLOR);
					renderer.drawQuad(cx, cy, cfx, cfy);
				}
			}
		}
	}
	
	protected void defaultRenderFocus(PRenderer renderer) {
		if (hasFocus() && getSelection() != null) {
			PTreeIndex lastSelectedIdx = getSelection().getLastSelected();
			if (lastSelectedIdx != null) {
				PCellComponent cellComp = getCellComponent(lastSelectedIdx);
				// In case the last selected index was removed!
				if (cellComp != null && cellComp.getParent() == this) {
					PBounds cellBounds = cellComp.getBounds();
					int cx = cellBounds.getX() - 1;
					int cy = cellBounds.getY() - 1;
					int cfx = cellBounds.getFinalX() + 1;
					int cfy = cellBounds.getFinalY() + 1;
					
					renderer.setRenderMode(renderer.getRenderModeOutlineDashed());
					renderer.setColor(FOCUS_COLOR);
					renderer.drawQuad(cx, cy, cfx, cfy);
				}
			}
		}
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	protected void removeSubTree(PTreeIndex index) {
		PTreeModel model = getModel();
		for (PTreeIndex childIndex : model.createPostOrderIterator(index)) {
			if (index == childIndex) {
				continue;
			}
			if (!getLayout().containsChild(childIndex)) {
				continue;
			}
			getSelection().removeSelection(childIndex);
			getLayoutInternal().removeChild(childIndex);
		}
	}
	
	protected void addSubTree(PTreeIndex index) {
		PTreeModel model = getModel();
		getSelection().clearSelection();
		for (PTreeIndex childIndex : model.createBreadthOrderIterator(index)) {
			if (index == childIndex) {
				continue;
			}
			if (!isIndexVisible(childIndex)) {
				continue;
			}
			PCellComponent cell = getCellFactory().makeCellComponent(getModel(), childIndex);
			getLayoutInternal().addChild(cell, childIndex);
		}
	}
	
	protected void addContent(PTreeIndex index, Object newContent) {
//		System.out.println("PTree.addContent() idx="+index+", obj="+newContent);
		if (isParentExpanded(index)) {
			getSelection().clearSelection();
			PCellComponent cell = getCellFactory().makeCellComponent(getModel(), index);
			getLayoutInternal().addChild(cell, index);
		}
	}
	
	protected void removeContent(PTreeIndex index, Object oldContent) {
//		System.out.println("PTree.removeContent() idx="+index+", obj="+oldContent+", cell="+getCellComponent(index));
		if (isParentExpanded(index)) {
			getSelection().removeSelection(index);
			getLayoutInternal().removeChild(getCellComponent(index));
		}
	}
	
	protected void changeContent(PTreeIndex index, Object oldContent) {
		if (isParentExpanded(index)) {
			getCellComponent(index).setElement(getModel(), index);
		}
	}
	
	protected boolean isParentExpanded(PTreeIndex index) {
		return index.getDepth() == 0 || isIndexVisible(index);
	}
	
	protected void onContentAdded(PTreeIndex index, Object content) {
		System.out.println("--- ADD --- "+content+", idx="+index);
		addContent(index, content);
		
		System.out.println(getModel());
		getLayoutInternal().debug();
	}
	
	protected void onContentRemoved(PTreeIndex index, Object content) {
		System.out.println("--- REMOVE --- "+content+", idx="+index);
		removeContent(index, content);
		
		System.out.println(getModel());
		getLayoutInternal().debug();
	}
	
	protected void onContentChanged(PTreeIndex index, Object content) {
		changeContent(index, content);
	}
	
	protected void onSelectionAdded(PTreeIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(true);
		}
	}
	
	protected void onSelectionRemoved(PTreeIndex index) {
		PCellComponent cellComp = getCellComponent(index);
		if (cellComp != null) {
			cellComp.setSelected(false);
		}
	}
	
	protected void onLastSelectedChanged(PModelIndex oldIdx, PModelIndex newIdx) {
		if (hasFocus()) {
			fireReRenderEvent();
		}
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
	
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseReleased(PMouse mouse, MouseButton btn) {
		if (isDragTagged && mouse.isReleased(MouseButton.DRAG_AND_DROP)) {
			isDragTagged = false;
		}
	}
	
	protected void onMouseMoved(PMouse mouse) {
		if (!isDragTagged) {
			return;
		}
		PDnDSupport dndSup = getDragAndDropSupport();
		if (dndSup != null && mouse.isPressed(MouseButton.DRAG_AND_DROP)) {
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
	
	public void addObs(PModelObs obs) {
		obsListModel.add(obs);
		if (getModel() != null) {
			getModel().addObs(obs);
		}
	}
	
	public void removeObs(PModelObs obs) {
		obsListModel.remove(obs);
		if (getModel() != null) {
			getModel().removeObs(obs);
		}
	}
	
	public void addObs(PSelectionObs obs) {
		obsListSelection.add(obs);
		if (getSelection() != null) {
			getSelection().addObs(obs);
		}
	}
	
	public void removeObs(PSelectionObs obs) {
		obsListSelection.remove(obs);
		if (getSelection() != null) {
			getSelection().removeObs(obs);
		}
	}
	
}