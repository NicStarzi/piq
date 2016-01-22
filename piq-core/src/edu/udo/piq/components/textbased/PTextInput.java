package edu.udo.piq.components.textbased;

import java.util.EnumMap;
import java.util.Map;



import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PClipboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.components.collections.PListIndex;

public class PTextInput {
	
	protected static final int PAGE_UP_OR_DOWN_ROW_COUNT = 20;
	
	protected static final Map<Key, KeyResponse> KEY_RESPONSE_MAP = new EnumMap<>(Key.class);
	
	protected final PKeyboardObs keyObs = new PKeyboardObs() {
		public void onStringTyped(PKeyboard keyboard, String typedString) {
			PTextInput.this.onStringTyped(keyboard, typedString);
		}
		public void onKeyPressed(PKeyboard keyboard, Key key) {
			PTextInput.this.onKeyPressed(keyboard, key);
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
	
	protected boolean skipInput(PKeyboard keyboard, Key key) {
		return !owner.hasFocus()
				|| owner.getSelection() == null
				|| !owner.getSelection().hasSelection()
				|| owner.getModel() == null;
	}
	
	protected void onStringTyped(PKeyboard keyboard, String typedString) {
		if (skipInput(keyboard, null)) {
			return;
		}
		if (!owner.isEditable()) {
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
	
	protected void onKeyPressed(PKeyboard keyboard, Key key) {
		if (skipInput(keyboard, key)) {
			return;
		}
		KeyResponse response = KEY_RESPONSE_MAP.get(key);
		if (response != null) {
			response.reactTo(keyboard, this);
		}
	}
	
	protected void keyBackspace(PKeyboard kb) {
		if (!owner.isEditable()) {
			return;
		}
		PTextSelection sel = owner.getSelection();
		PTextModel mdl = owner.getModel();
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
		setSelection(sel, from, from);
	}
	
	protected void keyDelete(PKeyboard kb) {
		if (!owner.isEditable()) {
			return;
		}
		PTextSelection sel = owner.getSelection();
		PTextModel mdl = owner.getModel();
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
		setSelection(sel, from, from);
	}
	
	protected void keyHome(PKeyboard kb) {
		PTextSelection sel = owner.getSelection();
		PTextIndexTable idxTab = owner.getIndexTable();
		if (sel == null || idxTab == null) {
			return;
		}
		int second = sel.getLastSelected().getIndexValue();
		int first = getFirst(sel, second);
		first = idxTab.getIndex(0, idxTab.getRow(first));
		if (kb.isModifierToggled(Modifier.SHIFT)) {
			setSelection(sel, first, second);
		} else {
			setSelection(sel, first, first);
		}
	}
	
	protected void keyEnd(PKeyboard kb) {
		PTextSelection sel = owner.getSelection();
		PTextIndexTable idxTab = owner.getIndexTable();
		if (sel == null || idxTab == null) {
			return;
		}
		int second = sel.getLastSelected().getIndexValue();
		int first = getFirst(sel, second);
		int row = idxTab.getRow(first);
		int last = idxTab.getIndex(idxTab.getColumnCount(row) - 1, row);
		if (kb.isModifierToggled(Modifier.SHIFT)) {
			setSelection(sel, last, second);
		} else {
			setSelection(sel, last, last);
		}
	}
	
	protected void keyLeft(PKeyboard kb) {
		PTextSelection sel = owner.getSelection();
		PTextModel mdl = owner.getModel();
		if (sel == null) {
			return;
		}
		int second = sel.getLastSelected().getIndexValue();
		int first = getFirst(sel, second);
		boolean shift = kb.isModifierToggled(Modifier.SHIFT);
		if (!shift && first != second) {
			PListIndex from = sel.getLowestSelectedIndex();
			setSelection(sel, from, from);
		} else {
			if (first > 0) {
				if (kb.isModifierToggled(Modifier.CTRL)) {
					String text = mdl.getText();
					int firstType = Character.getType(text.charAt(--first));
					while (first > 0) {
						int nowType = Character.getType(text.charAt(first));
						if (firstType != nowType) {
							first++;
							break;
						}
						first--;
					}
				} else {
					first--;
				}
			}
			if (!shift) {
				second = first;
			}
			setSelection(sel, first, second);
		}
	}
	
	protected void keyRight(PKeyboard kb) {
		PTextSelection sel = owner.getSelection();
		PTextModel mdl = owner.getModel();
		if (sel == null) {
			return;
		}
		int second = sel.getLastSelected().getIndexValue();
		int first = getFirst(sel, second);
		boolean shift = kb.isModifierToggled(Modifier.SHIFT);
		if (!shift && first != second) {
			PListIndex to = sel.getHighestSelectedIndex();
			setSelection(sel, to, to);
		} else {
			String text = mdl.getText();
			if (first < text.length()) {
				if (kb.isModifierToggled(Modifier.CTRL)) {
					int firstType = Character.getType(text.charAt(first++));
					while (first < text.length()) {
						int nowType = Character.getType(text.charAt(first));
						if (firstType != nowType) {
							break;
						}
						first++;
					}
				} else {
					first++;
				}
			}
			if (!shift) {
				second = first;
			}
			setSelection(sel, first, second);
		}
	}
	
	protected void keyUp(PKeyboard kb) {
		moveSelectionBy(owner, -1, kb.isModifierToggled(Modifier.SHIFT));
	}
	
	protected void keyDown(PKeyboard kb) {
		moveSelectionBy(owner, 1, kb.isModifierToggled(Modifier.SHIFT));
	}
	
	protected void keyPageUp(PKeyboard kb) {
		moveSelectionBy(owner, -PAGE_UP_OR_DOWN_ROW_COUNT, 
				kb.isModifierToggled(Modifier.SHIFT));
	}
	
	protected void keyPageDown(PKeyboard kb) {
		moveSelectionBy(owner, PAGE_UP_OR_DOWN_ROW_COUNT, 
				kb.isModifierToggled(Modifier.SHIFT));
	}
	
	protected void keySelectAll(PKeyboard kb) {
		if (kb.isModifierToggled(Modifier.CTRL)) {
			PTextSelection sel = owner.getSelection();
			PTextModel mdl = owner.getModel();
			setSelection(sel, 0, mdl.getText().length());
		}
	}
	
	protected void keyCopy(PKeyboard kb) {
		PTextSelection sel = owner.getSelection();
		int from = sel.getLowestSelectedIndex().getIndexValue();
		int to = sel.getHighestSelectedIndex().getIndexValue();
		if (to - from > 0) {
			PRoot root = owner.getRoot();
			if (root != null) {
				PClipboard clipBoard = root.getClipboard();
				if (clipBoard != null) {
					String text = owner.getModel().getText();
					String copyText = text.substring(from, to);
					clipBoard.put(copyText);
				}
			}
		}
	}
	
	protected void keyCut(PKeyboard kb) {
		PTextSelection sel = owner.getSelection();
		int from = sel.getLowestSelectedIndex().getIndexValue();
		int to = sel.getHighestSelectedIndex().getIndexValue();
		if (to - from > 0) {
			PRoot root = owner.getRoot();
			if (root != null) {
				PClipboard clipBoard = root.getClipboard();
				if (clipBoard != null) {
					String text = owner.getModel().getText();
					String copyText = text.substring(from, to);
					clipBoard.put(copyText);
					if (!owner.isEditable()) {
						return;
					}
					String newText = text.substring(0, from) + text.substring(to);
					owner.getModel().setValue(newText);
					setSelection(sel, from, from);
				}
			}
		}
	}
	
	protected void keyPaste(PKeyboard kb) {
		PRoot root = owner.getRoot();
		if (root != null) {
			PClipboard clipBoard = root.getClipboard();
			if (clipBoard != null) {
				Object obj = clipBoard.get();
				if (obj instanceof String) {
					String pasteText = (String) obj;
					PTextSelection sel = owner.getSelection();
					int from = sel.getLowestSelectedIndex().getIndexValue();
					int to = sel.getHighestSelectedIndex().getIndexValue();						
					PTextModel mdl = owner.getModel();
					String text = mdl.getText();
					String newText = text.substring(0, from) + pasteText 
							+ text.substring(to);
					mdl.setValue(newText);
					setSelection(sel, from, from + pasteText.length());
				}
			}
		}
	}
	
	protected void setSelection(PTextSelection sel, int idx1, int idx2) {
		setSelection(sel, new PListIndex(idx1), new PListIndex(idx2));
	}
	
	protected void setSelection(PTextSelection sel, PListIndex idx1, PListIndex idx2) {
		sel.clearSelection();
		sel.addSelection(idx1);
		sel.addSelection(idx2);
	}
	
	protected int getFirst(PTextSelection sel, int second) {
		int from = sel.getLowestSelectedIndex().getIndexValue();
		int to = sel.getHighestSelectedIndex().getIndexValue();
		if (second == from) {
			return to;
		} else {
			return from;
		}
	}
	
	protected void moveSelectionBy(PTextComponent comp, int rowOffset, boolean shift) {
		PTextSelection sel = comp.getSelection();
		PTextIndexTable idxTab = comp.getIndexTable();
		if (sel == null || idxTab == null) {
			return;
		}
		int second = sel.getLastSelected().getIndexValue();
		int first = getFirst(sel, second);
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
		if (shift) {
			setSelection(sel, first, second);
		} else {
			setSelection(sel, first, first);
		}
	}
	
	public static interface KeyResponse {
		public void reactTo(PKeyboard keyboard, PTextInput self);
	}
	
	static {
		KEY_RESPONSE_MAP.put(Key.BACKSPACE, (kb, self) -> self.keyBackspace(kb));
		KEY_RESPONSE_MAP.put(Key.DEL, (kb, self) -> self.keyDelete(kb));
		KEY_RESPONSE_MAP.put(Key.HOME, (kb, self) -> self.keyHome(kb));
		KEY_RESPONSE_MAP.put(Key.END, (kb, self) -> self.keyEnd(kb));
		KEY_RESPONSE_MAP.put(Key.LEFT, (kb, self) -> self.keyLeft(kb));
		KEY_RESPONSE_MAP.put(Key.RIGHT, (kb, self) -> self.keyRight(kb));
		KEY_RESPONSE_MAP.put(Key.UP, (kb, self) -> self.keyUp(kb));
		KEY_RESPONSE_MAP.put(Key.DOWN, (kb, self) -> self.keyDown(kb));
		KEY_RESPONSE_MAP.put(Key.PAGE_UP, (kb, self) -> self.keyPageUp(kb));
		KEY_RESPONSE_MAP.put(Key.PAGE_DOWN, (kb, self) -> self.keyPageDown(kb));
		KEY_RESPONSE_MAP.put(Key.A, (kb, self) -> self.keySelectAll(kb));
		KEY_RESPONSE_MAP.put(Key.COPY, (kb, self) -> self.keyCopy(kb));
		KEY_RESPONSE_MAP.put(Key.CUT, (kb, self) -> self.keyCut(kb));
		KEY_RESPONSE_MAP.put(Key.PASTE, (kb, self) -> self.keyPaste(kb));
	}
	
}