package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.tools.AbstractPListModel;

public class DefaultPListModel extends AbstractPListModel {
	
	protected final List<Object> elements = new ArrayList<>();
	
	public int getElementCount() {
		return elements.size();
	}
	
	public void addElement(Object element) {
		addElement(element, elements.size());
	}
	
	public void addElement(Object element, int index) {
		elements.add(index, element);
		fireAddedEvent(element, index);
	}
	
	public void removeElement(Object element) {
		int index = elements.indexOf(element);
		if (index == -1) {
			throw new IllegalArgumentException(element+" is not contained");
		}
		elements.remove(index);
		fireRemovedEvent(element, index);
	}
	
	public Object getElement(int index) {
		return elements.get(index);
	}
	
	public void fireChangedEvent(Object element) {
		int index = elements.indexOf(element);
		if (index == -1) {
			throw new IllegalArgumentException(element+" is not contained");
		}
		fireChangedEvent(element, index);
	}
}