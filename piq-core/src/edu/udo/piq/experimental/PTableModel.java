package edu.udo.piq.experimental;

import java.util.Iterator;

public class PTableModel extends PAbstractModel implements PModel {
	
	public Object get(PModelIndex index) {
		return null;
	}
	
	public boolean contains(PModelIndex index) {
		return false;
	}
	
	public PModelIndex getIndexOf(Object content) {
		return null;
	}
	
	public boolean canAdd(PModelIndex index, Object content) {
		return false;
	}
	
	public void add(PModelIndex index, Object content) {
	}
	
	public boolean canRemove(PModelIndex index) {
		return contains(index);
	}
	
	public void remove(PModelIndex index) {
	}
	
	public Iterator<Object> iterator() {
		return null;
	}
	
	private PTableIndex asTableIndex(PModelIndex index) {
		if (index instanceof PTableIndex) {
			return (PTableIndex) index;
		}
		throw new WrongIndexType(index, PTableIndex.class);
	}
	
}