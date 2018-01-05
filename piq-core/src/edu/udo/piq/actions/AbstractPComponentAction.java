package edu.udo.piq.actions;

import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PiqUtil;

public abstract class AbstractPComponentAction implements PComponentAction {
	
	protected final ObserverList<PComponentActionObs> obsList = PiqUtil.createDefaultObserverList();
	protected PAccelerator accelerator;
	
	public AbstractPComponentAction() {}
	
	public AbstractPComponentAction(PAccelerator initialAccelerator) {
		setAccelerator(initialAccelerator);
	}
	
	public void setAccelerator(Key key) {
		setAccelerator(new PAccelerator(key));
	}
	
	public void setAccelerator(Key key, FocusPolicy focusPolicy) {
		setAccelerator(new PAccelerator(key, focusPolicy));
	}
	
	public void setAccelerator(Key key, Modifier mod1, Modifier mod2) {
		setAccelerator(new PAccelerator(key, mod1, mod2));
	}
	
	public void setAccelerator(Key key, Modifier mod) {
		setAccelerator(new PAccelerator(key, mod));
	}
	
	public void setAccelerator(Key key, Modifier mod,
			FocusPolicy focusPolicy)
	{
		setAccelerator(new PAccelerator(key, mod, focusPolicy));
	}
	
	public void setAccelerator(Key key, Modifier mod,
			FocusPolicy focusPolicy, KeyInputType inputType)
	{
		setAccelerator(new PAccelerator(key, mod, focusPolicy, inputType));
	}
	
	public void setAccelerator(PAccelerator value) {
		if (accelerator != value) {
			accelerator = value;
			fireChangeEvent();
		}
	}
	
	@Override
	public PAccelerator getAccelerator() {
		return accelerator;
	}
	
	@Override
	public void addObs(PComponentActionObs obs) {
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PComponentActionObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireChangeEvent() {
		obsList.forEach(obs -> obs.onChange(this));
	}
	
}