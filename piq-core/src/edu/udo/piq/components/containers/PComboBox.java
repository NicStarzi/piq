package edu.udo.piq.components.containers;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentAction;
import edu.udo.piq.PInsets;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PRenderer;
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
import edu.udo.piq.components.util.DefaultPKeyInput;
import edu.udo.piq.components.util.ObjToStr;
import edu.udo.piq.components.util.PKeyInput;
import edu.udo.piq.components.util.PKeyInput.FocusPolicy;
import edu.udo.piq.components.util.PKeyInput.KeyInputType;
import edu.udo.piq.components.util.StrToObj;
import edu.udo.piq.util.AncestorIterator;
import edu.udo.piq.util.ThrowException;

public class PComboBox extends PDropDown {
	
	/**
	 * This method is used by the {@link #INPUT_PRESS_DOWN} to get the 
	 * {@link PComboBox} that belongs to the source of the key event.<br>
	 * @param comp	a non-null PComponent that is either a PComboBox or a descendant of a PComboBox
	 * @return		a non-null PComboBox
	 */
	protected static PComboBox getComboBox(PComponent comp) {
		for (PComponent anc : new AncestorIterator(comp, true)) {
			if (anc instanceof PComboBox) {
				return (PComboBox) anc;
			}
		}
		throw new IllegalArgumentException("Event source has no "
				+PComboBox.class.getSimpleName());
	}
	
	/**
	 * Used for {@link #INPUT_PRESS_DOWN}.
	 */
	public static final String INPUT_IDENTIFIER_PRESS_DOWN = "pressDown";
	/**
	 * If the DOWN key is pressed while all the following conditions are met:
	 *  - the combo box or a descendant of the combo box has focus
	 *  - the combo box is enabled
	 *  - the body of the combo box is not shown
	 * then the body will become visible.
	 * @see #INPUT_IDENTIFIER_PRESS_DOWN
	 * @see #REACTION_PRESS_DOWN
	 * @see #getComboBox(PComponent)
	 * @see #showDropDown()
	 */
	public static final PKeyInput INPUT_PRESS_DOWN = new DefaultPKeyInput(
			FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.TRIGGER, Key.DOWN, 
		(comp) -> {
			PComboBox comboBox = getComboBox(comp);
			return comboBox.isEnabled() && !comboBox.isBodyVisible();
		});
	/**
	 * Shows the drop down body of the combo box.
	 * @see #INPUT_PRESS_DOWN
	 * @see #getComboBox(PComponent)
	 * @see #showDropDown()
	 */
	public static final PComponentAction REACTION_PRESS_DOWN = (comp) -> {
		getComboBox(comp).showDropDown();
	};
	
	protected final PSelectionObs listSelectObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			PComboBox.this.onSelectionAdded(selection, index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			PComboBox.this.onSelectionRemoved(selection, index);
		}
	};
	protected final PModelObs modelObs = new PModelObs() {
		public void onContentAdded(PModel model, PModelIndex index, Object newContent) {
			PComboBox.this.onContentAdded(model, index, newContent);
		}
		public void onContentRemoved(PModel model, PModelIndex index, Object oldContent) {
			PComboBox.this.onContentRemoved(model, index, oldContent);
		}
		public void onContentChanged(PModel model, PModelIndex index, Object oldContent) {
			PComboBox.this.onContentChanged(model, index, oldContent);
		}
	};
	protected final PTextFieldObs txtFieldObs = (PTextFieldObs) (cmp) -> onTextFieldInput();
	protected PTextField txtField;
	protected PList list;
	protected ObjToStr encoder;
	protected StrToObj decoder;
	protected PModelIndex displayedIndex = null;
	protected PModelIndex prevDisplayedIndex = null;
	
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
		
		setTextField(new PTextField());
		
		addObs(new PDropDownObs() {
			public void onBodyShown(PDropDown dropDown) {
				getBody().takeFocus();
			}
		});
		addObs(new PKeyboardObs() {
			public void onKeyTriggered(PKeyboard keyboard, Key key) {
				PComboBox.this.onKeyTriggered(keyboard, key);
			}
		});
	}
	
	public void setPreview(PComponent component) {
		ThrowException.ifTypeCastFails(component, PTextField.class, 
				"!(component instanceof PTextField)");
		setTextField((PTextField) component);
	}
	
	public void setTextField(PTextField textField) {
		super.setPreview(textField);
		if (getTextField() != null) {
			getTextField().removeObs(txtFieldObs);
			getTextField().undefine(INPUT_IDENTIFIER_PRESS_DOWN);
		}
		txtField = textField;
		if (getTextField() != null) {
			getTextField().addObs(txtFieldObs);
			getTextField().defineInput(INPUT_IDENTIFIER_PRESS_DOWN, 
					INPUT_PRESS_DOWN, REACTION_PRESS_DOWN);
		}
	}
	
	public PTextField getTextField() {
		return txtField;
	}
	
	public PList getList() {
		return list;
	}
	
	public void setOutputEncoder(ObjToStr outputEncoder) {
		encoder = outputEncoder;
		// We have to remember the current index because it is lost after clearing the selection
		PModelIndex currentIndex = getDisplayedIndex();
		list.getSelection().clearSelection();
		// We re-select the displayed index to refresh the preview
		list.getSelection().addSelection(currentIndex);
	}
	
	public ObjToStr getOutputEncoder() {
		return encoder;
	}
	
	public void setInputDecoder(StrToObj stringDecoder) {
		decoder = stringDecoder;
	}
	
	public StrToObj getInputDecoder() {
		return decoder;
	}
	
	public void setDisplayedIndex(int indexVal) {
		setDisplayedIndex(new PListIndex(indexVal));
	}
	
	public void setDisplayedIndex(PModelIndex index) {
		if ((displayedIndex == null && index != null) || !displayedIndex.equals(index)) {
			displayedIndex = index;
			if (getTextField() == null) {
				return;
			}
			Object value;
			if (displayedIndex == null) {
				value = null;
			} else {
				value = getList().getModel().get(getDisplayedIndex());
			}
			if (getOutputEncoder() != null) {
				try {
					value = getOutputEncoder().parse(value);
				} catch (Exception e) {
					e.printStackTrace();
					value = null;
				}
			}
			getTextField().getModel().setValue(value);
		}
	}
	
	public PModelIndex getDisplayedIndex() {
		return displayedIndex;
	}
	
	public PModelIndex getPreviouslyDisplayedIndex() {
		return prevDisplayedIndex;
	}
	
	public void defaultRender(PRenderer renderer) {
	}
	
	protected void onSelectionAdded(PSelection selection, PModelIndex index) {
		setDisplayedIndex(index);
	}
	
	protected void onSelectionRemoved(PSelection selection, PModelIndex index) {
		if (index.equals(getDisplayedIndex())) {
			setDisplayedIndex(null);
		}
	}
	
	protected void onContentAdded(PModel model, PModelIndex index, Object newContent) {
		if (getDisplayedIndex() == null) {
			getList().getSelection().addSelection(index);
		}
	}
	
	protected void onContentRemoved(PModel model, PModelIndex index, Object oldContent) {
		if (index.equals(getDisplayedIndex())) {
			setDisplayedIndex(null);
			if (getList().getModel().getSize() > 0) {
				getList().getSelection().addSelection(new PListIndex(0));
			}
		}
	}
	
	protected void onContentChanged(PModel model, PModelIndex index, Object oldContent) {
		if (index.equals(getDisplayedIndex())) {
			setDisplayedIndex(index);
		}
	}
	
	protected void onTextFieldInput() {
		if (getTextField() == null) {
			return;
		}
		String txt = getTextField().getText();
		Object input;
		if (getInputDecoder() == null) {
			input = txt;
		} else {
			try {
				input = getInputDecoder().parse(txt);
			} catch (Exception e) {
				e.printStackTrace();
				input = null;
			}
		}
		
		PList list = getList();
		PListModel mdl = list.getModel();
		for (int i = 0; i < mdl.getSize(); i++) {
			Object obj = mdl.get(i);
			if (obj.equals(input)) {
				list.getSelection().clearSelection();
				list.getSelection().addSelection(new PListIndex(i));
				return;
			}
		}
		// We have to remember the current index because it is lost after clearing the selection
		PModelIndex currentIndex = getDisplayedIndex();
		list.getSelection().clearSelection();
		// We re-select the displayed index to refresh the preview
		list.getSelection().addSelection(currentIndex);
	}
	
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
	}
	
	public void onKeyTriggered(PKeyboard keyboard, Key key) {
		if (isBodyVisible()) {
			if (key == Key.ESC) {
				hideDropDownAndReset();
			} else if (key == Key.ENTER) {
				hideDropDown();
			}
		}
	}
	
	protected void onButtonClick() {
		if (isBodyVisible()) {
			hideDropDown();
		} else {
			showDropDown();
		}
	}
	
	protected void showDropDown() {
		prevDisplayedIndex = getDisplayedIndex();
		super.showDropDown();
	}
	
	protected void hideDropDown() {
		super.hideDropDown();
	}
	
	protected void hideDropDownAndReset() {
		hideDropDown();
		setDisplayedIndex(getPreviouslyDisplayedIndex());
	}
	
}