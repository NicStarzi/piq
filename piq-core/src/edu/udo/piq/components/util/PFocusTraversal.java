package edu.udo.piq.components.util;

import edu.udo.piq.PComponent;

public interface PFocusTraversal {
	
	public PComponent getNext(PComponent currentFocusOwner);
	
	public PComponent getPrevious(PComponent currentFocusOwner);
	
	public PComponent getInitial();
	
	public PComponent getDefault();
	
}