package edu.udo.piq.components;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPTreeCellFactory;
import edu.udo.piq.components.defaults.DefaultPTreeDnDSupport;
import edu.udo.piq.components.defaults.DefaultPTreeModel;
import edu.udo.piq.components.defaults.DefaultPTreeSelection;
import edu.udo.piq.layouts.PTreeLayout;
import edu.udo.piq.layouts.PTreeLayout.Constraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PTree extends AbstractPLayoutOwner {
	
	private static final int DRAG_AND_DROP_DISTANCE = 16;
	
	private final PMouseObs mouseObs = new PMouseObs() {
		private int lastMouseX;
		private int lastMouseY;
		private boolean isSelected = false;
		
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (getModel() == null || getSelection() == null) {
				return;
			}
			if (isMouseOverThisOrChild()) {
				PKeyboard keyboard = getKeyboard();
				int mx = mouse.getX();
				int my = mouse.getY();
				PTreeCellComponent selected = (PTreeCellComponent) getLayout().getChildAt(mx, my);
				if (selected != null) {
					lastMouseX = mx;
					lastMouseY = my;
					isSelected = true;
					
					Object node = selected.getNode();
					if (keyboard != null && keyboard.isModifierToggled(Modifier.CTRL)) {
						toggleSelection(node);
					} else if (keyboard != null && keyboard.isPressed(Key.SHIFT)) {
						rangeSelection(node);
					} else {
						setSelection(node);
					}
					if (!hasFocus()) {
						takeFocus();
					}
				}
			}
		}
		public void buttonReleased(PMouse mouse, MouseButton btn) {
			if (isSelected && btn == MouseButton.LEFT) {
				isSelected = false;
			}
		}
		public void mouseMoved(PMouse mouse) {
			PDnDSupport dndSup = getDragAndDropSupport();
			if (dndSup != null && isSelected && mouse.isPressed(MouseButton.LEFT)) {
				int mx = mouse.getX();
				int my = mouse.getY();
				int disX = Math.abs(lastMouseX - mx);
				int disY = Math.abs(lastMouseY - my);
				int dis = disX + disY;
				if (dis >= DRAG_AND_DROP_DISTANCE) {
					if (dndSup.canDrag(PTree.this, mx, my)) {
						dndSup.startDrag(PTree.this, mx, my);
					}
				}
			}
		}
	};
	private final PTreeModelObs modelObs = new PTreeModelObs() {
		public void nodeAdded(PTreeModel model, Object parent, Object child, int index) {
			PTree.this.nodeAdded(child);
		}
		public void nodeRemoved(PTreeModel model, Object parent, Object child, int index) {
			PTree.this.nodeRemoved(child);
		}
		public void nodeChanged(PTreeModel model, Object parent, Object child, int index) {
			PTree.this.nodeChanged(child);
		}
	};
	private final PTreeSelectionObs selectionObs = new PTreeSelectionObs() {
		public void selectionAdded(PTreeSelection selection, Object node) {
			PTree.this.selectionChanged(node, true);
		}
		public void selectionRemoved(PTreeSelection selection, Object node) {
			PTree.this.selectionChanged(node, false);
		}
	};
	private final Map<Object, PTreeCellComponent> elementToCompMap = new HashMap<>();
	private PDnDSupport dndSup;
	private PTreeSelection selection;
	private PTreeModel model;
	private PTreeCellFactory cellFac;
//	private boolean dropHighlight = false;
	
	public PTree() {
		this(new DefaultPTreeModel());
	}
	
	public PTree(PTreeModel model) {
		super();
		setLayout(new PTreeLayout(this));
		setDragAndDropSupport(new DefaultPTreeDnDSupport());
		setSelection(new DefaultPTreeSelection());
		setCellFactory(new DefaultPTreeCellFactory());
		setModel(model);
//		addObs(keyObs);
		addObs(mouseObs);
	}
	
	protected PTreeLayout getLayoutInternal() {
		return (PTreeLayout) super.getLayout();
	}
	
//	public void setDropHighlighted(boolean isHighlighted) {
//		if (dropHighlight != isHighlighted) {
//			dropHighlight = isHighlighted;
//			fireReRenderEvent();
//		}
//	}
//	
//	public boolean isDropHighlighted() {
//		return dropHighlight;
//	}
	
	public void setDragAndDropSupport(PDnDSupport support) {
		dndSup = support;
	}
	
	public PDnDSupport getDragAndDropSupport() {
		return dndSup;
	}
	
	public void setCellFactory(PTreeCellFactory factory) {
		cellFac = factory;
	}
	
	public PTreeCellFactory getCellFactory() {
		return cellFac;
	}
	
	public void setSelection(PTreeSelection selection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
		}
		this.selection = selection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
		}
	}
	
	public PTreeSelection getSelection() {
		return selection;
	}
	
	public void setModel(PTreeModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		getSelection().setModel(getModel());
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		modelChanged();
	}
	
	public PTreeModel getModel() {
		return model;
	}
	
	public PTreePosition getPositionAt(int x, int y) {
		PTreeCellComponent cellComp = getCellComponentAt(x, y);
		if (cellComp == null) {
			return null;
		}
		return new PTreePosition(getModel(), cellComp.getNode());
	}
	
	public PTreeCellComponent getCellComponentAt(int x, int y) {
		return (PTreeCellComponent) getLayoutInternal().getComponentAt(x, y);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		
		renderer.setColor(PColor.BLACK);
		
		PTreeLayout layout = getLayoutInternal();
		Deque<PTreeCellComponent> stack = new LinkedList<>();
		stack.push((PTreeCellComponent) layout.getRootComponent());
		while (!stack.isEmpty()) {
			PTreeCellComponent current = stack.pop();
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
				
				stack.push((PTreeCellComponent) child);
			}
		}
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	private void modelChanged() {
		getLayoutInternal().clearChildren();
		elementToCompMap.clear();
		
		PTreeModel model = getModel();
		if (model.getRoot() != null) {
			Deque<Object> stack = new LinkedList<>();
			stack.push(model.getRoot());
			while (!stack.isEmpty()) {
				Object node = stack.pop();
				nodeAdded(node);
				
				for (int i = 0; i < model.getChildCount(node); i++) {
					stack.push(model.getChild(node, i));
				}
			}
		}
	}
	
	protected void rangeSelection(Object element) {
//		PListModel model = getModel();
//		int index = model.getIndexOfElement(element);
//		PListSelection selection = getSelection();
//		for (int i = index; i >= 0; i--) {
//			Object elem = model.getElement(i);
//			if (selection.isSelected(elem)) {
//				for (; i <= index; i++) {
//					selection.addSelection(model.getElement(i));
//				}
//				return;
//			}
//		}
//		int elemCount = model.getElementCount();
//		for (int i = index; i < elemCount; i++) {
//			Object elem = model.getElement(i);
//			if (selection.isSelected(elem)) {
//				for (; i >= index; i--) {
//					selection.addSelection(model.getElement(i));
//				}
//				return;
//			}
//		}
//		selection.addSelection(element);
	}
	
	protected void toggleSelection(Object element) {
		if (selection.isSelected(element)) {
			selection.removeSelection(element);
		} else {
			selection.addSelection(element);
		}
	}
	
	protected void setSelection(Object element) {
		selection.clearSelection();
		selection.addSelection(element);
	}
	
	protected void nodeAdded(Object node) {
		PTreeCellComponent cellComp = getCellFactory().getCellComponentFor(getModel(), node);
		elementToCompMap.put(node, cellComp);
		
		Object parent = getModel().getParentOf(node);
		int index = getModel().getChildIndex(parent, node);
		PTreeCellComponent parentComp = elementToCompMap.get(parent);
		
		getLayoutInternal().addChild(cellComp, new Constraint(parentComp, index));
	}
	
	protected void nodeRemoved(Object element) {
		if (getSelection() != null) {
			getSelection().removeSelection(element);
		}
		PComponent cellComp = elementToCompMap.get(element);
		getLayoutInternal().removeChild(cellComp);
		elementToCompMap.remove(element);
	}
	
	protected void nodeChanged(Object element) {
		PTreeCellComponent comp = elementToCompMap.get(element);
		if (comp != null) {
			comp.setNode(getModel(), element);
		}
	}
	
	protected void selectionChanged(Object element, boolean value) {
		PTreeCellComponent comp = elementToCompMap.get(element);
		if (comp.isSelected() != value) { 
			comp.setSelected(value);
		}
	}
	
}