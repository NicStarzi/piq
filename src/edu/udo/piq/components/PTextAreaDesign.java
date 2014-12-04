package edu.udo.piq.components;

import edu.udo.piq.PBounds;
import edu.udo.piq.PDesign;

public interface PTextAreaDesign extends PDesign {
	
	public int getTextIndexAt(PTextArea txtArea, int x, int y);
	
	public PBounds getBoundsForText(PTextArea txtArea, int index);
	
}