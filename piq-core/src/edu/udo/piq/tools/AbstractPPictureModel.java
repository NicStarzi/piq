package edu.udo.piq.tools;

import edu.udo.piq.components.PPictureModel;
import edu.udo.piq.components.PPictureModelObs;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;

public abstract class AbstractPPictureModel implements PPictureModel {
	
	protected final ObserverList<PPictureModelObs> obsList
		= PCompUtil.createDefaultObserverList();
	
	public void addObs(PPictureModelObs obs) {
		obsList.add(obs);
	}
	
	public void removeObs(PPictureModelObs obs) {
		obsList.remove(obs);
	}
	
	protected void fireImagePathChanged() {
		obsList.fireEvent((obs) -> obs.onImagePathChanged(this));
	}
	
}