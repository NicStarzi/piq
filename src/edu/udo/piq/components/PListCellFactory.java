package edu.udo.piq.components;

public interface PListCellFactory {
	
	public PListCellComponent getCellComponentFor(PListModel listModel, int index);
	
}