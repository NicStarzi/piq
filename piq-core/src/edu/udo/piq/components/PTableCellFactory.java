package edu.udo.piq.components;

public interface PTableCellFactory {
	
	public PTableCellComponent getCellComponentFor(PTableModel listModel, PTablePosition cell);
	
}