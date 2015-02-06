package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PListCellComponent;
import edu.udo.piq.components.PListCellFactory;
import edu.udo.piq.components.PListModel;

public class DefaultPListCellFactory implements PListCellFactory {
	
	public PListCellComponent getCellComponentFor(PListModel listModel, int index) {
		Object text = listModel.getElement(index).toString();
		
		DefaultPListCellComponent label = new DefaultPListCellComponent();
		label.getModel().setValue(text);
		
		return label;
	}
	
}