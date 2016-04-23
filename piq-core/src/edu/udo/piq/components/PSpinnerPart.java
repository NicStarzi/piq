package edu.udo.piq.components;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.util.ObjToStr;

public interface PSpinnerPart extends PComponent {
	
	public void setSpinnerModel(PSpinnerModel model);
	
	public void setOutputEncoder(ObjToStr outputEncoder);
	
	public default PSpinner getSpinner() {
		if (getParent() instanceof PSpinner) {
			return (PSpinner) getParent();
		}
		return null;
	}
	
}