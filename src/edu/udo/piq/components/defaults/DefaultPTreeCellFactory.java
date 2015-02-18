package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PTreeCellComponent;
import edu.udo.piq.components.PTreeCellFactory;
import edu.udo.piq.components.PTreeModel;

public class DefaultPTreeCellFactory implements PTreeCellFactory {
	
	public PTreeCellComponent getCellComponentFor(PTreeModel model, Object node) {
		if (model == null) {
			throw new NullPointerException("model="+model);
		} if (node == null) {
			throw new NullPointerException("node="+node);
		}
		DefaultPTreeCellComponent label = new DefaultPTreeCellComponent();
		label.setNode(model, node);
		return label;
	}
	
}