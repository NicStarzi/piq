package edu.udo.piq.components.containers;

import edu.udo.piq.PComponent;

public interface PTabComponent extends PComponent {
	
	public void setSelected(boolean selected);
	
	public boolean isSelected();
	
	public void setPreview(PComponent component);
	
	public PComponent getPreview();
	
	public void setIndex(int index);
	
	public int getIndex();
	
}