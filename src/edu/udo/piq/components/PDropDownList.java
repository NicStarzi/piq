package edu.udo.piq.components;

import edu.udo.piq.components.PListSelection.SelectionMode;
import edu.udo.piq.components.defaults.DefaultPTextModel;

public class PDropDownList extends PDropDown {
	
	private final PListSelectionObs listSelectObs = new PListSelectionObs() {
		public void selectionRemoved(PListSelection selection, Object element) {
			if (label.getModel().getValue() == element) {
				if (list.getModel().getElementCount() > 0) {
					label.getModel().setValue(list.getModel().getElement(0));
				} else {
					label.getModel().setValue(null);
				}
			}
		}
		public void selectionAdded(PListSelection selection, Object element) {
			label.getModel().setValue(element);
		}
	};
	private final PLabel label;
	private final PList list;
	
	public PDropDownList() {
		super();
		list = new PList();
		list.getSelection().setSelectionMode(SelectionMode.SINGLE_ROW);
		list.getSelection().addObs(listSelectObs);
		setBody(list);
		
		label = new PLabel(new DefaultPTextModel());
		setPreview(label);
	}
	
	public PList getList() {
		return list;
	}
	
}