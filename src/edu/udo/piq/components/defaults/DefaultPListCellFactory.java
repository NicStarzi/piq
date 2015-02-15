package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PListCellComponent;
import edu.udo.piq.components.PListCellFactory;

public class DefaultPListCellFactory implements PListCellFactory {
	
	public PListCellComponent getCellComponentFor(Object element) {
		DefaultPListCellComponent label = new DefaultPListCellComponent();
		label.setElement(element);
		return label;
	}
	
}