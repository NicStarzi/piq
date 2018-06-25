package edu.udo.piq.components;

import java.util.function.Function;

import edu.udo.piq.components.collections.list.PListIndex;
import edu.udo.piq.components.textbased.PTextField;
import edu.udo.piq.components.textbased.PTextFieldObs;
import edu.udo.piq.components.textbased.PTextSelection;

public class PSpinnerEditor extends PTextField implements PSpinnerPart {
	
	protected final PSpinnerModelObs spnrModelObs =
			(model, oldVal) -> synchronizeModelValue();
	protected PSpinnerModel spnrModel;
	protected Function<Object, String> encoder;
	protected Function<String, Object> decoder;
	
	public PSpinnerEditor() {
		super("");
		
		addObs((PTextFieldObs) (self) -> onUserInput());
	}
	
	@Override
	public void setOutputEncoder(Function<Object, String> outputEncoder) {
		encoder = outputEncoder;
		synchronizeModelValue();
	}
	
	public Function<Object, String> getOutputEncoder() {
		return encoder;
	}
	
	@Override
	public void setInputDecoder(Function<String, Object> inputDecoder) {
		decoder = inputDecoder;
		onUserInput();
	}
	
	public Function<String, Object> getInputDecoder() {
		return decoder;
	}
	
	@Override
	public void setSpinnerModel(PSpinnerModel model) {
		if (getSpinnerModel() != null) {
			getSpinnerModel().removeObs(spnrModelObs);
		}
		spnrModel = model;
		if (getSpinnerModel() == null) {
			getModel().setValue("");
			setEditable(false);
		} else {
			synchronizeModelValue();
			getSpinnerModel().addObs(spnrModelObs);
		}
	}
	
	public PSpinnerModel getSpinnerModel() {
		return spnrModel;
	}
	
	@Override
	public PSpinner getSpinner() {
		if (getParent() instanceof PSpinner) {
			return (PSpinner) getParent();
		}
		return null;
	}
	
	@Override
	public boolean isEnabled() {
		PSpinner parent = getSpinner();
		if (parent != null) {
			return parent.isEnabled();
		}
		return super.isEnabled();
	}
	
	protected void onUserInput() {
		String text = getText();
		Object value = text;
		if (getInputDecoder() != null) {
			value = getInputDecoder().apply(text);
		}
		PSpinnerModel model = getSpinnerModel();
		if (model != null && model.canSetValue(value)) {
			model.setValue(value);
		}
		synchronizeModelValue();
	}
	
	protected void synchronizeModelValue() {
		if (getModel() != null) {
			Object output;
			if (getOutputEncoder() == null) {
				output = getSpinnerModel().getValue();
			} else {
				try {
					output = getOutputEncoder().apply(getSpinnerModel().getValue());
				} catch (Exception e) {
					e.printStackTrace();
					output = null;
				}
			}
			getModel().setValue(output);
		}
		PTextSelection sel = getSelection();
		if (sel != null && !sel.hasSelection()) {
			sel.addSelection(new PListIndex(0));
		}
	}
}