package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDManager;
import edu.udo.piq.PDnDTransfer;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.components.PList;
import edu.udo.piq.components.PListCellComponent;
import edu.udo.piq.components.PListModel;
import edu.udo.piq.components.PListSelection;
import edu.udo.piq.components.PPicture;
import edu.udo.piq.tools.ImmutablePDnDTransfer;

public class DefaultPListDnDSupport implements PDnDSupport {
	
	private PListCellComponent highlightedCellComp;
	
	public boolean canDrop(PComponent target, PDnDTransfer transfer, int x, int y) {
		if (target == null || transfer == null) {
			throw new NullPointerException();
		}
		// In case somebody uses this PDnDSupport for any other kind of PComponent
		if (!(target instanceof PList)) {
			return false;
		}
		try {
			PList list = (PList) target;
			// Model might be null
			PListModel model = list.getModel();
			if (model == null) {
				return false;
			}
			// Index might be -1, in that case use element count of model for index
			int index = list.getIndexAt(x, y);
			if (index == -1) {
				index = model.getElementCount();
			}
			
			Object data = transfer.getData();
			// We might get a transfer from a different kind of component.
			// We assume that a PList does not hold Collections as elements which might not always be true
			// In case a user wants to store Collections inside a PList a custom PDnDSupport is required
			if (data instanceof Collection) {
				for (Object element : (Collection<?>) data) {
					// We use the same index for each element but we are going to add them to different 
					// indices later. This might become an issue with non-standard PListModels.
					// For the moment the issue is simply ignored and a standard model is assumed.
					if (!model.canAddElement(index, element)) {
						return false;
					}
				}
				return true;
			} else {
				return model.canAddElement(index, data);
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
			// We know that this is a PList since the canDrop method returned true
			PList list = (PList) target;
			// We know the model is not null since the canDrop method returned true
			PListModel model = list.getModel();
			// If index is -1 we append at the end of the list
			int index = list.getIndexAt(x, y);
			if (index == -1) {
				index = model.getElementCount();
			}
			
			Object data = transfer.getData();
			// We might get a transfer from a different kind of component.
			// We assume that a PList does not hold Collections as elements which might not always be true
			// In case a user wants to store Collections inside a PList a custom PDnDSupport is required
			if (data instanceof Collection) {
				for (Object element : (Collection<?>) data) {
					model.addElement(index++, element);
				}
			} else {
				model.addElement(index, data);
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
		if (!(source instanceof PList)) {
			return false;
		}
		// If the root does not support drag and drop or if there is no root to begin with
		PDnDManager dndMngr = source.getDragAndDropManager();
		if (dndMngr == null || !dndMngr.canDrag()) {
			return false;
		}
		try {
			PList list = (PList) source;
			PListSelection selection = list.getSelection();
			// We drag what is selected. No selection => no drag
			if (selection == null) {
				return false;
			}
			PListModel model = list.getModel();
			// Can happen
			if (model == null) {
				return false;
			}
			for (Object element : selection.getSelection()) {
				int index = model.getIndexOfElement(element);
				if (!model.canRemoveElement(index)) {
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
			// We know that this is a PList since the canDrag method returned true
			PList list = (PList) source;
			// We know the selection is not null since the canDrag method returned true
			PListSelection selection = list.getSelection();
			
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
			// We know that this is a PList since the canDrag method returned true
			PList list = (PList) source;
			// We know the model is not null since the canDrag method returned true
			PListModel model = list.getModel();
			
			for (Object element : elementList) {
				// We assume each element is only contained once in the list.
				// This is a dangerous assumption that might not be true all the time, 
				// In this case the user is forced to write a custom PDndSupport
				int index = model.getIndexOfElement(element);
				model.removeElement(index);
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
		if (!(source instanceof PList)) {
			return;
		}
		try {
			if (highlightedCellComp != null) {
				highlightedCellComp.setDropHighlighted(false);
				highlightedCellComp = null;
			}
			
			PList list = (PList) source;
			
			highlightedCellComp = list.getCellComponentAt(x, y);
			list.setDropHighlighted(highlightedCellComp == null);
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
		if (!(source instanceof PList)) {
			return;
		}
		try {
			PList list = (PList) source;
			list.setDropHighlighted(false);
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