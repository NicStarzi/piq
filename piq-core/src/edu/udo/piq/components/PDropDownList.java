package edu.udo.piq.components;

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
	private final PListModelObs modelObs = new PListModelObs() {
		public void elementAdded(PListModel model, Object element, int index) {
			if (label.getModel().getValue() == null) {
				label.getModel().setValue(element);
			}
		}
		public void elementRemoved(PListModel model, Object element, int index) {
			if (label.getModel().getValue() == element) {
				if (list.getModel().getElementCount() > 0) {
					label.getModel().setValue(list.getModel().getElement(0));
				} else {
					label.getModel().setValue(null);
				}
			}
		}
		public void elementChanged(PListModel model, Object element, int index) {
			if (label.getModel().getValue() == element) {
				label.getModel().setValue(element);
			}
		}
	};
	private final PLabel label;
	private final PList list;
	
	public PDropDownList() {
		super();
		list = new PList();
		list.getModel().addObs(modelObs);
//		list.getSelection().setSelectionMode(SelectionMode.SINGLE_ROW);
		list.getSelection().addObs(listSelectObs);
		setBody(list);
		
		label = new PLabel(new DefaultPTextModel());
		setPreview(label);
		
		addObs(new PDropDownObs() {
			public void bodyShown(PDropDown dropDown) {
				getBody().takeFocus();
			}
			public void bodyHidden(PDropDown dropDown) {
			}
		});
	}
	
	public PList getList() {
		return list;
	}
	
}