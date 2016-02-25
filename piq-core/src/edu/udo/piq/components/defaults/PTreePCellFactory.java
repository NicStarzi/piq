package edu.udo.piq.components.defaults;

import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PCellFactory;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;

public class PTreePCellFactory implements PCellFactory {
	
	protected PCellFactory innerCellFact = new DefaultPCellFactory();
	
	public PCellFactory getInnerCellFactory() {
		return innerCellFact;
	}
	
	public PCellComponent makeCellComponent(PModel model, PModelIndex index) {
		PTreePCellComponent cell = new PTreePCellComponent();
		cell.setSecondComponent(getInnerCellFactory().makeCellComponent(model, index));
		cell.setElement(model, index);
		return cell;
	}
	
}