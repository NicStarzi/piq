package edu.udo.piq.components.defaults;

import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.util.PiqUtil;

public class ReRenderPFocusObs implements PFocusObs {
	
	public void onFocusGained(PComponent oldOwner, PComponent newOwner) {
		PiqUtil.fireReRenderEventFor(newOwner);
	}
	
	public void onFocusLost(PComponent oldOwner) {
		PiqUtil.fireReRenderEventFor(oldOwner);
	}
	
}