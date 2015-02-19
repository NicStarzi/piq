package edu.udo.piq.components;

import edu.udo.piq.PComponent;

public interface PTreeCellComponent extends PComponent {
	
	public void setSelected(boolean isSelected);
	
	public boolean isSelected();
	
	public void setDropHighlighted(boolean isHighlighted);
	
	public boolean isDropHighlighted();
	
	public void setNode(PTreeModel model, Object parent, int index);
	
	public Object getNode();
	
}