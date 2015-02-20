package edu.udo.piq.components;

import edu.udo.piq.PComponent;

public interface PTreeCellComponent extends PComponent {
	
	public void setSelected(boolean isSelected);
	
	public boolean isSelected();
	
	public void setDropHighlightType(DropHighlightType highlightType);
	
	public DropHighlightType getDropHighlightType();
	
	public void setNode(PTreeModel model, Object parent, int index);
	
	public Object getNode();
	
	public static enum DropHighlightType {
		BEFORE,
		BEHIND,
		INSIDE,
		;
	}
	
}