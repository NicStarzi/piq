package edu.udo.piq.comps.selectcomps;

public interface PCellFactory {
	
	public PCellComponent makeCellComponent(PModel model, PModelIndex index);
	
}