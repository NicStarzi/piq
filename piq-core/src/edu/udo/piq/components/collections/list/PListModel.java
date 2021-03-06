package edu.udo.piq.components.collections.list;

import edu.udo.piq.components.collections.PModel;

public interface PListModel extends PModel {
	
	public int getSize();
	
	public Object get(int indexVal);
	
	public boolean contains(int index);
	
	public boolean canSet(int index, Object content);
	
	public void set(int index, Object content);
	
	public boolean canAdd(int index, Object content);
	
	public void add(int index, Object content);
	
	public default boolean canAdd(Object content) {
		return canAdd(getSize(), content);
	}
	
	public default void add(Object content) {
		add(getSize(), content);
	}
	
	public boolean canRemove(int index);
	
	public void remove(int index);
	
}