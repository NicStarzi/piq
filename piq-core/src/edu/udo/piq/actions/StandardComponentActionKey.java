package edu.udo.piq.actions;

public class StandardComponentActionKey {
	
	// simple interaction controls like PButton, PCheckBox or PRadioButton
	public static final PActionKey INTERACT = new PActionKey("INTERACT");
	
	// traversal through lists and trees, as well as manipulating spinners and sliders
	public static final PActionKey MOVE_NEXT = new PActionKey("MOVE_NEXT");
	public static final PActionKey MOVE_PREV = new PActionKey("MOVE_PREV");
	public static final PActionKey MOVE_IN = new PActionKey("MOVE_IN");
	public static final PActionKey MOVE_OUT = new PActionKey("MOVE_OUT");
	
	// multi-value model components like PList, PTree or PTable
	public static final PActionKey NEW = new PActionKey("NEW");
	public static final PActionKey ADD = new PActionKey("ADD");
	public static final PActionKey EDIT = new PActionKey("EDIT");
	
	// multi-value model components like PList, PTree or PTable as well as text components like PTextField or PTextArea
	public static final PActionKey DELETE = new PActionKey("DELETE");
	public static final PActionKey COPY = new PActionKey("COPY");
	public static final PActionKey CUT = new PActionKey("CUT");
	public static final PActionKey PASTE = new PActionKey("PASTE");
	
	// support for undo-redo histories
	public static final PActionKey UNDO = new PActionKey("UNDO");
	public static final PActionKey REDO = new PActionKey("REDO");
	
	private StandardComponentActionKey() {
		throw new IllegalStateException();
	}
}