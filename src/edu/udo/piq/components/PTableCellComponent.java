package edu.udo.piq.components;

import edu.udo.piq.PComponent;

public interface PTableCellComponent extends PComponent {
	
	public void setSelected(boolean isSelected);
	
	public boolean isSelected();
	
	public void cellChanged(PTableModel model, PTableCell cell);
	
}