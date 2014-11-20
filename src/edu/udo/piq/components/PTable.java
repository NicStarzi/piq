package edu.udo.piq.components;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.defaults.DefaultPTableCellFactory;
import edu.udo.piq.components.defaults.DefaultPTableModel;
import edu.udo.piq.components.defaults.DefaultPTableSelection;
import edu.udo.piq.layouts.PTableLayout;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.UnmodifiablePLayoutView;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.PRenderUtil;

public class PTable extends AbstractPLayoutOwner {
	
	private final PTableModelObs modelObs = new PTableModelObs() {
		public void rowRemoved(PTableModel model, int rowIndex) {
//			PTable.this.elementRemoved(element, cell);
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
	private PLayout unmodifiableLayoutView;
	private PTableSelection selection;
	private PTableModel model;
	private PTableCellFactory cellFac;
	
	public PTable() {
		setLayout(new PTableLayout(this));
		unmodifiableLayoutView = new UnmodifiablePLayoutView(getTableLayout());
		setModel(new DefaultPTableModel(new Object[0][0]));
		setSelection(new DefaultPTableSelection());
		setCellFactory(new DefaultPTableCellFactory());
	}
	
	public PLayout getLayout() {
		return unmodifiableLayoutView;
	}
	
	protected PTableLayout getTableLayout() {
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
		}
		this.selection = selection;
		if (getSelection() != null) {
			getSelection().addObs(selectionObs);
		}
	}
	
	public PTableSelection getSelection() {
		return selection;
	}
	
	public void setModel(PTableModel model) {
		if (getModel() != null) {
			getModel().removeObs(modelObs);
		}
		this.model = model;
		if (getModel() != null) {
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
		PRenderUtil.strokeQuad(renderer, x, y, fx, fy, 1);
		
		int colCount = getModel().getColumnCount();
		PTableLayout layout = getTableLayout();
		int lineX = 0;
		for (int col = 0; col < colCount; col++) {
			lineX += layout.getColumnSize(col);
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x + lineX, y, x + lineX + 1, fy);
		}
		int rowCount = getModel().getRowCount();
		int lineY = 0;
		for (int row = 0; row < rowCount; row++) {
			lineY += layout.getRowSize(row);
			renderer.setColor(PColor.BLACK);
			renderer.drawQuad(x, y + lineY, fx, y + lineY + 1);
		}
	}
	
	protected void onUpdate() {
		if (getModel() == null || getSelection() == null) {
			return;
		}
		PMouse mouse = PCompUtil.getMouseOf(this);
//		PKeyboard keyboard = PCompUtil.getKeyboardOf(this);
		if (mouse == null) {
			return;
		}
		PComponent mouseOwner = mouse.getOwner();
		if (mouseOwner != null && mouseOwner != this) {
			return;
		}
		
		if (mouse.isTriggered(MouseButton.LEFT) 
				&& PCompUtil.isWithinClippedBounds(this, mouse.getX(), mouse.getY())) {
			
//			PComponent selected = getListLayout().getChildAt(mouse.getX(), mouse.getY());
//			if (selected != null) {
//				Integer index = Integer.valueOf(getListLayout().getChildIndex(selected));
//				
//				if (keyboard.isPressed(Key.CTRL)) {
//					toggleSelection(index);
//				} else if (keyboard.isPressed(Key.SHIFT)) {
//					rangeSelection(index);
//				} else {
//					setSelection(index);
//				}
//			}
		}
	}
	
	private void modelChanged() {
		int colCount = getModel().getColumnCount();
		int rowCount = getModel().getRowCount();
		getTableLayout().clearChildren();
		getTableLayout().resize(colCount, rowCount);
		unmodifiableLayoutView = new UnmodifiablePLayoutView(getTableLayout());
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
		getTableLayout().addChild(cellComp, cell);
	}
	
	private void cellChanged(PTableCell cell) {
		PTableCellComponent comp = cellToCompMap.get(cell);
		if (comp != null) {
			comp.cellChanged(getModel(), cell);
		}
	}
	
	private void selectionChanged(PTableCell cell, boolean value) {
		int col = cell.getColumnIndex();
		int row = cell.getRowIndex();
		Object elem = getModel().getCell(col, row);
		PTableCellComponent comp = cellToCompMap.get(elem);
		if (comp != null) {
			comp.setSelected(value);
		}
	}
	
}