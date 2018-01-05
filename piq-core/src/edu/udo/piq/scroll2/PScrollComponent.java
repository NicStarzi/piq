package edu.udo.piq.scroll2;

import edu.udo.piq.PComponent;

public interface PScrollComponent extends PComponent {
	
	public void onScrollRequest(int x, int y, int fx, int fy);
	
}