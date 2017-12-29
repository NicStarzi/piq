package edu.udo.piq.components.popup2;

import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.AbstractPComponentAction;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PComponentAction;

public class ActionRemoveOnEscape extends AbstractPComponentAction implements PComponentAction {
	
	public static final Object DEFAULT_KEY = new Object();
	public static final PComponentAction INSTANCE = new ActionRemoveOnEscape();
	
	public ActionRemoveOnEscape() {
		setAccelerator(ActualKey.ESCAPE, FocusPolicy.ALWAYS);
	}
	
	@Override
	public boolean isEnabled(PRoot root) {
		PMenuBody body = root.getFocusOwner().getFirstAncestorOfType(PMenuBody.class);
		return body != null;
	}
	
	@Override
	public void tryToPerform(PRoot root) {
		PMenuBody body = root.getFocusOwner().getFirstAncestorOfType(PMenuBody.class);
		if (body != null) {
			body.fireCloseRequestEvent();
		}
	}
	
}