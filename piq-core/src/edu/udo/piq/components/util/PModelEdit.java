package edu.udo.piq.components.util;

import edu.udo.piq.util.ThrowException;

public abstract class PModelEdit {
	
	private final PModelHistory history;
	private State state;
	
	public PModelEdit(PModelHistory history) {
		ThrowException.ifNull(history, "history == null");
		this.history = history;
		state = State.CAN_DO;
	}
	
	public final void doThis() {
		ThrowException.ifNotEqual(State.CAN_DO, state, "getState() != CAN_DO");
		PModelEdit mergeTarget = history.getMergeEdit();
		if (mergeTarget != null && mergeTarget.tryToMerge(this)) {
			wasMergedInternal(mergeTarget);
			history.afterEditWasMerged(mergeTarget, this);
			state = State.INVALID;
		} else {
			doThisInternal();
			history.afterEditWasDone(this);
			state = State.CAN_UNDO;
		}
	}
	
	public final void undoThis() {
		ThrowException.ifNotEqual(State.CAN_UNDO, state, "getState() != CAN_UNDO");
		undoThisInternal();
		history.afterEditWasUndone(this);
		state = State.CAN_REDO;
	}
	
	public final void redoThis() {
		ThrowException.ifNotEqual(State.CAN_REDO, state, "getState() != CAN_REDO");
		redoThisInternal();
		history.afterEditWasRedone(this);
		state = State.CAN_UNDO;
	}
	
	private final boolean tryToMerge(PModelEdit other) {
		ThrowException.ifNull(other, "other == null");
		ThrowException.ifNotEqual(State.CAN_DO, other.state, "other.getState() != CAN_DO");
		ThrowException.ifTrue(
				state != State.CAN_REDO && getState() != State.CAN_UNDO, 
				"getState() != CAN_REDO && getState() != CAN_UNDO");
		return tryToMergeInternal(other);
	}
	
	protected boolean tryToMergeInternal(PModelEdit other) {
		return false;
	}
	
	protected abstract void doThisInternal();
	
	protected abstract void undoThisInternal();
	
	protected void redoThisInternal() {
		doThisInternal();
	}
	
	protected void wasMergedInternal(PModelEdit combined) {
	}
	
	public String getUndoString() {
		return toString();
	}
	
	public String getRedoString() {
		return toString();
	}
	
	public State getState() {
		return state;
	}
	
	public static enum State {
		CAN_UNDO,
		CAN_REDO,
		CAN_DO,
		INVALID,
		;
	}
	
}