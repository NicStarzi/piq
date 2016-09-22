package edu.udo.piq.components.collections;

import edu.udo.piq.PComponent;

public interface PCellComponent extends PComponent {
	
	public void setSelected(boolean value);
	
	public boolean isSelected();
	
	public void setDropHighlighted(boolean value);
	
	public boolean isDropHighlighted();
	
	public void setElement(PModel model, PModelIndex index);
	
	public Object getElement();
	
	public PModel getElementModel();
	
	public PModelIndex getElementIndex();
	
}