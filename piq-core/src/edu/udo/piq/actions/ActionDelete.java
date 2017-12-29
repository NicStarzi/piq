package edu.udo.piq.actions;

import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionComponent;
import edu.udo.piq.components.popup2.ImmutablePActionIndicator;
import edu.udo.piq.components.popup2.PComponentActionIndicator;
import edu.udo.piq.components.textbased.PTextComponent;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.components.textbased.PTextSelection;

public class ActionDelete extends AbstractPComponentAction implements PComponentAction {
	
	public static final PComponentAction INSTANCE = new ActionDelete();
	public static final Object DEFAULT_KEY = StandardComponentActionKey.DELETE;
	public static final PComponentActionIndicator DEFAULT_INDICATOR = 
			new ImmutablePActionIndicator(DEFAULT_KEY, "Delete", null, 
					new PAccelerator(ActualKey.DELETE, FocusPolicy.NEVER));
	
	public ActionDelete() {
		setAccelerator(ActualKey.DELETE, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS);
	}
	
//	@Override
//	public Object getLabelValue(PRoot root) {
//		return "Delete";
//	}
	
	@Override
	public boolean isEnabled(PRoot root) {
		PComponent focusOwner = root.getLastStrongFocusOwner();
		if (focusOwner == null) {
			return false;
		}
		if (!focusOwner.hasAction(this)) {
			return false;
		}
		if (focusOwner instanceof PTextComponent) {
			PTextComponent txtComp = (PTextComponent) focusOwner;
			return isEnabledForFocusOwner(txtComp);
		}
		if (focusOwner instanceof PSelectionComponent) {
			PSelectionComponent selectComp = (PSelectionComponent) focusOwner;
			return isEnabledForFocusOwner(selectComp);
		}
		return false;
	}
	
	public boolean isEnabledForFocusOwner(PTextComponent txtComp) {
		if (!txtComp.isEditable()) {
			return false;
		}
		PTextSelection sel = txtComp.getSelection();
		PTextModel mdl = txtComp.getModel();
		if (sel == null || mdl == null) {
			return false;
		}
		PListIndex high = sel.getHighestSelectedIndex();
		PListIndex low = sel.getLowestSelectedIndex();
		return low.compareTo(high) < 0;
	}
	
	public boolean isEnabledForFocusOwner(PSelectionComponent selectComp) {
		PSelection selection = selectComp.getSelection();
		if (selection == null) {
			return false;
		}
		List<PModelIndex> selectedIndices = selection.getAllSelected();
		if (selectedIndices == null || selectedIndices.isEmpty()) {
			return false;
		}
		PModel model = selectComp.getModel();
		if (model == null) {
			return false;
		}
		for (PModelIndex index : selectedIndices) {
			if (!model.canRemove(index)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void tryToPerform(PRoot root) {
		PComponent focusOwner = root.getLastStrongFocusOwner();
		if (focusOwner instanceof PTextComponent) {
			PTextComponent txtComp = (PTextComponent) focusOwner;
			tryToPerformFor(root, txtComp);
		}
		if (focusOwner instanceof PSelectionComponent) {
			PSelectionComponent selectComp = (PSelectionComponent) focusOwner;
			tryToPerformFor(root, selectComp);
		}
	}
	
	public void tryToPerformFor(PRoot root, PTextComponent txtComp) {
		PTextSelection sel = txtComp.getSelection();
		PTextModel mdl = txtComp.getModel();
		PListIndex low = sel.getLowestSelectedIndex();
		int from = low.getIndexValue();
		int to = sel.getHighestSelectedIndex().getIndexValue();
		String oldText = mdl.getText();
		String newText;
		if (from != to) {
			newText = oldText.substring(0, from) + oldText.substring(to);
		} else if (from < oldText.length()) {
			newText = oldText.substring(0, from) + oldText.substring(to + 1);
		} else {
			return;
		}
		mdl.setValue(newText);
		sel.clearSelection();
		sel.addSelection(low);
		sel.addSelection(low);
	}
	
	public void tryToPerformFor(PRoot root, PSelectionComponent selectComp) {
		PSelection selection = selectComp.getSelection();
		PModel model = selectComp.getModel();
		model.removeAll(selection.getAllSelected());
	}
}