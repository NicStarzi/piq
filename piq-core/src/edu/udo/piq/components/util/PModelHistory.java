package edu.udo.piq.components.util;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.util.ThrowException;

public class PModelHistory {
	
	private final List<PModelEdit> editStack = new ArrayList<>();
	private int redoPos = 0;
	
	protected PModelEdit getMergeEdit() {
		if (redoPos == 0) {
			return null;
		}
		return editStack.get(redoPos - 1);
	}
	
	protected void afterEditWasMerged(PModelEdit combine, PModelEdit merged) {
	}
	
	protected void afterEditWasDone(PModelEdit edit) {
		for (int i = editStack.size() - 1; i > redoPos; i--) {
			editStack.remove(i);
		}
		if (editStack.size() == redoPos) {
			editStack.add(edit);
		} else {
			editStack.set(redoPos, edit);
		}
		redoPos = editStack.size();
	}
	
	public PModelEdit getNextUndoEdit() {
		if (redoPos == 0) {
			return null;
		}
		return editStack.get(redoPos - 1);
	}
	
	public boolean canUndo() {
		return getNextUndoEdit() != null;
	}
	
	public void undo() {
		ThrowException.ifFalse(canUndo(), "canUndo() == false");
		getNextUndoEdit().undoThis();
	}
	
	protected void afterEditWasUndone(PModelEdit edit) {
		redoPos--;
	}
	
	public PModelEdit getNextRedoEdit() {
		if (redoPos == editStack.size()) {
			return null;
		}
		return editStack.get(redoPos);
	}
	
	public boolean canRedo() {
		return getNextRedoEdit() != null;
	}
	
	public void redo() {
		ThrowException.ifFalse(canRedo(), "canRedo() == false");
		getNextRedoEdit().redoThis();
	}
	
	protected void afterEditWasRedone(PModelEdit edit) {
		redoPos++;
	}
	
}