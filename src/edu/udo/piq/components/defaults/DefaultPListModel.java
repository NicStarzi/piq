package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.tools.AbstractPListModel;

public class DefaultPListModel extends AbstractPListModel {
	
	protected final List<Object> elements = new ArrayList<>();
	
	public int getElementCount() {
		return elements.size();
	}
	
	public Object getElement(int index) {
		return elements.get(index);
	}
	
	public int getIndexOfElement(Object element) {
		return elements.indexOf(element);
	}
	
	public boolean canAddElement(int index, Object element) {
		return element != null && index >= 0 && index <= getElementCount();
	}
	
	public void addElement(int index, Object element) {
		if (!canAddElement(index, element)) {
			throw new IllegalArgumentException();
		}
		elements.add(index, element);
		fireAddedEvent(element, index);
	}
	
	public boolean canRemoveElement(int index) {
		return index >= 0 && index < getElementCount();
	}
	
	public void removeElement(int index) {
		if (!canRemoveElement(index)) {
			throw new IllegalArgumentException();
		}
		Object element = elements.remove(index);
		fireRemovedEvent(element, Integer.valueOf(index));
	}
	
	public void fireChangedEvent(Object element) {
		int index = elements.indexOf(element);
		if (index == -1) {
			throw new IllegalArgumentException(element+" is not contained");
		}
		fireChangedEvent(element, Integer.valueOf(index));
	}
}