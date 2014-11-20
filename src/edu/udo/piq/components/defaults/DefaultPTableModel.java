package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PTableModel;
import edu.udo.piq.components.PTableModelObs;

public class DefaultPTableModel implements PTableModel {
	
	private final List<PTableModelObs> obsList = new CopyOnWriteArrayList<>();
	private final Object[][] content;
	
	public DefaultPTableModel(Object[][] content) {
		this.content = content;
	}
	
	public int getColumnCount() {
		if (content.length == 0) {
			return 0;
		}
		return content[0].length;
	}
	
	public int getRowCount() {
		return content.length;
	}
	
	public void setCell(Object object, int columnIndex, int rowIndex) {
		content[rowIndex][columnIndex] = object;
		fireCellChangedEvent(columnIndex, rowIndex);
	}
	
	public Object getCell(int columnIndex, int rowIndex) {
		return content[rowIndex][columnIndex];
	}
	
	public void addObs(PTableModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTableModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireRowAddedEvent(int index) {
		for (PTableModelObs obs : obsList) {
			obs.rowAdded(this, index);
		}
	}
	
	protected void fireRowRemovedEvent(int index) {
		for (PTableModelObs obs : obsList) {
			obs.rowRemoved(this, index);
		}
	}
	
	public void fireCellChangedEvent(int col, int row) {
		Object obj = getCell(col, row);
		for (PTableModelObs obs : obsList) {
			obs.cellChanged(this, obj, col, row);
		}
	}
	
}