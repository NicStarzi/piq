package edu.udo.piq.components;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PLayout;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPListCellFactory;
import edu.udo.piq.components.defaults.DefaultPListModel;
import edu.udo.piq.components.defaults.DefaultPListSelection;
import edu.udo.piq.layouts.PListLayout;
import edu.udo.piq.layouts.PListLayout.ListAlignment;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.UnmodifiablePLayoutView;
import edu.udo.piq.util.PCompUtil;

public class PList extends AbstractPLayoutOwner {
	
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
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 0, y + 0, fx - 0, fy - 0);
	}
	
	protected void onUpdate() {
		if (getModel() == null || getSelection() == null) {
			return;
		}
		PMouse mouse = PCompUtil.getMouseOf(this);
		PKeyboard keyboard = PCompUtil.getKeyboardOf(this);
		if (mouse == null) {
			return;
		}
		PComponent mouseOwner = mouse.getOwner();
		if (mouseOwner != null && mouseOwner != this) {
			return;
		}
		
		if (mouse.isTriggered(MouseButton.LEFT) 
				&& PCompUtil.isWithinClippedBounds(this, mouse.getX(), mouse.getY())) {
			
			PComponent selected = getListLayout().getChildAt(mouse.getX(), mouse.getY());
			if (selected != null) {
				Integer index = Integer.valueOf(getListLayout().getChildIndex(selected));
				
				if (keyboard.isPressed(Key.CTRL)) {
					toggleSelection(index);
				} else if (keyboard.isPressed(Key.SHIFT)) {
					rangeSelection(index);
				} else {
					setSelection(index);
				}
			}
		}
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