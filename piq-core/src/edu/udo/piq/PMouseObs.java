package edu.udo.piq;

import edu.udo.piq.PMouse.MouseButton;

public interface PMouseObs {
	
	public default void mouseMoved(PMouse mouse) {}
	
	public default void buttonPressed(PMouse mouse, MouseButton btn) {}
	
	public default void buttonTriggered(PMouse mouse, MouseButton btn) {}
	
	public default void buttonReleased(PMouse mouse, MouseButton btn) {}
	
}