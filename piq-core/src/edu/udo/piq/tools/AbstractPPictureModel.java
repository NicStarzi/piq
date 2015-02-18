package edu.udo.piq.tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.udo.piq.components.PPictureModel;
import edu.udo.piq.components.PPictureModelObs;

public abstract class AbstractPPictureModel implements PPictureModel {
	
	private final List<PPictureModelObs> obsList = new CopyOnWriteArrayList<>();
	
	public void addObs(PPictureModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PPictureModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireImagePathChanged() {
		for (PPictureModelObs obs : obsList) {
			obs.imagePathChanged(this);
		}
	}
	
}