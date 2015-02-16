package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PDnDTransfer;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.components.PTree;
import edu.udo.piq.components.PTreeCellComponent;
import edu.udo.piq.components.PTreeModel;
import edu.udo.piq.components.PTreePosition;
import edu.udo.piq.components.PTreeSelection;
import edu.udo.piq.tools.ImmutablePDnDTransfer;

public class DefaultPTreeDnDSupport implements PDnDSupport {
	
	private PTreeCellComponent highlightedCellComp;
	
	public boolean canDrop(PComponent target, PDnDTransfer transfer, int x, int y) {
		if (target == null || transfer == null) {
			throw new NullPointerException();
		}
		// In case somebody uses this PDnDSupport for any other kind of PComponent
		if (!(target instanceof PTree)) {
			return false;
		}
		try {
			PTree tree = (PTree) target;
			// Model might be null
			PTreeModel model = tree.getModel();
			if (model == null) {
				return false;
			}
			// Position component might be null
			PTreePosition pos = tree.getPositionAt(x, y);
			if (pos == null) {
				return false;
			}
			Object parent = pos.getParent();
			int index = pos.getIndex();
			
			Object data = transfer.getData();
			// We might get a transfer from a different kind of component.
			// We assume that a PTree does not hold Collections as elements which might not always be true
			// In case a user wants to store Collections inside a PTree a custom PDnDSupport is required
			if (data instanceof Collection) {
				for (Object node : (Collection<?>) data) {
					// We use the same index for each element but we are going to add them to different 
					// indices later. This might become an issue with non-standard PTreeModels.
					// For the moment the issue is simply ignored and a standard model is assumed.
					if (!model.canAddChild(parent, node, index)) {
						return false;
					}
				}
				return true;
			} else {
				return model.canAddChild(parent, data, index);
			}
		} catch (Exception e) {
			// Just in case
			e.printStackTrace();
			return false;
		}
	}
	
	public void drop(PComponent target, PDnDTransfer transfer, int x, int y) {
		// canDrop(...) throws NullPointerExceptions if needed as well
		if (!canDrop(target, transfer, x, y)) {
			throw new IllegalArgumentException();
		}
		try {
			// We know that this is a PTree since the canDrop method returned true
			PTree tree = (PTree) target;
			// We know the model is not null since the canDrop method returned true
			PTreeModel model = tree.getModel();
			
			// We know this is not null since canDrop() returned true
			PTreePosition pos = tree.getPositionAt(x, y);
			Object parent = pos.getParent();
			int index = pos.getIndex();
			
			Object data = transfer.getData();
			// We might get a transfer from a different kind of component.
			// We assume that a PTree does not hold Collections as elements which might not always be true
			// In case a user wants to store Collections inside a PTree a custom PDnDSupport is required
			if (data instanceof Collection) {
				for (Object node : (Collection<?>) data) {
					model.addChild(parent, node, index);
				}
			} else {
				model.addChild(parent, data, index);
			}
		} catch (Exception e) {
			// Just in case
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
	public boolean canDrag(PComponent source, int x, int y) {
		if (source == null) {
			throw new NullPointerException();
		}
		// In case somebody uses this PDnDSupport for any other kind of PComponent
		if (!(source instanceof PTree)) {
			return false;
		}
		// If the root does not support drag and drop or if there is no root to begin with
		PDnDManager dndMngr = source.getDragAndDropManager();
		if (dndMngr == null || !dndMngr.canDrag()) {
			return false;
		}
		try {
			PTree tree = (PTree) source;
			PTreeSelection selection = tree.getSelection();
			// We drag what is selected. No selection => no drag
			if (selection == null) {
				return false;
			}
			PTreeModel model = tree.getModel();
			// Can happen
			if (model == null) {
				return false;
			}
			for (Object node : selection.getSelection()) {
				Object parent = model.getParentOf(node);
				int index = model.getChildIndex(parent, node);
				if (!model.canRemoveChild(parent, index)) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			// Just in case
			e.printStackTrace();
			return false;
		}
	}
	
	public void startDrag(PComponent source, int x, int y) {
		// canDrag(...) throws NullPointerExceptions if needed as well
		if (!canDrag(source, x, y)) {
			throw new IllegalArgumentException();
		}
		try {
			// We know that this is a PTree since the canDrag method returned true
			PTree tree = (PTree) source;
			// We know the selection is not null since the canDrag method returned true
			PTreeSelection selection = tree.getSelection();
			
			Set<Object> selectedElements = selection.getSelection();
			List<Object> data = new ArrayList<>(selectedElements);
			
			PDnDTransfer transfer = new ImmutablePDnDTransfer(source, x, y, data, 
					createVisibleRepresentation(data));
			
			source.getDragAndDropManager().startDrag(transfer);
		} catch (Exception e) {
			// Just in case
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
	public void finishDrag(PComponent source, PComponent target, PDnDTransfer transfer) {
		if (source == null || target == null || transfer == null) {
			throw new NullPointerException();
		}
		try {
			// We know that this is a List since we created it in the startDrag method
			List<?> elementList = (List<?>) transfer.getData();
			// We know that this is a PTree since the canDrag method returned true
			PTree tree = (PTree) source;
			// We know the model is not null since the canDrag method returned true
			PTreeModel model = tree.getModel();
			
			for (Object node : elementList) {
				Object parent = model.getParentOf(node);
				// node might already have been removed parent was removed earlier
				if (parent != null) {
					int index = model.getChildIndex(parent, node);
					model.removeChild(parent, index);
				}
			}
		} catch (Exception e) {
			// Just in case
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
	public void abortDrag(PComponent source, PDnDTransfer transfer) {
		if (source == null) {
			throw new NullPointerException();
		}
		// do nothing
	}
	
	protected PComponent createVisibleRepresentation(List<Object> data) {
		PPicture pic = new PPicture();
		pic.getModel().setImagePath("DragAndDrop.png");
		pic.setStretchToSize(true);
		pic.setElusive(true);
		return pic;
	}
	
	public void showDropLocation(PComponent source, PDnDTransfer transfer, int x, int y) {
		if (source == null || transfer == null) {
			throw new NullPointerException();
		}
		// In case somebody uses this PDnDSupport for any other kind of PComponent
		if (!(source instanceof PTree)) {
			return;
		}
		try {
			if (highlightedCellComp != null) {
				highlightedCellComp.setDropHighlighted(false);
				highlightedCellComp = null;
			}
			
			PTree tree = (PTree) source;
			
			highlightedCellComp = tree.getCellComponentAt(x, y);
//			tree.setDropHighlighted(highlightedCellComp == null);
			if (highlightedCellComp != null) {
				highlightedCellComp.setDropHighlighted(true);
			}
		} catch (Exception e) {
			// Just in case
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
	public void hideDropLocation(PComponent source, PDnDTransfer transfer, int x, int y) {
		if (source == null || transfer == null) {
			throw new NullPointerException();
		}
		// In case somebody uses this PDnDSupport for any other kind of PComponent
		if (!(source instanceof PTree)) {
			return;
		}
		try {
//			PTree tree = (PTree) source;
//			tree.setDropHighlighted(false);
			if (highlightedCellComp != null) {
				highlightedCellComp.setDropHighlighted(false);
				highlightedCellComp = null;
			}
		} catch (Exception e) {
			// Just in case
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}
	
}