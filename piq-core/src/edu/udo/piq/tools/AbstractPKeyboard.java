package edu.udo.piq.tools;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPKeyboard implements PKeyboard {
	
	protected final ObserverList<PKeyboardObs> obsList
		= PiqUtil.createDefaultObserverList();
	
	@Override
	public void addObs(PKeyboardObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PKeyboardObs obs) {
		obsList.remove(obs);
	}
	
	protected void firePressEvent(ActualKey key) {
		obsList.fireEvent(obs -> obs.onKeyPressed(this, key));
	}
	
	protected void fireTriggerEvent(ActualKey key) {
		obsList.fireEvent(obs -> obs.onKeyTriggered(this, key));
	}
	
	protected void fireReleaseEvent(ActualKey key) {
		obsList.fireEvent(obs -> obs.onKeyReleased(this, key));
	}
	
	protected void fireModifierToggledEvent(Modifier mod) {
		obsList.fireEvent(obs -> obs.onModifierToggled(this, mod));
	}
	
	protected void fireStringTypedEvent(String string) {
		obsList.fireEvent(obs -> obs.onStringTyped(this, string));
	}
	
}