package edu.udo.piq.components.collections;

import java.util.function.Predicate;

import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.actions.FocusOwnerAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.StandardComponentActionKey;

public interface PListLike extends PDropComponent {
	
	public static final Predicate<PListLike> CONDITION_MOVE_SELECTION = self -> self.isEnabled()
			&& self.getModel() != null
			&& self.getSelection() != null
			&& self.getSelection().getLastSelected() != null;
	public static final PActionKey KEY_NEXT = StandardComponentActionKey.MOVE_NEXT;
	public static final PAccelerator ACCELERATOR_PRESS_DOWN = new PAccelerator(
			ActualKey.DOWN, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.PRESS);
	public static final PComponentAction ACTION_PRESS_DOWN = new FocusOwnerAction<>(
			PListLike.class, true,
			ACCELERATOR_PRESS_DOWN,
			CONDITION_MOVE_SELECTION,
			self -> self.moveSelectedIndexBy(1));
	
	public static final PActionKey KEY_PREV = StandardComponentActionKey.MOVE_PREV;
	public static final PAccelerator ACCELERATOR_PRESS_UP = new PAccelerator(
			ActualKey.UP, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.PRESS);
	public static final PComponentAction ACTION_PRESS_UP = new FocusOwnerAction<>(
			PListLike.class, true,
			ACCELERATOR_PRESS_UP,
			CONDITION_MOVE_SELECTION,
			self -> self.moveSelectedIndexBy(-1));
	
	public void setSelection(PListSelection listSelection);
	
	@Override
	public PListSelection getSelection();
	
	public void setModel(PListModel listModel);
	
	@Override
	public PListModel getModel();
	
	@Override
	public PListIndex getIndexAt(int x, int y);
	
	public default void moveSelectedIndexBy(PListIndex offset) {
		moveSelectedIndexBy(offset.getIndexValue());
	}
	
	public default void moveSelectedIndexBy(int moveOffset) {
		PListIndex lastSelected = getSelection().getLastSelected();
		int nextSelectedVal = lastSelected.getIndexValue() + moveOffset;
		if (nextSelectedVal >= 0 && nextSelectedVal < getModel().getSize()) {
			PListIndex nextSelected = new PListIndex(nextSelectedVal);
			
			PKeyboard keyBoard = getKeyboard();
			if (keyBoard == null || !keyBoard.isModifierToggled(Modifier.CTRL)) {
				getSelection().clearSelection();
			}
			
			getSelection().addSelection(nextSelected);
		}
	}
	
}