package edu.udo.piq.components;

import java.util.function.Function;

import edu.udo.piq.PComponent;

public interface PSpinnerPart extends PComponent {
	
	public void setSpinnerModel(PSpinnerModel model);
	
	public void setOutputEncoder(Function<Object, String> outputEncoder);
	
	public void setInputDecoder(Function<String, Object> inputDecoder);
	
	public default PSpinner getSpinner() {
		if (getParent() instanceof PSpinner) {
			return (PSpinner) getParent();
		}
		return null;
	}
	
}