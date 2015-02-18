package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PCheckBoxModel;
import edu.udo.piq.components.PCheckBoxModelObs;
import edu.udo.piq.components.util.PModelHistory;

public class DefaultPCheckBoxModel implements PCheckBoxModel {
	
	protected final List<PCheckBoxModelObs> obsList = new CopyOnWriteArrayList<>();
//	protected final PModelHistory history = new PModelHistory();
	protected boolean checked;
	
	public void setChecked(final boolean isChecked) {
		final boolean oldIsChecked = checked;
		if (oldIsChecked != isChecked) {
//			new PModelEdit(getHistory()) {
//				protected void undoThisInternal() {
//					checked = oldIsChecked;
//					fireChangeEvent();
//				}
//				protected void doThisInternal() {
//					checked = isChecked;
//					fireChangeEvent();
//				}
//			}.doThis();
			checked = isChecked;
			fireChangeEvent();
		}
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public PModelHistory getHistory() {
		return null;
//		return history;
	}
	
	public void addObs(PCheckBoxModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PCheckBoxModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireChangeEvent() {
		for (PCheckBoxModelObs obs : obsList) {
			obs.onChange(this);
		}
	}
	
}