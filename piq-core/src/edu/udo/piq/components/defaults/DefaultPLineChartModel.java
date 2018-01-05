package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.components.charts.PLineChartModel;
import edu.udo.piq.components.util.PUndoRedoStack;
import edu.udo.piq.tools.AbstractPLineChartModel;

public class DefaultPLineChartModel extends AbstractPLineChartModel implements PLineChartModel {
	
	private final List<Integer> dataList = new ArrayList<>();
	
	public int getDataCount() {
		return dataList.size();
	}
	
	public int getDataPoint(int index) {
		return dataList.get(index);
	}
	
	public boolean canAddDataPoint(int index, Object value) {
		return index >= 0 && index <= getDataCount() && value instanceof Integer;
	}
	
	public void addDataPoint(int index, Object value) {
		dataList.add(index, (Integer) value);
		fireDataAddedEvent(index);
	}
	
	public boolean canRemoveDataPoint(int index) {
		return index >= 0 && index < getDataCount();
	}
	
	public void removeDataPoint(int index) {
		dataList.remove(index);
		fireDataRemovedEvent(index);
	}
	
	public boolean canSetDataPoint(int index, Object value) {
		return index >= 0 && index < getDataCount() && value instanceof Integer;
	}
	
	public void setDataPoint(int index, Object value) {
		Integer intVal = (Integer) value;
		if (intVal.intValue() != getDataPoint(index)) {
			dataList.set(index, (Integer) value);
			fireDataChangedEvent(index);
		}
	}
	
	public PUndoRedoStack getHistory() {
		return null;
	}
	
}