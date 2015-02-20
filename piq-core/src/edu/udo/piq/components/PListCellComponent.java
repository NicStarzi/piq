package edu.udo.piq.components;

import edu.udo.piq.PComponent;

public interface PListCellComponent extends PComponent {
	
	public void setSelected(boolean isSelected);
	
	public boolean isSelected();
	
	public void setDropHighlighted(boolean isHighlighted);
	
	public boolean isDropHighlighted();
	
	public void setElement(PListModel model, int index);
	
	public Object getElement();
	
}