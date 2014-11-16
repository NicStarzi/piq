package edu.udo.piq.layouts;

import edu.udo.piq.PComponent;

public class PWrapLayout extends PListLayout {
	
	public PWrapLayout(PComponent owner, Orientation orientation, int gap) {
		super(owner, orientation, gap);
	}
	
	public PWrapLayout(PComponent owner, Orientation orientation) {
		super(owner, orientation, DEFAULT_GAP);
	}
	
	public PWrapLayout(PComponent owner, int gap) {
		super(owner, DEFAULT_ORIENTATION, gap);
	}
	
	public PWrapLayout(PComponent owner) {
		super(owner, DEFAULT_ORIENTATION, DEFAULT_GAP);
	}
	
	public void layOut() {
	}
	
}