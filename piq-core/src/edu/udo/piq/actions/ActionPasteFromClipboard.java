package edu.udo.piq.actions;

import edu.udo.piq.PClipboard;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionComponent;
import edu.udo.piq.components.popup2.ImmutablePActionIndicator;
import edu.udo.piq.components.popup2.PComponentActionIndicator;

public class ActionPasteFromClipboard extends AbstractPComponentAction implements PComponentAction {
	
	public static final PComponentAction INSTANCE = new ActionPasteFromClipboard();
	public static final Object DEFAULT_KEY = StandardComponentActionKey.PASTE;
	public static final PComponentActionIndicator DEFAULT_INDICATOR = 
			new ImmutablePActionIndicator(DEFAULT_KEY, "Paste", null, 
					new PAccelerator(ActualKey.P, Modifier.COMMAND, FocusPolicy.NEVER));
	
	public ActionPasteFromClipboard() {
		setAccelerator(ActualKey.V, Modifier.COMMAND, 
				FocusPolicy.THIS_OR_CHILD_HAS_FOCUS);
	}
	
//	@Override
//	public Object getLabelValue(PRoot root) {
//		return "Paste";
//	}
	
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
		PModelIndex selectedIndex = selection.getLastSelected();
		if (selectedIndex == null) {
			selectedIndex = model.iterator().next();
		}
		return model.canAdd(selectedIndex, clipBoard.get());
	}
	
	@Override
	public void tryToPerform(PRoot root) {
		PSelectionComponent selectComp = (PSelectionComponent) root.getLastStrongFocusOwner();
		PModel model = selectComp.getModel();
		PSelection selection = selectComp.getSelection();
		PModelIndex selectedIndex = selection.getLastSelected();
		if (selectedIndex == null) {
			selectedIndex = model.iterator().next();
		}
		Object obj = root.getClipboard().get();
		model.add(selectedIndex, obj);
	}
}