package edu.udo.piq.components.collections.table;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.tools.AbstractPModel;

public abstract class AbstractPTableModel extends AbstractPModel implements PTableModel {
	
	@Override
	public Iterator<PModelIndex> iterator() {
		return new DefaultPTableIterator(this);
	}
	
	public static class DefaultPTableIterator implements Iterator<PModelIndex> {
		
		private final int cols;
		private final int cells;
		private int index = 0;
		
		public DefaultPTableIterator(PTableModel model) {
			cols = model.getColumnCount();
			cells = cols * model.getRowCount();
		}
		
		@Override
		public boolean hasNext() {
			return index < cells;
		}
		
		@Override
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