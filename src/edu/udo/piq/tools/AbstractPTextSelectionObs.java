package edu.udo.piq.tools;

import edu.udo.piq.components.PTextSelection;
import edu.udo.piq.components.PTextSelectionObs;

public abstract class AbstractPTextSelectionObs implements PTextSelectionObs {
	
	public void selectionAdded(PTextSelection selection, int index) {
	}
	
	public void selectionRemoved(PTextSelection selection, int index) {
	}
	
	public void selectionChanged(PTextSelection selection) {
	}
	
}