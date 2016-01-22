package edu.udo.piq.components.textbased;

import edu.udo.piq.PComponent;
import edu.udo.piq.components.collections.PListIndex;

public interface PTextComponent extends PComponent {
	
	public static final int INDEX_NO_SELECTION = -1;
	
	public PTextModel getModel();
	
	public PTextSelection getSelection();
	
	public PTextIndexTable getIndexTable();
	
	public boolean isEditable();
	
	public PListIndex getTextIndexAt(int x, int y);
	
}