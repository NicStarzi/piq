package edu.udo.piq.components;

import edu.udo.piq.components.defaults.DefaultPTextModel;
import edu.udo.piq.comps.selectcomps.PList;
import edu.udo.piq.comps.selectcomps.PListIndex;
import edu.udo.piq.comps.selectcomps.PListSingleSelection;
import edu.udo.piq.comps.selectcomps.PModel;
import edu.udo.piq.comps.selectcomps.PModelIndex;
import edu.udo.piq.comps.selectcomps.PModelObs;
import edu.udo.piq.comps.selectcomps.PSelection;
import edu.udo.piq.comps.selectcomps.PSelectionObs;

public class PDropDownList extends PDropDown {
	
	private final PSelectionObs listSelectObs = new PSelectionObs() {
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			if (index.equals(displayedIndex)) {
				if (list.getModel().getSize() > 0) {
					setDisplayedIndex(0);
				} else {
					setDisplayedIndex(null);
				}
			}
		}
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			setDisplayedIndex(index);
		}
		public void onLastSelectedChanged(PSelection selection,
				PModelIndex prevLastSelected, PModelIndex newLastSelected) 
		{
		}
	};
	private final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PModel model, PModelIndex index,
				Object newContent) 
		{
			if (displayedIndex == null) {
				setDisplayedIndex(index);
			}
		}
		public void onContentRemoved(PModel model, PModelIndex index,
				Object oldContent) 
		{
			if (index.equals(displayedIndex)) {
				if (list.getModel().getSize() > 0) {
					setDisplayedIndex(0);
				} else {
					setDisplayedIndex(null);
				}
			}
		}
		public void onContentChanged(PModel model, PModelIndex index,
				Object oldContent) 
		{
			if (index.equals(displayedIndex)) {
				setDisplayedIndex(index);
			}
		}
	};
	private final PLabel label;
	private final PList list;
	private PModelIndex displayedIndex = null;
	
	public PDropDownList() {
		super();
		list = new PList();
		list.setDragAndDropSupport(null);
		list.setSelection(new PListSingleSelection());
		list.addObs(modelObs);
		list.addObs(listSelectObs);
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
	
	public void setDisplayedIndex(int indexVal) {
		setDisplayedIndex(new PListIndex(indexVal));
	}
	
	public void setDisplayedIndex(PModelIndex index) {
		displayedIndex = index;
		if (displayedIndex == null) {
			label.getModel().setValue(null);
		} else {
			label.getModel().setValue(list.getModel().get(index));
		}
	}
	
//	public PSelection getSelection() {
//		return list.getSelection();
//	}
//	
//	public PModel getModel() {
//		return list.getModel();
//	}
//	
//	public PModelIndex getIndexAt(int x, int y) {
//		return displayedIndex;
//	}
//	
//	public List<Object> getAllSelectedContent() {
//		return list.getAllSelectedContent();
//	}
//	
//	public void addObs(PModelObs obs) {
//	}
//	
//	public void removeObs(PModelObs obs) {
//	}
//	
//	public void addObs(PSelectionObs obs) {
//	}
//	
//	public void removeObs(PSelectionObs obs) {
//	}
//	
//	public PModelIndex getDropIndex(int x, int y) {
//		return displayedIndex;
//	}
//	
//	public void setDropHighlight(PModelIndex index) {
//		list.setDropHighlight(index);
//	}
	
}