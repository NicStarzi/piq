package edu.udo.piq.components.textbased;

public interface PTextModel {
	
	public void setValue(Object value);
	
	public Object getValue();
	
	public String getText();
	
	public default int getLength() {
		return getText().length();
	}
	
	public void addObs(PTextModelObs obs);
	
	public void removeObs(PTextModelObs obs);
	
}