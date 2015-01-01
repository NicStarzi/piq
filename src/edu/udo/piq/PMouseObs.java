package edu.udo.piq;

import edu.udo.piq.PMouse.MouseButton;

public interface PMouseObs {
	
	public void mouseMoved(PMouse mouse);
	
	public void buttonPressed(PMouse mouse, MouseButton btn);
	
	public void buttonTriggered(PMouse mouse, MouseButton btn);
	
	public void buttonReleased(PMouse mouse, MouseButton btn);
	
}