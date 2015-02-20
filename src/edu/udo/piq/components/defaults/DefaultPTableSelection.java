package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.udo.piq.components.PTablePosition;
import edu.udo.piq.components.PTableSelection;
import edu.udo.piq.tools.AbstractPTableSelection;

public class DefaultPTableSelection extends AbstractPTableSelection implements PTableSelection {
	
	private final Set<PTablePosition> selection = new HashSet<>();
	
	public void addSelection(PTablePosition cell) {
		switch (getSelectionMode()) {
		case SINGLE_COLUMN:
			clearSelection();
			addColumn(cell);
			break;
		case MULTIPLE_COLUMN:
			addColumn(cell);
			break;
		case SINGLE_ROW:
			clearSelection();
			addRow(cell);
			break;
		case MULTIPLE_ROW:
			addRow(cell);
			break;
		case SINGLE_CELL:
			clearSelection();
			addCell(cell);
			break;
		case MULTIPLE_CELL:
			addCell(cell);
			break;
		case NO_SELECTION:
		default:
			break;
		}
	}
	
	private void addColumn(PTablePosition cell) {
		int col = cell.getColumnIndex();
		for (int row = 0; row < getModel().getRowCount(); row++) {
			addCell(new PTablePosition(col, row));
		}
	}
	
	private void addRow(PTablePosition cell) {
		int row = cell.getRowIndex();
		for (int col = 0; col < getModel().getColumnCount(); col++) {
			addCell(new PTablePosition(col, row));
		}
	}
	
	private void addCell(PTablePosition cell) {
		if (selection.add(cell)) {
			fireSelectionAddedEvent(cell);
		}
	}
	
	public void removeSelection(PTablePosition cell) {
		switch (getSelectionMode()) {
		case SINGLE_COLUMN:
		case MULTIPLE_COLUMN:
			removeColumn(cell);
			break;
		case SINGLE_ROW:
		case MULTIPLE_ROW:
			removeRow(cell);
			break;
		case SINGLE_CELL:
		case MULTIPLE_CELL:
			removeCell(cell);
			break;
		case NO_SELECTION:
		default:
			break;
		}
	}
	
	private void removeColumn(PTablePosition cell) {
		int col = cell.getColumnIndex();
		for (int row = 0; row < getModel().getRowCount(); row++) {
			removeCell(new PTablePosition(col, row));
		}
	}
	
	private void removeRow(PTablePosition cell) {
		int row = cell.getRowIndex();
		for (int col = 0; col < getModel().getColumnCount(); col++) {
			removeCell(new PTablePosition(col, row));
		}
	}
	
	private void removeCell(PTablePosition cell) {
		if (selection.remove(cell)) {
			fireSelectionRemovedEvent(cell);
		}
	}
	
	public void clearSelection() {
		if (!selection.isEmpty()) {
			List<PTablePosition> copy = new ArrayList<>(selection);
			selection.clear();
			for (PTablePosition cell : copy) {
				fireSelectionRemovedEvent(cell);
			}
		}
	}
	
	public List<PTablePosition> getSelection() {
		return null;
//		return Collections.unmodifiableSet(selection);
	}
	
	public boolean isSelected(PTablePosition cell) {
		return selection.contains(cell);
	}
	
	public void setSelectionMode(SelectionMode selectionMode) {
		List<PTablePosition> oldSelection = null;
		if (!selection.isEmpty()) {
			oldSelection = new ArrayList<>(selection);
			selection.clear();
		}
		super.setSelectionMode(selectionMode);
		if (oldSelection != null) {
			for (PTablePosition cell : oldSelection) {
				addSelection(cell);
			}
		}
	}
	
}