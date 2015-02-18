package edu.udo.piq.components;

import java.util.List;

public interface PSelection<E> {
	
	public void addSelection(E cell);
	
	public void removeSelection(E cell);
	
	public void clearSelection();
	
	public List<E> getSelection();
	
	public boolean isSelected(E cell);
	
}