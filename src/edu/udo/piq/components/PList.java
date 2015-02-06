package edu.udo.piq.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPListCellFactory;
import edu.udo.piq.components.defaults.DefaultPListDnDSupport;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPListSelection;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPKeyboardObs;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.AbstractPMouseObs;
import edu.udo.piq.util.PCompUtil;

public class PList extends AbstractPLayoutOwner {
	
	private static final int DRAG_AND_DROP_DISTANCE = 16;
	
	private final PKeyboardObs keyObs = new AbstractPKeyboardObs() {
		public void keyPressed(PKeyboard keyboard, Key key) {
			if (!PCompUtil.hasFocus(PList.this) || getSelection() == null) {
				return;
			}
			PListSelection selection = getSelection();
			Set<Integer> allSelected = selection.getSelection();
			if (allSelected.isEmpty()) {
				return;
			}
			if (key == Key.UP || key == Key.LEFT) {
				int index = Collections.min(allSelected) - 1;
				if (keyboard != null && keyboard.isPressed(Key.SHIFT)) {
					rangeSelection(index);
				} else {
					setSelection(index);
				}
			} else if (key == Key.DOWN || key == Key.RIGHT) {
				int index = Collections.max(allSelected) + 1;
				if (keyboard != null && keyboard.isPressed(Key.SHIFT)) {
					rangeSelection(index);
				} else {
					setSelection(index);
				}
			}
		}
	};
	private final PMouseObs mouseObs = new AbstractPMouseObs() {
		private int lastMouseX;
		private int lastMouseY;
		private boolean isSelected = false;
		
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (getModel() == null || getSelection() == null) {
				return;
			}
			PKeyboard keyboard = PCompUtil.getKeyboardOf(PList.this);
			int mx = mouse.getX();
			int my = mouse.getY();
			if (PCompUtil.isWithinClippedBounds(PList.this, mx, my)) {
				PComponent selected = getLayout().getChildAt(mx, my);
				if (selected != null) {
					lastMouseX = mx;
					lastMouseY = my;
					isSelected = true;
					
					Integer index = Integer.valueOf(getLayout().getChildIndex(selected));
					if (keyboard != null && keyboard.isPressed(Key.CTRL)) {
						toggleSelection(index);
					} else if (keyboard != null && keyboard.isPressed(Key.SHIFT)) {
						rangeSelection(index);
					} else {
						setSelection(index);
					}
					if (!PCompUtil.hasFocus(PList.this)) {
						PCompUtil.takeFocus(PList.this);
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
			if (isSelected && mouse.isPressed(MouseButton.LEFT) && dndSup != null) {
				int mx = mouse.getX();
				int my = mouse.getY();
				int disX = Math.abs(lastMouseX - mx);
				int disY = Math.abs(lastMouseY - my);
				int dis = disX + disY;
				if (dis >= DRAG_AND_DROP_DISTANCE) {
					if (dndSup.canDrag(PList.this, mx, my)) {
						dndSup.startDrag(PList.this, mx, my);
					}
				}
			}
		}
	};
	private final PListModelObs modelObs = new PListModelObs() {
		public void elementAdded(PListModel model, Object element, int index) {
			PList.this.elementAdded(index);
		}
		public void elementRemoved(PListModel model, Object element, int index) {
			PList.this.elementRemoved(element);
		}
		public void elementChanged(PListModel model, Object element, int index) {
			PList.this.elementChanged(index);
		}
	};
	private final PListSelectionObs selectionObs = new PListSelectionObs() {
		public void selectionRemoved(PListSelection selection, int index) {
			PList.this.selectionChanged(index, false);
		}
		public void selectionAdded(PListSelection selection, int index) {
			PList.this.selectionChanged(index, true);
		}
	};
	private final Map<Object, PListCellComponent> elementToCompMap = new HashMap<>();
	private PDnDSupport dndSup;
	private PListSelection selection;
	private PListModel model;
	private PListCellFactory cellFac;
	
	public PList() {
		setLayout(new PListLayout(this, ListAlignment.FROM_TOP, 1));
		setDragAndDropSupport(new DefaultPListDnDSupport());
		setModel(new DefaultPListModel());
		setSelection(new DefaultPListSelection());
		setCellFactory(new DefaultPListCellFactory());
	}
	
	public PListLayout getLayout() {
		return (PListLayout) super.getLayout();
	}
	
	public void setDragAndDropSupport(PDnDSupport support) {
		dndSup = support;
	}
	
	public PDnDSupport getDragAndDropSupport() {
		return dndSup;
	}
	
	public void setCellFactory(PListCellFactory factory) {
		cellFac = factory;
	}
	
	public PListCellFactory getCellFactory() {
		return cellFac;
	}
	
	public void setSelection(PListSelection selection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
		}
		this.selection = selection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
		}
	}
	
	public PListSelection getSelection() {
		return selection;
	}
	
	public void setModel(PListModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			getModel().addObs(modelObs);
		}
		modelChanged();
	}
	
	public PListModel getModel() {
		return model;
	}
	
	public int getIndexAt(int x, int y) {
		PComponent cellComp = getLayout().getChildAt(x, y);
		if (cellComp == null) {
			return -1;
		}
		return getLayout().getChildIndex(cellComp);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	protected PKeyboardObs getKeyboardObs() {
		return keyObs;
	}
	
	protected PMouseObs getMouseObs() {
		return mouseObs;
	}
	
	private void rangeSelection(Integer index) {
		PListSelection selection = getSelection();
		for (int i = index.intValue(); i >= 0; i--) {
			if (selection.isSelected(Integer.valueOf(i))) {
				for (; i <= index.intValue(); i++) {
					selection.addSelection(Integer.valueOf(i));
				}
				return;
			}
		}
		int elemCount = getModel().getElementCount();
		for (int i = index.intValue(); i < elemCount; i++) {
			if (selection.isSelected(Integer.valueOf(i))) {
				for (; i >= index.intValue(); i--) {
					selection.addSelection(Integer.valueOf(i));
				}
				return;
			}
		}
		selection.addSelection(index);
	}
	
	private void toggleSelection(Integer index) {
		if (selection.isSelected(index)) {
			selection.removeSelection(index);
		} else {
			selection.addSelection(index);
		}
	}
	
	private void setSelection(Integer index) {
		selection.clearSelection();
		selection.addSelection(index);
	}
	
	private void modelChanged() {
		getLayout().clearChildren();
		elementToCompMap.clear();
		
		for (int i = 0; i < getModel().getElementCount(); i++) {
			elementAdded(i);
		}
	}
	
	private void elementAdded(int index) {
		if (getSelection() != null) {
			getSelection().clearSelection();
		}
		Object element = getModel().getElement(Integer.valueOf(index));
		PListCellComponent cellComp = getCellFactory().getCellComponentFor(getModel(), index);
		elementToCompMap.put(element, cellComp);
		getLayout().addChild(cellComp, Integer.valueOf(index));
	}
	
	private void elementRemoved(Object element) {
		if (getSelection() != null) {
			getSelection().clearSelection();
		}
		PComponent cellComp = elementToCompMap.get(element);
		getLayout().removeChild(cellComp);
		elementToCompMap.remove(element);
	}
	
	private void elementChanged(int index) {
		Object elem = getModel().getElement(index);
		PListCellComponent comp = elementToCompMap.get(elem);
		if (comp != null) {
			comp.elementChanged(getModel(), index);
		}
	}
	
	private void selectionChanged(int index, boolean value) {
		if (index >= getModel().getElementCount()) {
			return;
		}
		Object elem = getModel().getElement(index);
		PListCellComponent comp = elementToCompMap.get(elem);
		if (comp != null && comp.isSelected() != value) {
			comp.setSelected(value);
		}
//		System.out.println(getSelection().getSelection());
	}
	
}