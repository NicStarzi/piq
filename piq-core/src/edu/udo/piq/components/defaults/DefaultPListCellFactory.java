package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PListCellComponent;
import edu.udo.piq.components.PListCellFactory;
import edu.udo.piq.components.PListModel;

public class DefaultPListCellFactory implements PListCellFactory {
	
	public PListCellComponent getCellComponentFor(PListModel model, int index) {
		DefaultPListCellComponent label = new DefaultPListCellComponent();
		label.setElement(model, index);
		return label;
	}
	
}