package edu.udo.piq.components;

public interface PListCellFactory {
	
	public PListCellComponent getCellComponentFor(PListModel model, int index);
	
}