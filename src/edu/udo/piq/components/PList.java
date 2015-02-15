package edu.udo.piq.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPListCellFactory;
import edu.udo.piq.components.defaults.DefaultPListDnDSupport;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPListSelection;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.PClipboardUtil;

public class PList extends AbstractPLayoutOwner {
	
	private static final int DRAG_AND_DROP_DISTANCE = 16;
	
	private final PKeyboardObs keyObs = new PKeyboardObs() {
		public void keyTriggered(PKeyboard keyboard, Key key) {
			if (!hasFocus() || getSelection() == null) {
				return;
			}
			if (key == Key.COPY) {
				Set<Object> selectedElements = getSelection().getSelection();
				PClipboardUtil.setClipboardContent(new ArrayList<>(selectedElements));
			} else if (key == Key.PASTE) {
				PListModel model = getModel();
				
				List<Integer> selectedIndices = getSelectedIndices();
				int index;
				if (selectedIndices.isEmpty()) {
					index = model.getElementCount();
				} else {
					index = Collections.max(getSelectedIndices()) + 1;
				}
				
				Iterable<?> elements = PClipboardUtil.getClipboardContent(Iterable.class);
				for (Object element : elements) {
					if (!model.canAddElement(index, element)) {
						return;
					}
				}
				for (Object element : elements) {
					model.addElement(index++, element);
				}
			} else if (key == Key.CUT) {
				PListModel model = getModel();
				
				List<Object> selectedElements = new ArrayList<>(getSelection().getSelection());
				for (Object element : selectedElements) {
					if (!model.canRemoveElement(model.getIndexOfElement(element))) {
						return;
					}
				}
				
				PClipboardUtil.setClipboardContent(selectedElements);
				for (Object element : selectedElements) {
					model.removeElement(model.getIndexOfElement(element));
				}
			}
		}
		public void keyPressed(PKeyboard keyboard, Key key) {
			if (!hasFocus() || getSelection() == null) {
				return;
			}
			PListSelection selection = getSelection();
			Set<Object> selectedElements = selection.getSelection();
			if (selectedElements.isEmpty()) {
				return;
			}
			PListModel model = getModel();
			int index = -1;
			List<Integer> selectedIndices = getSelectedIndices();
			if (key == Key.UP || key == Key.LEFT) {
				index = Collections.min(selectedIndices) - 1;
			} else if (key == Key.DOWN || key == Key.RIGHT) {
				index = Collections.max(selectedIndices) + 1;
			}
			if (index < 0 || index >= model.getElementCount()) {
				return;
			}
			if (keyboard != null && keyboard.isPressed(Key.SHIFT)) {
				rangeSelection(model.getElement(index));
			} else {
				setSelection(model.getElement(index));
			}
		}
	};
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
				PComponent selected = getLayout().getChildAt(mx, my);
				if (selected != null) {
					lastMouseX = mx;
					lastMouseY = my;
					isSelected = true;
					
					Integer index = Integer.valueOf(getLayout().getChildIndex(selected));
					Object element = getModel().getElement(index.intValue());
					if (keyboard != null && keyboard.isModifierToggled(Modifier.CTRL)) {
						toggleSelection(element);
					} else if (keyboard != null && keyboard.isPressed(Key.SHIFT)) {
						rangeSelection(element);
					} else { //if (!getSelection().isSelected(element)) 
						setSelection(element);
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
			PList.this.elementAdded(element);
		}
		public void elementRemoved(PListModel model, Object element, int index) {
			PList.this.elementRemoved(element);
		}
		public void elementChanged(PListModel model, Object element, int index) {
			PList.this.elementChanged(element);
		}
	};
	private final PListSelectionObs selectionObs = new PListSelectionObs() {
		public void selectionAdded(PListSelection selection, Object element) {
			PList.this.selectionChanged(element, true);
		}
		public void selectionRemoved(PListSelection selection, Object element) {
			PList.this.selectionChanged(element, false);
		}
	};
	private final Map<Object, PListCellComponent> elementToCompMap = new HashMap<>();
	private PDnDSupport dndSup;
	private PListSelection selection;
	private PListModel model;
	private PListCellFactory cellFac;
	private boolean dropHighlight = false;
	
	public PList() {
		this(new DefaultPListModel());
	}
	
	public PList(PListModel model) {
		super();
		setLayout(new PListLayout(this, ListAlignment.FROM_TOP, 1));
		setDragAndDropSupport(new DefaultPListDnDSupport());
		setSelection(new DefaultPListSelection());
		setCellFactory(new DefaultPListCellFactory());
		setModel(model);
		addObs(keyObs);
		addObs(mouseObs);
	}
	
	public PListLayout getLayout() {
		return (PListLayout) super.getLayout();
	}
	
	public void setDropHighlighted(boolean isHighlighted) {
		if (dropHighlight != isHighlighted) {
			dropHighlight = isHighlighted;
			fireReRenderEvent();
		}
	}
	
	public boolean isDropHighlighted() {
		return dropHighlight;
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
	
	public PListCellComponent getCellComponentAt(int x, int y) {
		return (PListCellComponent) getLayout().getChildAt(x, y);
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
		
		if (isDropHighlighted()) {
			renderer.setColor(PColor.BLUE);
			
			PListModel model = getModel();
			int highestIndex = model.getElementCount() - 1;
			if (highestIndex == -1) {
				renderer.drawQuad(x, y, fx, y + 2);
			} else {
				PListCellComponent cellComp = (PListCellComponent) getLayout().getChild(highestIndex);
				PBounds cellBounds = cellComp.getBounds();
				int cx = cellBounds.getX();
				int cy = cellBounds.getFinalY();
				int cfx = cellBounds.getFinalX();
				int cfy = cy + 2;
				
				renderer.drawQuad(cx, cy, cfx, cfy);
			}
		}
	}
	
	public boolean isFocusable() {
		return true;
	}
	
	protected void rangeSelection(Object element) {
		PListModel model = getModel();
		int index = model.getIndexOfElement(element);
		PListSelection selection = getSelection();
		for (int i = index; i >= 0; i--) {
			Object elem = model.getElement(i);
			if (selection.isSelected(elem)) {
				for (; i <= index; i++) {
					selection.addSelection(model.getElement(i));
				}
				return;
			}
		}
		int elemCount = model.getElementCount();
		for (int i = index; i < elemCount; i++) {
			Object elem = model.getElement(i);
			if (selection.isSelected(elem)) {
				for (; i >= index; i--) {
					selection.addSelection(model.getElement(i));
				}
				return;
			}
		}
		selection.addSelection(element);
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
	
	private void modelChanged() {
		getLayout().clearChildren();
		elementToCompMap.clear();
		
		PListModel model = getModel();
		for (int i = 0; i < model.getElementCount(); i++) {
			elementAdded(model.getElement(i));
		}
	}
	
	protected void elementAdded(Object element) {
		int index = getModel().getIndexOfElement(element);
		PListCellComponent cellComp = getCellFactory().getCellComponentFor(element);
		elementToCompMap.put(element, cellComp);
		getLayout().addChild(cellComp, Integer.valueOf(index));
	}
	
	protected void elementRemoved(Object element) {
		if (getSelection() != null) {
			getSelection().removeSelection(element);
		}
		PComponent cellComp = elementToCompMap.get(element);
		getLayout().removeChild(cellComp);
		elementToCompMap.remove(element);
	}
	
	protected void elementChanged(Object element) {
		PListCellComponent comp = elementToCompMap.get(element);
		if (comp != null) {
			comp.setElement(element);
		}
	}
	
	protected void selectionChanged(Object element, boolean value) {
		PListCellComponent comp = elementToCompMap.get(element);
		if (comp.isSelected() != value) { 
			comp.setSelected(value);
		}
	}
	
	protected List<Integer> getSelectedIndices() {
		PListSelection selection = getSelection();
		Set<Object> selectedElements = selection.getSelection();
		PListModel model = getModel();
		List<Integer> selectedIndices = new ArrayList<>();
		for (Object element : selectedElements) {
			selectedIndices.add(model.getIndexOfElement(element));
		}
		return selectedIndices;
	}
	
}