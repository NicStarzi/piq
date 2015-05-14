package edu.udo.piq.comps.selectcomps;

import edu.udo.piq.PComponent;

public interface PCellComponent extends PComponent {
	
	public void setSelected(boolean isSelected);
	
	public boolean isSelected();
	
	public void setDropHighlighted(boolean isHighlighted);
	
	public boolean isDropHighlighted();
	
	public void setElement(PModel model, PModelIndex index);
	
}