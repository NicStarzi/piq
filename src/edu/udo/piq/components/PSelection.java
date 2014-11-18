package edu.udo.piq.components;

import java.util.Set;

public interface PSelection<E> {
	
	public void addSelection(E cell);
	
	public void removeSelection(E cell);
	
	public void clearSelection();
	
	public Set<E> getSelection();
	
	public boolean isSelected(E cell);
	
}