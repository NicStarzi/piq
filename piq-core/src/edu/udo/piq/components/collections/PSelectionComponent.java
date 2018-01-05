package edu.udo.piq.components.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PClipboard;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.VirtualKey;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.CompositeAction;
import edu.udo.piq.actions.FocusOwnerAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PAccelerator.KeyInputType;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.actions.StandardComponentActionKey;
import edu.udo.piq.components.popup2.ImmutablePActionIndicator;
import edu.udo.piq.components.popup2.PComponentActionIndicator;
import edu.udo.piq.util.ThrowException;

public interface PSelectionComponent extends PComponent {
	
	public static final PActionKey KEY_DELETE = StandardComponentActionKey.DELETE;
	public static final PAccelerator ACCELERATOR_DEL = new PAccelerator(
			ActualKey.DELETE, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.TRIGGER);
	public static final PComponentAction ACTION_DELETE = new FocusOwnerAction<>(
			PSelectionComponent.class, true,
			ACCELERATOR_DEL,
			self -> {
				if (!self.isEnabled()) {
					return false;
				}
				PSelection selection = self.getSelection();
				if (selection == null || !selection.hasSelection()) {
					return false;
				}
				PModel model = self.getModel();
				if (model == null) {
					return false;
				}
				return model.canRemove(selection.getAllSelected());
			},
			self -> {
				PSelection selection = self.getSelection();
				PModelIndex lastIdx = selection.getLastSelected();
				PModel model = self.getModel();
				model.removeAll(selection.getAllSelected());
				if (model.contains(lastIdx)) {
					self.setSelected(lastIdx);
				} else if (model.getSize() > 0) {
					self.setSelected(model.iterator().next());
				}
			});
	public static final PComponentActionIndicator INDICATOR_DELETE =
			new ImmutablePActionIndicator(KEY_DELETE, "Delete", "Delete", ACCELERATOR_DEL);
	
	public static final PActionKey KEY_COPY = StandardComponentActionKey.COPY;
	public static final PAccelerator ACCELERATOR_COPY = new PAccelerator(
			VirtualKey.COPY, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.TRIGGER);
	public static final PComponentAction ACTION_COPY = new FocusOwnerAction<>(
			PSelectionComponent.class, true,
			ACCELERATOR_COPY,
			self -> {
				PRoot root = self.getRoot();
				if (root == null) {
					return false;
				}
				if (root.getClipboard() == null) {
					return false;
				}
				if (self.getModel() == null) {
					return false;
				}
				PSelection selection = self.getSelection();
				if (selection == null) {
					return false;
				}
				return selection.hasSelection();
			},
			self -> {
				PModelIndex index = self.getSelection().getLastSelected();
				Object content = self.getModel().get(index);
				PClipboard clipBoard = self.getRoot().getClipboard();
				clipBoard.put(content);
			});
	public static final PComponentActionIndicator INDICATOR_COPY =
			new ImmutablePActionIndicator(KEY_COPY, "Copy", "Copy", ACCELERATOR_COPY);
	
	public static final PActionKey KEY_PASTE = StandardComponentActionKey.PASTE;
	public static final PAccelerator ACCELERATOR_PASTE = new PAccelerator(
			VirtualKey.PASTE, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.TRIGGER);
	public static final PComponentAction ACTION_PASTE = new FocusOwnerAction<>(
			PSelectionComponent.class, true,
			ACCELERATOR_PASTE,
			self -> {
				if (!self.isEnabled()) {
					return false;
				}
				PRoot root = self.getRoot();
				if (root == null) {
					return false;
				}
				PClipboard clipBoard = root.getClipboard();
				if (clipBoard == null) {
					return false;
				}
				Object content = clipBoard.get();
				if (content == null) {
					return false;
				}
				PModel model = self.getModel();
				if (model == null) {
					return false;
				}
				PSelection selection = self.getSelection();
				if (selection == null) {
					return false;
				}
				PModelIndex selectedIndex = selection.getLastSelected();
				if (selectedIndex == null) {
					selectedIndex = model.iterator().next();
				}
				return model.canAdd(selectedIndex, content);
			},
			self -> {
				PModel model = self.getModel();
				PModelIndex selectedIndex = self.getSelection().getLastSelected();
				if (selectedIndex == null) {
					selectedIndex = model.iterator().next();
				}
				Object content = self.getRoot().getClipboard().get();
				model.add(selectedIndex, content);
				self.setSelected(selectedIndex);
			});
	public static final PComponentActionIndicator INDICATOR_PASTE =
			new ImmutablePActionIndicator(KEY_PASTE, "Paste", "Paste", ACCELERATOR_PASTE);
	
	public static final PActionKey KEY_CUT = StandardComponentActionKey.CUT;
	public static final PAccelerator ACCELERATOR_CUT = new PAccelerator(
			VirtualKey.CUT, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS, KeyInputType.TRIGGER);
	public static final PComponentAction ACTION_CUT = new CompositeAction(ACCELERATOR_CUT, KEY_COPY, KEY_DELETE);
	public static final PComponentActionIndicator INDICATOR_CUT =
			new ImmutablePActionIndicator(KEY_CUT, "Cut", "Cut", ACCELERATOR_CUT);
	
	public PSelection getSelection();
	
	public PModel getModel();
	
	public PModelIndex getIndexAt(int x, int y);
	
	public void setEnabled(boolean value);
	
	public boolean isEnabled();
	
	public default void setSelected(Object value) {
		ThrowException.ifNull(getModel(), "getModel() == null");
		PModelIndex index = getModel().getIndexOf(value);
		ThrowException.ifNull(index, "getModel().getIndexOf(value) == null");
		
		PSelection sel = getSelection();
		if (sel == null) {
			return;
		}
		if (sel.isSelected(index)) {
			return;
		}
		getSelection().clearSelection();
		getSelection().addSelection(index);
	}
	
	public default void setSelected(PModelIndex index) {
		ThrowException.ifNull(index, "index == null");
		if (getSelection() != null) {
			getSelection().clearSelection();
			getSelection().addSelection(index);
		}
	}
	
	public default List<PModelIndex> getAllSelectedIndices() {
		if (getSelection() == null) {
			return Collections.emptyList();
		}
		return getSelection().getAllSelected();
	}
	
	public default PModelIndex getLastSelectedIndex() {
		if (getSelection() == null) {
			return null;
		}
		return getSelection().getLastSelected();
	}
	
	public default Object getLastSelectedContent() {
		if (getSelection() == null || getModel() == null) {
			return null;
		}
		PModelIndex index = getSelection().getLastSelected();
		if (index == null) {
			return null;
		}
		return getModel().get(index);
	}
	
	public default Object getContentAt(int x, int y) {
		PModel model = getModel();
		if (model == null) {
			return null;
		}
		PModelIndex index = getIndexAt(x, y);
		if (index == null) {
			return null;
		}
		return model.get(index);
	}
	
	public default List<Object> getAllSelectedContent() {
		PSelection selection = getSelection();
		List<PModelIndex> indices = selection.getAllSelected();
		if (indices.isEmpty()) {
			return Collections.emptyList();
		}
		PModel model = getModel();
		if (indices.size() == 1) {
			PModelIndex index = indices.get(0);
			Object element = model.get(index);
			return Collections.singletonList(element);
		}
		List<Object> result = new ArrayList<>(indices.size());
		for (PModelIndex index : indices) {
			result.add(model.get(index));
		}
		return result;
	}
	
	@Override
	public default boolean isStrongFocusOwner() {
		return true;
	}
	
	public void addObs(PModelObs obs);
	
	public void removeObs(PModelObs obs);
	
	public void addObs(PSelectionObs obs);
	
	public void removeObs(PSelectionObs obs);
	
}