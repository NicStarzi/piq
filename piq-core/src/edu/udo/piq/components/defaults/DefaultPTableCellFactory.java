package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PTablePosition;
import edu.udo.piq.components.PTableCellComponent;
import edu.udo.piq.components.PTableCellFactory;
import edu.udo.piq.components.PTableModel;

public class DefaultPTableCellFactory implements PTableCellFactory {
	
	public PTableCellComponent getCellComponentFor(PTableModel tableModel, PTablePosition cell) {
		int col = cell.getColumnIndex();
		int row = cell.getRowIndex();
		Object element = tableModel.getCell(col, row);
		String text = element.toString();
		
		DefaultPTableCellComponent label = new DefaultPTableCellComponent();
		label.getModel().setValue(text);
		
		return label;
	}
	
}