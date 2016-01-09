package edu.udo.piq.components.util;

public interface PDictionary {
	
	public String translate(Object value);
	
	public void addObs(PDictionaryObs obs);
	
	public void removeObs(PDictionaryObs obs);
	
}