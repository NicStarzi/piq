package edu.udo.piq.components.containers;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PListSingleSelection;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PModelObs;
import edu.udo.piq.components.collections.PReadOnlyModel;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.textbased.PLabel;

public class PDropDownList extends PDropDown {
	
	protected final PSelectionObs listSelectObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			setDisplayedIndex(index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			if (index.equals(displayedIndex)) {
				setDisplayedIndex(null);
			}
		}
	};
	protected final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PReadOnlyModel model, PModelIndex index,
				Object newContent) 
		{
			if (displayedIndex == null) {
				list.getSelection().addSelection(index);
			}
		}
		public void onContentRemoved(PReadOnlyModel model, PModelIndex index,
				Object oldContent) 
		{
			if (index.equals(displayedIndex)) {
				setDisplayedIndex(null);
				if (list.getModel().getSize() > 0) {
					list.getSelection().addSelection(new PListIndex(0));
				}
			}
		}
		public void onContentChanged(PReadOnlyModel model, PModelIndex index,
				Object oldContent) 
		{
			if (index.equals(displayedIndex)) {
				setDisplayedIndex(index);
			}
		}
	};
	protected final PKeyboardObs keyObs = new PKeyboardObs() {
		public void onKeyTriggered(PKeyboard keyboard, Key key) {
			if (isBodyVisible() && (key == Key.ENTER || key == Key.ESC)) {
				hideDropDown();
			}
		}
	};
	protected final PLabel label;
	protected final PList list;
	protected PModelIndex displayedIndex = null;
	
	public PDropDownList() {
		super();
		list = new PList();
		list.setDragAndDropSupport(null);
		list.setSelection(new PListSingleSelection());
		list.addObs(modelObs);
		list.addObs(listSelectObs);
		setBody(list);
		
		label = new PLabel();
		setPreview(label);
		
		addObs(new PDropDownObs() {
			public void onBodyShown(PDropDown dropDown) {
				getBody().takeFocus();
			}
		});
		addObs(keyObs);
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
			label.getModel().setValue(list.getModel().get(displayedIndex));
		}
	}
	
}