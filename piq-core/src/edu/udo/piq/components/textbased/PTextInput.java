package edu.udo.piq.components.textbased;

import java.util.function.Predicate;

import edu.udo.piq.PClipboard;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PKeyboard.VirtualKey;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.CompositeAction;
import edu.udo.piq.actions.FocusOwnerAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.components.collections.list.PListIndex;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.StandardComponentActionKey;

public class PTextInput {
	
	protected final PKeyboardObs keyObs = new PKeyboardObs() {
		@Override
		public void onStringTyped(PKeyboard keyboard, String typedString) {
			PTextInput.this.onStringTyped(keyboard, typedString);
		}
		@Override
		public void onKeyPressed(PKeyboard keyboard, ActualKey key) {
			PTextInput.this.onKeyPressed(keyboard, key);
		}
		@Override
		public void onKeyReleased(PKeyboard keyboard, ActualKey key) {
			PTextInput.this.onKeyReleased(keyboard, key);
		}
		@Override
		public void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
			PTextInput.this.onKeyTriggered(keyboard, key);
		}
		@Override
		public void onModifierToggled(PKeyboard keyboard, Modifier modifier) {
			PTextInput.this.onModifierToggled(keyboard, modifier);
		}
	};
	protected final PTextComponent owner;
	
	public PTextInput(PTextComponent component) {
		owner = component;
	}
	
	public PKeyboardObs getKeyObs() {
		return keyObs;
	}
	
	protected PTextSelection getSelection() {
		return owner.getSelection();
	}
	
	protected PTextModel getModel() {
		return owner.getModel();
	}
	
	protected PTextIndexTable getIndexTable() {
		return owner.getIndexTable();
	}
	
	protected void onStringTyped(PKeyboard keyboard, String typedString) {
		if (!owner.hasFocus()) {
			return;
		}
		if (!EDIT_TEXT_CONDITION.test(owner)) {
			return;
		}
		if (typedString.equals("\n")) {
			keyNewLine(keyboard);
			return;
		}
		PTextSelection selection = getSelection();
		int from = selection.getLowestSelectedIndex().getIndexValue();
		int to = selection.getHighestSelectedIndex().getIndexValue();
		
		int newFrom = from + typedString.length();
		
		PTextModel model = getModel();
		String oldText = model.getText();
		String newText = oldText.substring(0, from) + typedString + oldText.substring(to);
		model.setValue(newText);
		selection.clearSelection();
		selection.addSelection(new PListIndex(newFrom));
	}
	
	protected void onKeyPressed(PKeyboard keyboard, ActualKey key) {}
	
	protected void onKeyReleased(PKeyboard keyboard, ActualKey key) {}
	
	protected void onKeyTriggered(PKeyboard keyboard, ActualKey key) {}
	
	protected void onModifierToggled(PKeyboard keyboard, Modifier modifier) {}
	
	protected void keyNewLine(PKeyboard kb) {
		PTextSelection selection = getSelection();
		int from = selection.getLowestSelectedIndex().getIndexValue();
		int to = selection.getHighestSelectedIndex().getIndexValue();

		PTextModel model = getModel();
		String oldText = model.getText();
		String newText = oldText.substring(0, from) + "\n" + oldText.substring(to);
		model.setValue(newText);
		selection.clearSelection();
		selection.addSelection(new PListIndex(from + 1));
	}
	
//	protected void keyCut(PKeyboard kb) {
//		PTextSelection sel = owner.getSelection();
//		int from = sel.getLowestSelectedIndex().getIndexValue();
//		int to = sel.getHighestSelectedIndex().getIndexValue();
//		if (to - from > 0) {
//			PRoot root = owner.getRoot();
//			if (root != null) {
//				PClipboard clipBoard = root.getClipboard();
//				if (clipBoard != null) {
//					String text = owner.getModel().getText();
//					String copyText = text.substring(from, to);
//					clipBoard.put(copyText);
//					if (!owner.isEditable()) {
//						return;
//					}
//					String newText = text.substring(0, from) + text.substring(to);
//					owner.getModel().setValue(newText);
//					setSelection(sel, from, from);
//				}
//			}
//		}
//	}
	
	protected static int getFirstSelectedIndex(PTextSelection sel, int lastSelected) {
		int from = sel.getLowestSelectedIndex().getIndexValue();
		int to = sel.getHighestSelectedIndex().getIndexValue();
		if (lastSelected == from) {
			return to;
		} else {
			return from;
		}
	}
	
	protected static void moveSelectionBy(PTextComponent comp, int rowOffset) {
		PTextSelection sel = comp.getSelection();
		PTextIndexTable idxTab = comp.getIndexTable();
		if (sel == null || idxTab == null) {
			return;
		}
		int lastSelected = sel.getLastSelected().getIndexValue();
		int first = PTextInput.getFirstSelectedIndex(sel, lastSelected);
		int row = idxTab.getRow(first);
		int col = idxTab.getColumn(first, row);
		row = row + rowOffset;
		if (row < 0) {
			row = 0;
		} else if (row >= idxTab.getRowCount()) {
			row = idxTab.getRowCount() - 1;
		}
		int maxCol = idxTab.getColumnCount(row) - 1;
		if (col >= maxCol) {
			col = maxCol;
		}
		first = idxTab.getIndex(col, row);
		PKeyboard kb = comp.getKeyboard();
		boolean shift = kb != null && kb.isModifierToggled(Modifier.SHIFT);
		if (shift) {
			sel.setSelectionToRange(first, lastSelected);
		} else {
			sel.setSelectionToRange(first, first);
		}
	}
	
	public static final int PAGE_UP_OR_DOWN_ROW_COUNT = 20;
	
	public static final PActionKey KEY_BACKSPACE = new PActionKey("KEY_BACKSPACE");
	public static final PActionKey KEY_DELETE = StandardComponentActionKey.DELETE;
	public static final PActionKey KEY_HOME = new PActionKey("KEY_HOME");
	public static final PActionKey KEY_END = new PActionKey("KEY_END");
	public static final PActionKey KEY_MOVE_LEFT = StandardComponentActionKey.MOVE_PREV;
	public static final PActionKey KEY_MOVE_RIGHT = StandardComponentActionKey.MOVE_NEXT;
	public static final PActionKey KEY_MOVE_UP = new PActionKey("KEY_MOVE_UP");
	public static final PActionKey KEY_MOVE_DOWN = new PActionKey("KEY_MOVE_DOWN");
	public static final PActionKey KEY_PAGE_UP = new PActionKey("KEY_PAGE_UP");
	public static final PActionKey KEY_PAGE_DOWN = new PActionKey("KEY_PAGE_DOWN");
	public static final PActionKey KEY_SELECT_ALL = new PActionKey("KEY_SELECT_ALL");
	public static final PActionKey KEY_COPY = StandardComponentActionKey.COPY;
	public static final PActionKey KEY_CUT = StandardComponentActionKey.CUT;
	public static final PActionKey KEY_PASTE = StandardComponentActionKey.PASTE;
	
	public static final Predicate<PTextComponent> CHANGE_SELECTION_CONDITION =
			self -> self.isEnabled()
					&& self.getSelection() != null
					&& self.getSelection().hasSelection()
					&& self.getModel() != null;
	public static final Predicate<PTextComponent> EDIT_TEXT_CONDITION =
			self -> self.isEditable()
					&& CHANGE_SELECTION_CONDITION.test(self);
	
	public static final PAccelerator ACCELERATOR_BACKSPACE = new PAccelerator(ActualKey.BACKSPACE, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_DELETE = new PAccelerator(ActualKey.DELETE, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_HOME = new PAccelerator(ActualKey.HOME);
	public static final PAccelerator ACCELERATOR_END = new PAccelerator(ActualKey.END);
	public static final PAccelerator ACCELERATOR_LEFT = new PAccelerator(ActualKey.LEFT, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_RIGHT = new PAccelerator(ActualKey.RIGHT, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_UP = new PAccelerator(ActualKey.UP, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_DOWN = new PAccelerator(ActualKey.DOWN, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_PAGE_UP = new PAccelerator(ActualKey.PAGE_UP, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_PAGE_DOWN = new PAccelerator(ActualKey.PAGE_DOWN, KeyInputType.PRESS);
	public static final PAccelerator ACCELERATOR_CTRL_A = new PAccelerator(ActualKey.A, Modifier.COMMAND);
	public static final PAccelerator ACCELERATOR_COPY = new PAccelerator(VirtualKey.COPY);
	public static final PAccelerator ACCELERATOR_CUT = new PAccelerator(VirtualKey.CUT);
	public static final PAccelerator ACCELERATOR_PASTE = new PAccelerator(VirtualKey.PASTE);
	
	public static final PComponentAction ACTION_BACKSPACE = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_BACKSPACE,
			EDIT_TEXT_CONDITION,
			self -> {
				PTextSelection sel = self.getSelection();
				PTextModel mdl = self.getModel();
				if (sel == null || mdl == null) {
					return;
				}
				int from = sel.getLowestSelectedIndex().getIndexValue();
				int to = sel.getHighestSelectedIndex().getIndexValue();
				String oldText = mdl.getText();
				String newText;
				if (from != to) {
					newText = oldText.substring(0, from) + oldText.substring(to);
				} else if (from > 0) {
					newText = oldText.substring(0, from - 1) + oldText.substring(to);
					from--;
				} else {
					return;
				}
				mdl.setValue(newText);
				sel.setSelectionToRange(from, from);
			});
	public static final PComponentAction ACTION_DELETE = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_DELETE,
			EDIT_TEXT_CONDITION,
			self -> {
				PTextSelection sel = self.getSelection();
				PTextModel mdl = self.getModel();
				if (sel == null || mdl == null) {
					return;
				}
				int from = sel.getLowestSelectedIndex().getIndexValue();
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
				sel.setSelectionToRange(from, from);
			});
	public static final PComponentAction ACTION_HOME = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_HOME,
			CHANGE_SELECTION_CONDITION,
			self -> {
				PTextSelection sel = self.getSelection();
				PTextIndexTable idxTab = self.getIndexTable();
				if (sel == null || idxTab == null) {
					return;
				}
				int lastSelected = sel.getLastSelected().getIndexValue();
				int first = PTextInput.getFirstSelectedIndex(sel, lastSelected);
				first = idxTab.getIndex(0, idxTab.getRow(first));
				if (self.getKeyboard().isModifierToggled(Modifier.SHIFT)) {
					sel.setSelectionToRange(first, lastSelected);
				} else {
					sel.setSelectionToRange(first, first);
				}
			});
	public static final PComponentAction ACTION_END = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_END,
			CHANGE_SELECTION_CONDITION,
			self -> {
				PTextSelection sel = self.getSelection();
				PTextIndexTable idxTab = self.getIndexTable();
				if (sel == null || idxTab == null) {
					return;
				}
				int second = sel.getLastSelected().getIndexValue();
				int first = PTextInput.getFirstSelectedIndex(sel, second);
				int row = idxTab.getRow(first);
				int last = idxTab.getIndex(idxTab.getColumnCount(row) - 1, row);
				if (self.getKeyboard().isModifierToggled(Modifier.SHIFT)) {
					sel.setSelectionToRange(last, second);
				} else {
					sel.setSelectionToRange(last, last);
				}
			});
	public static final PComponentAction ACTION_MOVE_LEFT = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_LEFT,
			CHANGE_SELECTION_CONDITION,
			self -> {
				PTextSelection sel = self.getSelection();
				PTextModel mdl = self.getModel();
				if (sel == null) {
					return;
				}
				int lastSelected = sel.getLastSelected().getIndexValue();
				int firstSelected = PTextInput.getFirstSelectedIndex(sel, lastSelected);
				PKeyboard kb = self.getKeyboard();
				boolean shift = kb.isModifierToggled(Modifier.SHIFT);
				if (!shift && firstSelected != lastSelected) {
					PListIndex from = sel.getLowestSelectedIndex();
					sel.setSelectionToRange(from, from);
				} else {
					if (firstSelected > 0) {
						if (kb.isModifierToggled(Modifier.CTRL)) {
							String text = mdl.getText();
							int firstType = Character.getType(text.charAt(--firstSelected));
							while (firstSelected > 0) {
								int nowType = Character.getType(text.charAt(firstSelected));
								if (firstType != nowType) {
									firstSelected++;
									break;
								}
								firstSelected--;
							}
						} else {
							firstSelected--;
						}
					}
					if (!shift) {
						lastSelected = firstSelected;
					}
					sel.setSelectionToRange(firstSelected, lastSelected);
				}
			});
	public static final PComponentAction ACTION_MOVE_RIGHT = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_RIGHT,
			CHANGE_SELECTION_CONDITION,
			self -> {
				PTextSelection sel = self.getSelection();
				PTextModel mdl = self.getModel();
				if (sel == null) {
					return;
				}
				int lastSelected = sel.getLastSelected().getIndexValue();
				int firstSelected = PTextInput.getFirstSelectedIndex(sel, lastSelected);
				PKeyboard kb = self.getKeyboard();
				boolean shift = kb.isModifierToggled(Modifier.SHIFT);
				if (!shift && firstSelected != lastSelected) {
					PListIndex to = sel.getHighestSelectedIndex();
					sel.setSelectionToRange(to, to);
				} else {
					String text = mdl.getText();
					if (firstSelected < text.length()) {
						if (kb.isModifierToggled(Modifier.CTRL)) {
							int firstType = Character.getType(text.charAt(firstSelected++));
							while (firstSelected < text.length()) {
								int nowType = Character.getType(text.charAt(firstSelected));
								if (firstType != nowType) {
									break;
								}
								firstSelected++;
							}
						} else {
							firstSelected++;
						}
					}
					if (!shift) {
						lastSelected = firstSelected;
					}
					sel.setSelectionToRange(firstSelected, lastSelected);
				}
			});
	public static final PComponentAction ACTION_MOVE_UP = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_UP,
			CHANGE_SELECTION_CONDITION,
			self -> PTextInput.moveSelectionBy(self, -1));
	public static final PComponentAction ACTION_MOVE_DOWN = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_DOWN,
			CHANGE_SELECTION_CONDITION,
			self -> PTextInput.moveSelectionBy(self, +1));
	public static final PComponentAction ACTION_PAGE_UP = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_PAGE_UP,
			CHANGE_SELECTION_CONDITION,
			self -> PTextInput.moveSelectionBy(self, -PAGE_UP_OR_DOWN_ROW_COUNT));
	public static final PComponentAction ACTION_PAGE_DOWN = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_PAGE_DOWN,
			CHANGE_SELECTION_CONDITION,
			self -> PTextInput.moveSelectionBy(self, +PAGE_UP_OR_DOWN_ROW_COUNT));
	public static final PComponentAction ACTION_SELECT_ALL = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_CTRL_A,
			CHANGE_SELECTION_CONDITION,
			self -> self.getSelection().selectAll(self.getModel()));
	public static final PComponentAction ACTION_COPY = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_COPY,
			self -> {
				PRoot root = self.getRoot();
				if (root == null) {
					return false;
				}
				if (root.getClipboard() == null) {
					return false;
				}
				PTextSelection sel = self.getSelection();
				return sel != null
						&& sel.hasSelection()
						&& self.getModel() != null
						&& sel.getLowestSelectedIndex().getIndexValue() < sel.getHighestSelectedIndex().getIndexValue();
			},
			self -> {
				PTextSelection sel = self.getSelection();
				int from = sel.getLowestSelectedIndex().getIndexValue();
				int to = sel.getHighestSelectedIndex().getIndexValue();
				PClipboard clipBoard = self.getRoot().getClipboard();
				String text = self.getModel().getText();
				String copyText = text.substring(from, to);
				clipBoard.put(copyText);
			});
	public static final PComponentAction ACTION_PASTE = new FocusOwnerAction<>(
			PTextComponent.class, true,
			ACCELERATOR_PASTE,
			self -> {
				PRoot root = self.getRoot();
				if (root == null) {
					return false;
				}
				PClipboard clipBoard = root.getClipboard();
				if (clipBoard == null) {
					return false;
				}
				if (clipBoard.get() == null) {
					return false;
				}
				PTextSelection sel = self.getSelection();
				return sel != null
						&& sel.hasSelection()
						&& self.getModel() != null;
			},
			self -> {
				PClipboard clipBoard = self.getRoot().getClipboard();
				Object content = clipBoard.get();
				String pasteText = content.toString();
				
				PTextSelection sel = self.getSelection();
				int from = sel.getLowestSelectedIndex().getIndexValue();
				int to = sel.getHighestSelectedIndex().getIndexValue();
				PTextModel mdl = self.getModel();
				String text = mdl.getText();
				String newText = text.substring(0, from) + pasteText
						+ text.substring(to);
				mdl.setValue(newText);
				sel.setSelectionToRange(from, from + pasteText.length());
			});
	public static final PComponentAction ACTION_CUT = new CompositeAction(ACCELERATOR_CUT, KEY_COPY, KEY_DELETE);
}