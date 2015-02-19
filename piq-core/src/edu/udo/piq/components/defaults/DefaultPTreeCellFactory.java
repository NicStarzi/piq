package edu.udo.piq.components.defaults;

import edu.udo.piq.components.PTreeCellComponent;
import edu.udo.piq.components.PTreeCellFactory;
import edu.udo.piq.components.PTreeModel;

public class DefaultPTreeCellFactory implements PTreeCellFactory {
	
	public PTreeCellComponent getCellComponentFor(PTreeModel model, Object parent, int index) {
		if (model == null) {
			throw new NullPointerException("model="+model);
		} if (parent == null) {
			throw new NullPointerException("parent="+parent);
		} if (index < 0 || index >= model.getChildCount(parent)) {
			throw new IllegalArgumentException("index="+index);
		}
		DefaultPTreeCellComponent label = new DefaultPTreeCellComponent();
		label.setNode(model, parent, index);
		return label;
	}
	
}