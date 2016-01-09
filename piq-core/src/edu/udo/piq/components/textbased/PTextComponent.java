package edu.udo.piq.components.textbased;

import edu.udo.piq.PComponent;

public interface PTextComponent extends PComponent {
	
	public PTextModel getModel();
	
	public PTextSelection getSelection();
	
	public boolean isEditable();
	
}