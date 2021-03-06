package edu.udo.piq.components.containers;

import java.util.function.Function;

import edu.udo.piq.PComponent;
import edu.udo.piq.PInsets;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PRenderer;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.actions.FocusOwnerAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.StandardComponentActionKey;
import edu.udo.piq.components.PSingleValueModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PModelObs;
import edu.udo.piq.components.collections.PReadOnlyModel;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.components.collections.list.PList;
import edu.udo.piq.components.collections.list.PListIndex;
import edu.udo.piq.components.collections.list.PListModel;
import edu.udo.piq.components.collections.list.PListSingleSelection;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.components.textbased.PTextFieldObs;
import edu.udo.piq.util.ThrowException;

public class PComboBox extends PDropDown {
	
	public static final PActionKey KEY_OPEN = StandardComponentActionKey.INTERACT;
	public static final PAccelerator ACCELERATOR_OPEN = new PAccelerator(
			ActualKey.DOWN, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.TRIGGER);
	public static final PComponentAction ACTION_OPEN = new FocusOwnerAction<>(
			PComboBox.class, false,
			ACCELERATOR_OPEN,
			self -> self.isEnabled() && !self.isBodyVisible(),
			self -> self.showDropDown());
	
	protected final PSelectionObs listSelectObs = new PSelectionObs() {
		@Override
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			PComboBox.this.onSelectionAdded(selection, index);
		}
		@Override
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			PComboBox.this.onSelectionRemoved(selection, index);
		}
	};
	protected final PModelObs modelObs = new PModelObs() {
		@Override
		public void onContentAdded(PReadOnlyModel model, PModelIndex index, Object newContent) {
			PComboBox.this.onContentAdded(model, index, newContent);
		}
		@Override
		public void onContentRemoved(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			PComboBox.this.onContentRemoved(model, index, oldContent);
		}
		@Override
		public void onContentChanged(PReadOnlyModel model, PModelIndex index, Object oldContent) {
			PComboBox.this.onContentChanged(model, index, oldContent);
		}
	};
	protected final PTextFieldObs txtFieldObs = (PTextFieldObs) (cmp) -> onTextFieldInput();
	protected PTextField txtField;
	protected PList list;
	protected Function<Object, String> encoder;
	protected Function<String, Object> decoder;
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
			@Override
			public void onBodyShown(PDropDown dropDown) {
				getBody().takeFocus();
			}
		});
		addObs(new PKeyboardObs() {
			@Override
			public void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
				PComboBox.this.onKeyTriggered(keyboard, key);
			}
		});
		
		addActionMapping(KEY_OPEN, ACTION_OPEN);
	}
	
	@Override
	public void setPreview(PComponent component) {
		ThrowException.ifTypeCastFails(component, PTextField.class,
				"!(component instanceof PTextField)");
		setTextField((PTextField) component);
	}
	
	public void setTextField(PTextField textField) {
		super.setPreview(textField);
		if (getTextField() != null) {
			getTextField().removeObs(txtFieldObs);
//			getTextField().undefine(INPUT_IDENTIFIER_PRESS_DOWN);
		}
		txtField = textField;
		if (getTextField() != null) {
			getTextField().addObs(txtFieldObs);
//			getTextField().defineInput(INPUT_IDENTIFIER_PRESS_DOWN,
//					INPUT_PRESS_DOWN, REACTION_PRESS_DOWN);
		}
	}
	
	public PTextField getTextField() {
		return txtField;
	}
	
	public PList getList() {
		return list;
	}
	
	public void setOutputEncoder(Function<Object, String> outputEncoder) {
		encoder = outputEncoder;
		// We have to remember the current index because it is lost after clearing the selection
		PModelIndex currentIndex = getDisplayedIndex();
		list.getSelection().clearSelection();
		// We re-select the displayed index to refresh the preview
		list.getSelection().addSelection(currentIndex);
	}
	
	public Function<Object, String> getOutputEncoder() {
		return encoder;
	}
	
	public void setInputDecoder(Function<String, Object> stringDecoder) {
		decoder = stringDecoder;
	}
	
	public Function<String, Object> getInputDecoder() {
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
					value = getOutputEncoder().apply(value);
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
	
	@Override
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
	
	protected void onContentAdded(PReadOnlyModel model, PModelIndex index, Object newContent) {
		if (getDisplayedIndex() == null) {
			getList().getSelection().addSelection(index);
		}
	}
	
	protected void onContentRemoved(PReadOnlyModel model, PModelIndex index, Object oldContent) {
		if (index.equals(getDisplayedIndex())) {
			setDisplayedIndex(null);
			if (getList().getModel().getSize() > 0) {
				getList().getSelection().addSelection(new PListIndex(0));
			}
		}
	}
	
	protected void onContentChanged(PReadOnlyModel model, PModelIndex index, Object oldContent) {
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
				input = getInputDecoder().apply(txt);
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
	
	@Override
	protected void onMouseButtonTriggered(PMouse mouse, MouseButton btn) {
	}
	
	@Override
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
	}
	
	public void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
		if (isBodyVisible()) {
			if (key == ActualKey.ESCAPE) {
				hideDropDownAndReset();
			} else if (key == ActualKey.ENTER) {
				hideDropDown();
			}
		}
	}
	
	@Override
	@TemplateMethod
	protected void onModelChange(PSingleValueModel<Boolean> model, Boolean oldValue, Boolean newValue) {
		// does nothing
	}
	
	@Override
	protected void onButtonClick() {
		if (isBodyVisible()) {
			hideDropDown();
		} else {
			showDropDown();
		}
	}
	
	@Override
	protected void showDropDown() {
		prevDisplayedIndex = getDisplayedIndex();
		super.showDropDown();
	}
	
	protected void hideDropDownAndReset() {
		hideDropDown();
		setDisplayedIndex(getPreviouslyDisplayedIndex());
	}
	
}