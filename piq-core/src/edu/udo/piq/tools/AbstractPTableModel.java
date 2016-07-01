package edu.udo.piq.tools;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTableCellIndex;
import edu.udo.piq.components.collections.PTableHeaderObs;
import edu.udo.piq.components.collections.PTableModel;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPTableModel extends AbstractPModel implements PTableModel {
	
	protected final ObserverList<PTableHeaderObs> obsList = 
			PCompUtil.createDefaultObserverList();
	
	public Iterator<PModelIndex> iterator() {
		return new DefaultPTableIterator(this);
	}
	
	public void addObs(PTableHeaderObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PTableHeaderObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireHeaderStatusChangedEvent() {
		obsList.fireEvent((obs) -> obs.onHeaderStatusChanged(this));
	}
	
	protected void fireHeaderElementChangedEvent(int column, Object oldElement) {
		obsList.fireEvent((obs) -> obs.onHeaderElementChanged(this, column, oldElement));
	}
	
	public static class DefaultPTableIterator implements Iterator<PModelIndex> {
		
		private final int cols;
		private final int cells;
		private int index = 0;
		
		public DefaultPTableIterator(PTableModel model) {
			cols = model.getColumnCount();
			cells = cols * model.getRowCount();
		}
		
		public boolean hasNext() {
			return index < cells;
		}
		
		public PModelIndex next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			int col = index % cols;
			int row = index / cols;
			index += 1;
			return new PTableCellIndex(col, row);
		}
		
	}
	
}