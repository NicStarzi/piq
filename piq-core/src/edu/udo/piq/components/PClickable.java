package edu.udo.piq.components;

import edu.udo.piq.PComponent;

public interface PClickable extends PComponent {
	
	public void addObs(PClickObs obs);
	
	public void removeObs(PClickObs obs);
}