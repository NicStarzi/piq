package edu.udo.piq.components.defaults;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PCheckBoxModel;
import edu.udo.piq.components.PCheckBoxModelObs;

public class DefaultPCheckBoxModel implements PCheckBoxModel {
	
	protected final List<PCheckBoxModelObs> obsList = new CopyOnWriteArrayList<>();
	protected boolean checked;
	
	public void setCheckedNoEvent(boolean isChecked) {
		checked = isChecked;
	}
	
	public void setChecked(boolean isChecked) {
		if (checked != isChecked) {
			checked = isChecked;
			fireChangeEvent();
		}
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void addObs(PCheckBoxModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PCheckBoxModelObs obs) {
		obsList.remove(obs);
	}
	
	public void fireChangeEvent() {
		for (PCheckBoxModelObs obs : obsList) {
			obs.onChange(this);
		}
	}
	
	public void fireClickEvent() {
		for (PCheckBoxModelObs obs : obsList) {
			obs.onClick(this);
		}
	}
	
}