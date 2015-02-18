package edu.udo.piq.components.util;

import java.util.ArrayList;
import java.util.List;

public class PModelHistory {
	
	private final List<PModelEdit> editStack = new ArrayList<>();
	private int redoPos = 0;
	
	void performDo(PModelEdit edit) {
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
		if (!canUndo()) {
			throw new IllegalStateException("canUndo()==false");
		}
		getNextUndoEdit().undoThis();
	}
	
	void performUndo(PModelEdit edit) {
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
		if (!canRedo()) {
			throw new IllegalStateException("canRedo()==false");
		}
		getNextRedoEdit().redoThis();
	}
	
	void performRedo(PModelEdit edit) {
		redoPos++;
	}
	
}