package edu.udo.piq.tools;

import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPMouse implements PMouse {
	
	protected final ObserverList<PMouseObs> obsList
		= PCompUtil.createDefaultObserverList();
	
	@Override
	public void addObs(PMouseObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PMouseObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireMoveEvent() {
		obsList.fireEvent((obs) -> obs.onMouseMoved(this));
	}
	
	protected void fireTriggerEvent(MouseButton btn) {
		obsList.fireEvent((obs) -> obs.onButtonTriggered(this, btn));
	}
	
	protected void fireReleaseEvent(MouseButton btn, int clickCount) {
		obsList.fireEvent((obs) -> obs.onButtonReleased(this, btn, clickCount));
	}
	
	protected void firePressEvent(MouseButton btn, int clickCount) {
		obsList.fireEvent((obs) -> obs.onButtonPressed(this, btn, clickCount));
	}
	
}