package edu.udo.piq.actions;

import edu.udo.piq.PClipboard;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionComponent;
import edu.udo.piq.components.popup2.ImmutablePActionIndicator;
import edu.udo.piq.components.popup2.PComponentActionIndicator;

public class ActionCopyToClipboard extends AbstractPComponentAction implements PComponentAction {
	
	public static final PComponentAction INSTANCE = new ActionCopyToClipboard();
	public static final Object DEFAULT_KEY = StandardComponentActionKey.COPY;
	public static final PComponentActionIndicator DEFAULT_INDICATOR = 
			new ImmutablePActionIndicator(DEFAULT_KEY, "Copy", null, 
					new PAccelerator(ActualKey.C, Modifier.COMMAND, FocusPolicy.NEVER));
	
	public ActionCopyToClipboard() {
		setAccelerator(ActualKey.C, Modifier.COMMAND, 
				FocusPolicy.THIS_OR_CHILD_HAS_FOCUS);
	}
	
	@Override
	public boolean isEnabled(PRoot root) {
		PClipboard clipBoard = root.getClipboard();
		if (clipBoard == null) {
			return false;
		}
		PComponent focusOwner = root.getLastStrongFocusOwner();
		if (focusOwner == null) {
			return false;
		}
		if (!focusOwner.hasAction(this)) {
			return false;
		}
		if (!(focusOwner instanceof PSelectionComponent)) {
			return false;
		}
		PSelectionComponent selectComp = (PSelectionComponent) focusOwner;
		PSelection selection = selectComp.getSelection();
		if (selection == null) {
			return false;
		}
		PModel model = selectComp.getModel();
		if (model == null) {
			return false;
		}
		return selection.hasSelection();
	}
	
	@Override
	public void tryToPerform(PRoot root) {
		PSelectionComponent selectComp = (PSelectionComponent) root.getLastStrongFocusOwner();
		PSelection selection = selectComp.getSelection();
		PModel model = selectComp.getModel();
		Object content = model.get(selection.getOneSelected());
		root.getClipboard().put(content);
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof ActionCopyToClipboard;
	}
	
	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
	
}