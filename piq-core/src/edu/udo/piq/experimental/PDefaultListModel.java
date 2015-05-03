package edu.udo.piq.experimental;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PDefaultListModel extends PAbstractModel implements PModel {
	
	private final List<Object> list = new ArrayList<>();
	
	public int getSize() {
		return list.size();
	}
	
	public Object get(PModelIndex index) {
		int indexVal = asListIndex(index);
		if (!withinBounds(indexVal, 0)) {
			throw new IllegalIndex(index);
		}
		return list.get(indexVal);
	}
	
	public boolean contains(PModelIndex index) {
		return withinBounds(asListIndex(index), 0);
	}
	
	public PListIndex getIndexOf(Object content) {
		int indexVal = list.indexOf(content);
		if (indexVal == -1) {
			return null;
		}
		return new PListIndex(indexVal);
	}
	
	public boolean canAdd(PModelIndex index, Object content) {
		return content != null && withinBounds(asListIndex(index), 1);
	}
	
	public void add(PModelIndex index, Object content) {
		if (!canAdd(index, content)) {
			throw new AddImpossible(this, index, content);
		}
		list.add(asListIndex(index), content);
		fireAddEvent(index, content);
	}
	
	public boolean canRemove(PModelIndex index) {
		return contains(index);
	}
	
	public void remove(PModelIndex index) {
		if (!canRemove(index)) {
			throw new RemoveImpossible(this, index);
		}
		Object oldContent = list.remove(asListIndex(index));
		fireRemoveEvent(index, oldContent);
	}
	
	public Iterator<Object> iterator() {
		return list.iterator();
	}
	
	private boolean withinBounds(int indexVal, int sizeOffset) {
		return indexVal >= 0 && indexVal < getSize() + sizeOffset;
	}
	
	private int asListIndex(PModelIndex index) {
		if (index instanceof PListIndex) {
			return ((PListIndex) index).getIndexValue();
		}
		throw new WrongIndexType(index, PListIndex.class);
	}
	
}