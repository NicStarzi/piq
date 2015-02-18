package edu.udo.piq.components.util;

public abstract class PModelEdit {
	
	private final PModelHistory history;
	private State state;
	
	public PModelEdit(PModelHistory history) {
		this.history = history;
		state = State.CAN_DO;
	}
	
	public final void doThis() {
		if (state != State.CAN_DO) {
			throw new IllegalStateException("state="+state);
		}
		doThisInternal();
		history.performDo(this);
		state = State.CAN_UNDO;
	}
	
	public final void undoThis() {
		if (state != State.CAN_UNDO) {
			throw new IllegalStateException("state="+state);
		}
		undoThisInternal();
		history.performUndo(this);
		state = State.CAN_REDO;
	}
	
	public final void redoThis() {
		if (state != State.CAN_REDO) {
			throw new IllegalStateException("state="+state);
		}
		redoThisInternal();
		history.performRedo(this);
		state = State.CAN_UNDO;
	}
	
	protected abstract void doThisInternal();
	
	protected abstract void undoThisInternal();
	
	protected void redoThisInternal() {
		doThisInternal();
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
		CAN_DO;
	}
	
}