package edu.udo.piq.components;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
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
import edu.udo.piq.util.PCompUtil;

public class PList extends AbstractPLayoutOwner {
	
	private final PListModelObs modelObs = new PListModelObs() {
		public void elementAdded(PListModel model, Object element, int index) {
			PList.this.elementAdded(element, index);
		}
		public void elementRemoved(PListModel model, Object element, int index) {
			PList.this.elementRemoved(element, index);
		}
		public void elementChanged(PListModel model, Object element, int index) {
			PList.this.elementChanged(element, index);
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
	private final Map<Object, PListCellComponent> elementToCellMap = new HashMap<>();
	private PListSelection selection;
	private PListModel model;
	private PListCellFactory cellFac;
	
	public PList() {
		setLayout(new PListLayout(this, ListAlignment.FROM_TOP));
		setModel(new DefaultPListModel());
		setSelection(new DefaultPListSelection());
		setCellFactory(new DefaultPListCellFactory());
	}
	
	public PListLayout getLayout() {
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
		PListSelection selection = getSelection();
		if (getModel() == null || selection == null) {
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
			
			PComponent selected = getLayout().getChildAt(mouse.getX(), mouse.getY());
			if (selected != null) {
				Integer index = Integer.valueOf(getLayout().getChildIndex(selected));
				
				if (keyboard.isPressed(Key.CTRL)) {
					if (selection.isSelected(index)) {
						selection.removeSelection(index);
					} else {
						selection.addSelection(index);
					}
				} else if (keyboard.isPressed(Key.SHIFT)) {
					selection.addSelection(index);
				} else {
					selection.clearSelection();
					selection.addSelection(index);
				}
			}
		}
	}
	
	private void elementAdded(Object element, int index) {
		PListCellComponent cellComp = getCellFactory().getCellComponentFor(getModel(), index);
		elementToCellMap.put(element, cellComp);
		getLayout().addChild(cellComp, null);
	}
	
	private void elementRemoved(Object element, int index) {
		PComponent cellComp = elementToCellMap.get(element);
		getLayout().removeChild(cellComp);
	}
	
	private void elementChanged(Object element, int index) {
		Object elem = getModel().getElement(index);
		PListCellComponent comp = elementToCellMap.get(elem);
		if (comp != null) {
			comp.elementChanged(getModel(), index);
		}
	}
	
	private void selectionChanged(int index, boolean value) {
		Object elem = getModel().getElement(index);
		PListCellComponent comp = elementToCellMap.get(elem);
		if (comp != null && comp.isSelected() != value) {
			comp.setSelected(value);
		}
	}
	
}