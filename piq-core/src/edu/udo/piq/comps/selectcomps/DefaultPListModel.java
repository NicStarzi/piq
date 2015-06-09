package edu.udo.piq.comps.selectcomps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultPListModel extends AbstractPModel implements PListModel {
	
	private final List<Object> list = new ArrayList<>();
	
	public DefaultPListModel() {
	}
	
	public DefaultPListModel(Iterable<Object> contents) {
		for (Object o : contents) {
			add(getSize(), o);
		}
	}
	
	public DefaultPListModel(Object[] contents) {
		for (Object o : contents) {
			add(getSize(), o);
		}
	}
	
	public int getSize() {
		return list.size();
	}
	
	public Object get(PModelIndex index) {
		int indexVal = asListIndex(index);
		if (!withinBounds(indexVal, 0)) {
			throw new IllegalIndex(index);
		}
		return get(indexVal);
	}
	
	public Object get(int indexVal) {
		return list.get(indexVal);
	}
	
	public boolean contains(PModelIndex index) {
		return withinBounds(asListIndex(index), 0);
	}
	
	public boolean contains(int index) {
		return withinBounds(index, 0);
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
	
	public boolean canAdd(int index, Object content) {
		return content != null && withinBounds(index, 1);
	}
	
	public void add(PModelIndex index, Object content) {
		if (!canAdd(index, content)) {
			throw new AddImpossible(this, index, content);
		}
		list.add(asListIndex(index), content);
		fireAddEvent(index, content);
	}
	
	public void add(int index, Object content) {
		add(new PListIndex(index), content);
	}
	
	public boolean canRemove(PModelIndex index) {
		return contains(index);
	}
	
	public boolean canRemove(int index) {
		return contains(index);
	}
	
	public void remove(PModelIndex index) {
		if (!canRemove(index)) {
			throw new RemoveImpossible(this, index);
		}
		Object oldContent = list.remove(asListIndex(index));
		fireRemoveEvent(index, oldContent);
	}
	
	public void remove(int index) {
		remove(new PListIndex(index));
	}
	
	public Iterator<Object> iterator() {
		return list.iterator();
	}
	
	protected boolean withinBounds(int indexVal, int sizeOffset) {
		return indexVal >= 0 && indexVal < getSize() + sizeOffset;
	}
	
	protected int asListIndex(PModelIndex index) {
		if (index == null) {
			throw new NullPointerException("index == null");
		}
		if (index instanceof PListIndex) {
			return ((PListIndex) index).getIndexValue();
		}
		throw new WrongIndexType(index, PListIndex.class);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(list.toString());
		return sb.toString();
	}
	
}