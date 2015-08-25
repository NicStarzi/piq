package edu.udo.piq.tools;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PTableIndex;
import edu.udo.piq.components.collections.PTableModel;
import edu.udo.piq.components.collections.RemoveImpossible;

public abstract class AbstractPTableModel extends AbstractPModel implements PTableModel {
	
	protected abstract Object getAndSet(int column, int row, Object newContent);
	
	public void add(PModelIndex index, Object content) {
		if (!canAdd(index, content)) {
			throw new AddImpossible(this, index, content);
		}
		PTableIndex ti = asTableIndex(index);
		Object old = getAndSet(ti.getColumn(), ti.getRow(), content);
		if (old != null) {
			fireChangeEvent(index, content);
		} else {
			fireAddEvent(index, content);
		}
	}
	
	public void remove(PModelIndex index) {
		if (!canRemove(index)) {
			throw new RemoveImpossible(this, index);
		}
		PTableIndex ti = asTableIndex(index);
		Object old = getAndSet(ti.getColumn(), ti.getRow(), null);
		fireRemoveEvent(index, old);
	}
	
	public Iterator<PModelIndex> iterator() {
		return new DefaultPTableIterator(this);
	}
	
	public static class DefaultPTableIterator implements Iterator<PModelIndex> {
		
		private final PTableModel model;
		private final int cols;
		private final int cells;
		private int index = 0;
		
		public DefaultPTableIterator(PTableModel model) {
			this.model = model;
			cols = model.getColumnCount();
			cells = cols * model.getRowCount();
		}
		
		public boolean hasNext() {
			for (int i = index; i < cells; i++) {
				int col = i % cols;
				int row = i / cols;
				if (model.get(col, row) != null) {
					index = i;
					return true;
				}
			}
			return false;
		}
		
		public PModelIndex next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			int col = index % cols;
			int row = index / cols;
			index += 1;
			return new PTableIndex(col, row);
		}
		
	}
	
}