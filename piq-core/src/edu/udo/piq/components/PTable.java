package edu.udo.piq.components;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PRenderer;
import edu.udo.piq.components.defaults.DefaultPTableCellFactory;
import edu.udo.piq.components.defaults.DefaultPTableModel;
import edu.udo.piq.components.defaults.DefaultPTableSelection;
import edu.udo.piq.layouts.PTableLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class PTable extends AbstractPLayoutOwner {
	
	private final PTableModelObs modelObs = new PTableModelObs() {
		public void rowRemoved(PTableModel model, int rowIndex) {
		}
		public void rowAdded(PTableModel model, int rowIndex) {
		}
		public void cellChanged(PTableModel model, Object cell, int columnIndex, int rowIndex) {
			PTable.this.cellChanged(new PTableCell(columnIndex, rowIndex));
		}
	};
	private final PTableSelectionObs selectionObs = new PTableSelectionObs() {
		public void selectionRemoved(PTableSelection selection, PTableCell cell) {
			PTable.this.selectionChanged(cell, false);
		}
		public void selectionAdded(PTableSelection selection, PTableCell cell) {
			PTable.this.selectionChanged(cell, true);
		}
	};
	private final Map<PTableCell, PTableCellComponent> cellToCompMap = new HashMap<>();
	private PTableSelection selection;
	private PTableModel model;
	private PTableCellFactory cellFac;
	
	public PTable() {
		super();
		setLayout(new PTableLayout(this));
		setModel(new DefaultPTableModel(new Object[0][0]));
		setSelection(new DefaultPTableSelection());
		setCellFactory(new DefaultPTableCellFactory());
	}
	
	public PTableLayout getLayout() {
		return (PTableLayout) super.getLayout();
	}
	
	public void setCellFactory(PTableCellFactory factory) {
		cellFac = factory;
	}
	
	public PTableCellFactory getCellFactory() {
		return cellFac;
	}
	
	public void setSelection(PTableSelection selection) {
		if (getSelection() != null) {
			getSelection().removeObs(selectionObs);
			getSelection().setModel(null);
		}
		this.selection = selection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
			getSelection().setModel(getModel());
		}
	}
	
	public PTableSelection getSelection() {
		return selection;
	}
	
	public void setModel(PTableModel model) {
		if (getModel() != null) {
			if (getSelection() != null) {
				getSelection().setModel(null);
			}
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
			if (getSelection() != null) {
				getSelection().setModel(getModel());
			}
			getModel().addObs(modelObs);
		}
		modelChanged();
	}
	
	public PTableModel getModel() {
		return model;
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		int fx = bounds.getFinalX();
		int fy = bounds.getFinalY();
		
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(x + 1, y + 1, fx - 1, fy - 1);
		renderer.setColor(PColor.BLACK);
		renderer.strokeQuad(x, y, fx, fy, 1);
		
		PTableLayout layout = getLayout();
		int gap = layout.getGap();
		int colCount = getModel().getColumnCount();
		int lineX = 0;
		for (int col = 0; col < colCount; col++) {
			lineX += layout.getColumnSize(col);
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x + lineX, y, x + lineX + gap, fy);
			lineX += gap;
		}
		int rowCount = getModel().getRowCount();
		int lineY = 0;
		for (int row = 0; row < rowCount; row++) {
			lineY += layout.getRowSize(row);
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x, y + lineY, fx, y + lineY + gap);
			lineY += gap;
		}
	}
	
	private void doNothing() {}
	
//	protected void onUpdate() {
//		if (getModel() == null || getSelection() == null) {
//			return;
//		}
//		PMouse mouse = PCompUtil.getMouseOf(this);
//		PKeyboard keyboard = PCompUtil.getKeyboardOf(this);
//		if (mouse == null) {
//			return;
//		}
//		
//		if (mouse.isTriggered(MouseButton.LEFT) 
//				&& PCompUtil.isWithinClippedBounds(this, mouse.getX(), mouse.getY())) {
//			
//			PComponent selected = getLayout().getChildAt(mouse.getX(), mouse.getY());
//			if (selected != null) {
//				PTableCell cell = (PTableCell) getLayout().getChildConstraint(selected);
//				
//				if (keyboard.isPressed(Key.CTRL)) {
//					toggleSelection(cell);
//				} else if (keyboard.isPressed(Key.SHIFT)) {
//					rangeSelection(cell);
//				} else {
//					setSelection(cell);
//				}
//			}
//		}
//	}
	
	private void toggleSelection(PTableCell cell) {
		if (selection.isSelected(cell)) {
			selection.removeSelection(cell);
		} else {
			selection.addSelection(cell);
		}
	}
	
	private void rangeSelection(PTableCell cell) {
		
	}
	
	private void setSelection(PTableCell cell) {
		selection.clearSelection();
		selection.addSelection(cell);
	}
	
	private void modelChanged() {
		int colCount = getModel().getColumnCount();
		int rowCount = getModel().getRowCount();
		getLayout().clearChildren();
		getLayout().resize(colCount, rowCount);
		cellToCompMap.clear();
		
		for (int col = 0; col < getModel().getColumnCount(); col++) {
			for (int row = 0; row < getModel().getRowCount(); row++) {
				cellAdded(col, row);
			}
		}
	}
	
	private void cellAdded(int col, int row) {
		PTableCell cell = new PTableCell(col, row);
		PTableCellComponent cellComp = getCellFactory().getCellComponentFor(getModel(), cell);
		cellToCompMap.put(cell, cellComp);
		getLayout().addChild(cellComp, cell);
	}
	
	private void cellChanged(PTableCell cell) {
		PTableCellComponent comp = cellToCompMap.get(cell);
		if (comp != null) {
			comp.cellChanged(getModel(), cell);
		}
	}
	
	private void selectionChanged(PTableCell cell, boolean value) {
		PTableCellComponent comp = cellToCompMap.get(cell);
		if (comp != null) {
			comp.setSelected(value);
		}
	}
	
}