package edu.udo.piq.components.util;

import java.util.ArrayList;
import java.util.List;

import edu.udo.piq.util.ThrowException;

public class PUndoRedoStack {
	
	private final List<PEdit> editStack = new ArrayList<>();
	private int redoPos = 0;
	
	protected PEdit getMergeEdit() {
		return getNextUndoEdit();
	}
	
	protected void afterEditWasMerged(PEdit combine, PEdit merged) {
	}
	
	protected void afterEditWasDone(PEdit edit) {
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
	
	public PEdit getNextUndoEdit() {
		if (redoPos == 0) {
			return null;
		}
		return editStack.get(redoPos - 1);
	}
	
	public boolean canUndo() {
		return redoPos > 0;
	}
	
	public void undo() {
		ThrowException.ifFalse(canUndo(), "canUndo() == false");
		getNextUndoEdit().undoThis();
	}
	
	protected void afterEditWasUndone(PEdit edit) {
		redoPos--;
	}
	
	public PEdit getNextRedoEdit() {
		if (redoPos == editStack.size()) {
			return null;
		}
		return editStack.get(redoPos);
	}
	
	public boolean canRedo() {
		return redoPos < editStack.size();
	}
	
	public void redo() {
		ThrowException.ifFalse(canRedo(), "canRedo() == false");
		getNextRedoEdit().redoThis();
	}
	
	protected void afterEditWasRedone(PEdit edit) {
		redoPos++;
	}
	
}