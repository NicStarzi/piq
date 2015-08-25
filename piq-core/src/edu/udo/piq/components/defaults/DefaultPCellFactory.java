package edu.udo.piq.components.defaults;

import edu.udo.piq.components.collections.PCellComponent;
import edu.udo.piq.components.collections.PCellFactory;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;

public class DefaultPCellFactory implements PCellFactory {
	
	public PCellComponent makeCellComponent(PModel model, PModelIndex index) {
		DefaultPCellComponent cell = new DefaultPCellComponent();
		cell.setElement(model, index);
		return cell;
	}
	
}