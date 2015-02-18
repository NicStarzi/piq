package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.udo.piq.components.PTableCell;
import edu.udo.piq.components.PTableSelection;
import edu.udo.piq.tools.AbstractPTableSelection;

public class DefaultPTableSelection extends AbstractPTableSelection implements PTableSelection {
	
	private final Set<PTableCell> selection = new HashSet<>();
	
	public void addSelection(PTableCell cell) {
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
	
	private void addColumn(PTableCell cell) {
		int col = cell.getColumnIndex();
		for (int row = 0; row < getModel().getRowCount(); row++) {
			addCell(new PTableCell(col, row));
		}
	}
	
	private void addRow(PTableCell cell) {
		int row = cell.getRowIndex();
		for (int col = 0; col < getModel().getColumnCount(); col++) {
			addCell(new PTableCell(col, row));
		}
	}
	
	private void addCell(PTableCell cell) {
		if (selection.add(cell)) {
			fireSelectionAddedEvent(cell);
		}
	}
	
	public void removeSelection(PTableCell cell) {
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
	
	private void removeColumn(PTableCell cell) {
		int col = cell.getColumnIndex();
		for (int row = 0; row < getModel().getRowCount(); row++) {
			removeCell(new PTableCell(col, row));
		}
	}
	
	private void removeRow(PTableCell cell) {
		int row = cell.getRowIndex();
		for (int col = 0; col < getModel().getColumnCount(); col++) {
			removeCell(new PTableCell(col, row));
		}
	}
	
	private void removeCell(PTableCell cell) {
		if (selection.remove(cell)) {
			fireSelectionRemovedEvent(cell);
		}
	}
	
	public void clearSelection() {
		if (!selection.isEmpty()) {
			List<PTableCell> copy = new ArrayList<>(selection);
			selection.clear();
			for (PTableCell cell : copy) {
				fireSelectionRemovedEvent(cell);
			}
		}
	}
	
	public List<PTableCell> getSelection() {
		return null;
//		return Collections.unmodifiableSet(selection);
	}
	
	public boolean isSelected(PTableCell cell) {
		return selection.contains(cell);
	}
	
	public void setSelectionMode(SelectionMode selectionMode) {
		List<PTableCell> oldSelection = null;
		if (!selection.isEmpty()) {
			oldSelection = new ArrayList<>(selection);
			selection.clear();
		}
		super.setSelectionMode(selectionMode);
		if (oldSelection != null) {
			for (PTableCell cell : oldSelection) {
				addSelection(cell);
			}
		}
	}
	
}