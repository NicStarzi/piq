package edu.udo.piq.components.collections;

public interface PCellFactory {
	
	public PCellComponent makeCellComponent(PModel model, PModelIndex index);
	
}