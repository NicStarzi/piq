package edu.udo.piq.components.textbased;

import edu.udo.piq.PBounds;
import edu.udo.piq.PDesign;

public interface PTextAreaDesign extends PDesign {
	
	public PBounds getBoundsForLetter(PTextArea txtArea, int index);
	
}