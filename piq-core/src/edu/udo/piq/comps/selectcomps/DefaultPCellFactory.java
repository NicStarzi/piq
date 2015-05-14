package edu.udo.piq.comps.selectcomps;

public class DefaultPCellFactory implements PCellFactory {
	
	public PCellComponent makeCellComponent(PModel model, PModelIndex index) {
		DefaultPCellComponent cell = new DefaultPCellComponent();
		cell.setElement(model, index);
		return cell;
	}
	
}