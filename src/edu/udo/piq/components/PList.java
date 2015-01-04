package edu.udo.piq.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PLayout;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPListCellFactory;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPListSelection;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPKeyboardObs;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.AbstractPMouseObs;
import edu.udo.piq.tools.UnmodifiablePLayoutView;
import edu.udo.piq.util.PCompUtil;

public class PList extends AbstractPLayoutOwner {
	
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
		public void buttonTriggered(PMouse mouse, MouseButton btn) {
			if (getModel() == null || getSelection() == null) {
				return;
			}
			PKeyboard keyboard = PCompUtil.getKeyboardOf(PList.this);
			if (PCompUtil.isWithinClippedBounds(PList.this, mouse.getX(), mouse.getY())) {
				PComponent selected = getListLayout().getChildAt(mouse.getX(), mouse.getY());
				if (selected != null) {
					Integer index = Integer.valueOf(getListLayout().getChildIndex(selected));
					
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
	};
	private final PListModelObs modelObs = new PListModelObs() {
		public void elementAdded(PListModel model, Object element, int index) {
			PList.this.elementAdded(index);
		}
		public void elementRemoved(PListModel model, Object element, int index) {
			PList.this.elementRemoved(index);
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
	private final PLayout unmodifiableLayoutView;
	private PListSelection selection;
	private PListModel model;
	private PListCellFactory cellFac;
	
	public PList() {
		setLayout(new PListLayout(this, ListAlignment.FROM_TOP, 1));
		setModel(new DefaultPListModel());
		setSelection(new DefaultPListSelection());
		setCellFactory(new DefaultPListCellFactory());
		unmodifiableLayoutView = new UnmodifiablePLayoutView(super.getLayout());
	}
	
	public PLayout getLayout() {
		return unmodifiableLayoutView;
	}
	
	protected PListLayout getListLayout() {
		return (PListLayout) super.getLayout();
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
		PComponent cellComp = getListLayout().getChildAt(x, y);
		if (cellComp == null) {
			return -1;
		}
		return getListLayout().getChildIndex(cellComp);
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
		getListLayout().clearChildren();
		elementToCompMap.clear();
		
		for (int i = 0; i < getModel().getElementCount(); i++) {
			elementAdded(i);
		}
	}
	
	private void elementAdded(int index) {
		Object element = getModel().getElement(Integer.valueOf(index));
		PListCellComponent cellComp = getCellFactory().getCellComponentFor(getModel(), index);
		elementToCompMap.put(element, cellComp);
		getListLayout().addChild(cellComp, null);
	}
	
	private void elementRemoved(int index) {
		Object element = getModel().getElement(Integer.valueOf(index));
		PComponent cellComp = elementToCompMap.get(element);
		getListLayout().removeChild(cellComp);
	}
	
	private void elementChanged(int index) {
		Object elem = getModel().getElement(index);
		PListCellComponent comp = elementToCompMap.get(elem);
		if (comp != null) {
			comp.elementChanged(getModel(), index);
		}
	}
	
	private void selectionChanged(int index, boolean value) {
		Object elem = getModel().getElement(index);
		PListCellComponent comp = elementToCompMap.get(elem);
		if (comp != null && comp.isSelected() != value) {
			comp.setSelected(value);
		}
	}
	
}