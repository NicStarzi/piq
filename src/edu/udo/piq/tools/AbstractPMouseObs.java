package edu.udo.piq.tools;

import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;

public abstract class AbstractPMouseObs implements PMouseObs {
	public void mouseMoved(PMouse mouse) {
	}
	public void buttonPressed(PMouse mouse, MouseButton btn) {
	}
	public void buttonTriggered(PMouse mouse, MouseButton btn) {
	}
	public void buttonReleased(PMouse mouse, MouseButton btn) {
	}
}