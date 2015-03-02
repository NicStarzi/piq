package edu.udo.piq.components.defaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.PTablePosition;
import edu.udo.piq.components.PTableSelection;
import edu.udo.piq.tools.AbstractPTableSelection;

public class DefaultPTableSelection extends AbstractPTableSelection implements PTableSelection {
	
	private final List<PTablePosition> oneCellList = Arrays.asList(new PTablePosition[] {null});
	
	public void addSelection(PTablePosition cell) {
		if (!cell.equals(getSelectedPosition())) {
			oneCellList.set(0, cell);
			fireSelectionAddedEvent(cell);
		}
	}
	
	public void removeSelection(PTablePosition cell) {
		if (cell.equals(getSelectedPosition())) {
			oneCellList.set(0, null);
			fireSelectionRemovedEvent(cell);
		}
	}
	
	public void clearSelection() {
		PTablePosition selected = getSelectedPosition();
		if (selected != null) {
			removeSelection(getSelectedPosition());
		}
	}
	
	public List<PTablePosition> getSelection() {
		return Collections.unmodifiableList(oneCellList);
	}
	
	public boolean isSelected(PTablePosition cell) {
		return cell.equals(getSelectedPosition());
	}
	
	private PTablePosition getSelectedPosition() {
		return oneCellList.get(0);
	}
	
}