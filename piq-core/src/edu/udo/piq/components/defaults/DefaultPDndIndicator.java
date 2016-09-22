package edu.udo.piq.components.defaults;

import java.util.Objects;

import edu.udo.piq.PDnDIndicator;
import edu.udo.piq.components.PPicture;

public class DefaultPDndIndicator extends PPicture implements PDnDIndicator {
	
	private Object dndPosImageID;
	private Object dndImpImageID;
	private boolean dropPossible;
	
	public DefaultPDndIndicator() {
		setStretchToSize(true);
		setElusive(true);
	}
	
	public DefaultPDndIndicator(Object dropPossibleImageID, Object dropImpossibleImageID) {
		this();
		setDropPossibleImageID(dropPossibleImageID);
		setDropImpossibleImageID(dropImpossibleImageID);
	}
	
	public void setDropPossibleImageID(Object imageID) {
		if (!Objects.equals(getDropPossibleImageID(), imageID)) {
			dndPosImageID = imageID;
			if (isDropPossible()) {
				getModel().setImageID(getDropPossibleImageID());
			}
		}
	}
	
	public Object getDropPossibleImageID() {
		return dndPosImageID;
	}
	
	public void setDropImpossibleImageID(Object imageID) {
		if (!Objects.equals(getDropImpossibleImageID(), imageID)) {
			dndImpImageID = imageID;
			if (!isDropPossible()) {
				getModel().setImageID(getDropImpossibleImageID());
			}
		}
	}
	
	public Object getDropImpossibleImageID() {
		return dndImpImageID;
	}
	
	public void setDropPossible(boolean value) {
		if (isDropPossible() != value) {
			dropPossible = value;
			if (isDropPossible()) {
				getModel().setImageID(getDropPossibleImageID());
			} else {
				getModel().setImageID(getDropImpossibleImageID());
			}
		}
	}
	
	public boolean isDropPossible() {
		return dropPossible;
	}
	
}