package edu.udo.piq.components;

public interface PTreeCellFactory {
	
	public PTreeCellComponent getCellComponentFor(PTreeModel model, Object parent, int index);
	
}