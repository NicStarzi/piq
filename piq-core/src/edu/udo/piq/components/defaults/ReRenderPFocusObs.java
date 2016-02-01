package edu.udo.piq.components.defaults;

import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.util.PCompUtil;

public class ReRenderPFocusObs implements PFocusObs {
	
	public void onFocusGained(PComponent oldOwner, PComponent newOwner) {
		PCompUtil.fireReRenderEventFor(newOwner);
	}
	
	public void onFocusLost(PComponent oldOwner) {
		PCompUtil.fireReRenderEventFor(oldOwner);
	}
	
}