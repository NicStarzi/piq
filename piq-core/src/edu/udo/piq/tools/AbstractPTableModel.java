package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PTableModel;
import edu.udo.piq.components.PTableModelObs;

public abstract class AbstractPTableModel implements PTableModel {
	
	private final List<PTableModelObs> obsList = new CopyOnWriteArrayList<>();
	
	public void addObs(PTableModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTableModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireColumnAddedEvent(int index) {
		for (PTableModelObs obs : obsList) {
			obs.columnAdded(this, index);
		}
	}
	
	protected void fireColumnRemovedEvent(int index) {
		for (PTableModelObs obs : obsList) {
			obs.columnRemoved(this, index);
		}
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