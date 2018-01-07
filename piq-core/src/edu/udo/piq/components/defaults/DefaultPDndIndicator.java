package edu.udo.piq.components.defaults;

import java.util.Objects;

import edu.udo.piq.components.PPicture;
import edu.udo.piq.dnd.PDnDIndicator;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;

public class DefaultPDndIndicator extends PPicture implements PDnDIndicator {
	
	private Object dndPosImageID;
	private Object dndImpImageID;
	private boolean dropPossible;
	
	public DefaultPDndIndicator() {
		setAlignmentX(AlignmentX.FILL);
		setAlignmentY(AlignmentY.FILL);
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
				getModel().setValue(getDropPossibleImageID());
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
				getModel().setValue(getDropImpossibleImageID());
			}
		}
	}
	
	public Object getDropImpossibleImageID() {
		return dndImpImageID;
	}
	
	@Override
	public void setDropPossible(boolean value) {
		if (isDropPossible() != value) {
			dropPossible = value;
			if (isDropPossible()) {
				getModel().setValue(getDropPossibleImageID());
			} else {
				getModel().setValue(getDropImpossibleImageID());
			}
		}
	}
	
	@Override
	public boolean isDropPossible() {
		return dropPossible;
	}
	
}