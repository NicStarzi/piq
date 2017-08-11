package edu.udo.piq.tools;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPKeyboard implements PKeyboard {
	
	protected final ObserverList<PKeyboardObs> obsList
		= PCompUtil.createDefaultObserverList();
	
	@Override
	public void addObs(PKeyboardObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PKeyboardObs obs) {
		obsList.remove(obs);
	}
	
	protected void firePressEvent(Key key) {
		obsList.fireEvent(obs -> obs.onKeyPressed(this, key));
	}
	
	protected void fireTriggerEvent(Key key) {
		obsList.fireEvent(obs -> obs.onKeyTriggered(this, key));
	}
	
	protected void fireReleaseEvent(Key key) {
		obsList.fireEvent(obs -> obs.onKeyReleased(this, key));
	}
	
	protected void fireModifierToggledEvent(Modifier mod) {
		obsList.fireEvent(obs -> obs.onModifierToggled(this, mod));
	}
	
	protected void fireStringTypedEvent(String string) {
		obsList.fireEvent(obs -> obs.onStringTyped(this, string));
	}
	
}