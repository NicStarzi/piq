package edu.udo.piq.components.popup;

import edu.udo.piq.PMouse;

public class PPopupSubMenu extends PPopupButton implements PPopupComponent {
	
	public PPopupSubMenu(Object labelModelValue) {
		super(labelModelValue);
	}
	
	public PPopupSubMenu(PPopupComponent content) {
		super(content);
	}
	
	public PPopupSubMenu() {
		super();
	}
	
	protected void onMouseMoved(PMouse mouse) {
		super.onMouseMoved(mouse);
		if (isHighlighted()) {
			
		}
	}
	
}