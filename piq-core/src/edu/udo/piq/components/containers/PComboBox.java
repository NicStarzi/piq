package edu.udo.piq.components.containers;

import edu.udo.piq.PInsets;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.collections.PList;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PListModel;
import edu.udo.piq.components.collections.PListSingleSelection;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PModelObs;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.components.textbased.PTextFieldObs;

public class PComboBox extends PDropDown {
	
	private final PSelectionObs listSelectObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			setDisplayedIndex(index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			if (index.equals(displayedIndex)) {
				setDisplayedIndex(null);
			}
		}
	};
	private final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PModel model, PModelIndex index,
				Object newContent) 
		{
			if (displayedIndex == null) {
				list.getSelection().addSelection(index);
			}
		}
		public void onContentRemoved(PModel model, PModelIndex index,
				Object oldContent) 
		{
			if (index.equals(displayedIndex)) {
				setDisplayedIndex(null);
				if (list.getModel().getSize() > 0) {
					list.getSelection().addSelection(new PListIndex(0));
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
	private final PTextField txtField;
	private final PList list;
	private StringEncoder strEnc;
	private PModelIndex displayedIndex = null;
	
	public PComboBox() {
		super();
		getLayoutInternal().setInsets(PInsets.ZERO_INSETS);
		getLayoutInternal().setGap(0);
		
		list = new PList();
		list.setDragAndDropSupport(null);
		list.setSelection(new PListSingleSelection());
		list.addObs(modelObs);
		list.addObs(listSelectObs);
		setBody(list);
		
		txtField = new PTextField();
		setPreview(txtField);
		
		txtField.addObs((PTextFieldObs) (cmp) -> onTextFieldInput());
		
		addObs(new PDropDownObs() {
			public void onBodyShown(PDropDown dropDown) {
				getBody().takeFocus();
			}
		});
	}
	
	public PTextField getTextField() {
		return txtField;
	}
	
	public PList getList() {
		return list;
	}
	
	public void setStringEncoder(StringEncoder stringEncoder) {
		strEnc = stringEncoder;
	}
	
	public StringEncoder getStringEncoder() {
		return strEnc;
	}
	
	public void setDisplayedIndex(int indexVal) {
		setDisplayedIndex(new PListIndex(indexVal));
	}
	
	public void setDisplayedIndex(PModelIndex index) {
		displayedIndex = index;
		if (displayedIndex == null) {
			txtField.getModel().setValue(null);
		} else {
			txtField.getModel().setValue(list.getModel().get(displayedIndex));
		}
	}
	
	public void defaultRender(PRenderer renderer) {
	}
	
	protected void onTextFieldInput() {
		StringEncoder strEnc = getStringEncoder();
		if (strEnc == null) {
			return;
		}
		String txt = txtField.getText().toLowerCase();
		
		PListModel mdl = getList().getModel();
		for (int i = 0; i < mdl.getSize(); i++) {
			Object obj = mdl.get(i);
			String objStr = strEnc.getStringFor(obj).toLowerCase();
			if (objStr.contains(txt)) {
				getList().getSelection().clearSelection();
				getList().getSelection().addSelection(new PListIndex(i));
				return;
			}
		}
		setDisplayedIndex(displayedIndex);
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
	}
	
	protected void onButtonClick() {
		if (isBodyVisible()) {
			hideDropDown();
		} else {
			showDropDown();
		}
	}
	
	public static interface StringEncoder {
		public String getStringFor(Object obj);
	}
	
}