package edu.udo.piq.components;

public class PListPosition {
	
	private final PListModel model;
	private final int index;
	
	public PListPosition(PListModel model, int index) {
		if (model == null) {
			throw new NullPointerException("model="+model);
		} if (index < 0) {
			throw new IllegalArgumentException("index="+index);
		}
		this.model = model;
		this.index = index;
	}
	
	public PListPosition(PListModel model, Object element) {
		if (model == null) {
			throw new NullPointerException("model="+model);
		} if (element == null) {
			throw new NullPointerException("element="+element);
		}
		this.model = model;
		this.index = model.getElementIndex(element);
	}
	
	public PListModel getModel() {
		return model;
	}
	
	public int getIndex() {
		return index;
	}
	
	public Object getElement() {
		return getModel().getElement(getIndex());
	}
	
	public boolean canBeAdded(Object element) {
		return getModel().canAddElement(getIndex(), element);
	}
	
	public void add(Object element) {
		getModel().addElement(getIndex(), element);
	}
	
	public boolean canBeRemoved(Object element) {
		return getModel().canRemoveElement(getIndex());
	}
	
	public void remove() {
		getModel().removeElement(getIndex());
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + model.hashCode();
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} if (obj == null || !(obj instanceof PListPosition)) {
			return false;
		}
		PListPosition other = (PListPosition) obj;
		return index == other.index && model == other.model;
	}
	
}