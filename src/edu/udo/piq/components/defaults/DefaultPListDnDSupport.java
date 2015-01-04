package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDTransfer;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.components.PList;
import edu.udo.piq.components.PListModel;
import edu.udo.piq.components.PListSelection;
import edu.udo.piq.tools.ImmutablePDnDTransfer;

public class DefaultPListDnDSupport implements PDnDSupport {
	
	@SuppressWarnings("rawtypes")
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
			// Index might be -1 but in that case the model will return false with canAddElement
			int index = list.getIndexAt(x, y);
			PListModel model = list.getModel();
			if (model == null) {
				return false;
			}
			
			Object data = transfer.getElement();
			// We might get a transfer from a different kind of component.
			// We assume that a PList does not hold Collections as elements which might not always be true
			// In case a user wants to store Collections inside a PList a custom PDnDSupport is required
			if (data instanceof Collection) {
				for (Object element : (Collection) data) {
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
	
	@SuppressWarnings("rawtypes")
	public void drop(PComponent target, PDnDTransfer transfer, int x, int y) {
		// Throws NullPointerExceptions as well
		if (!canDrop(target, transfer, x, y)) {
			throw new IllegalArgumentException();
		}
		try {
			// We know that this is a PList since the canDrop method returned true
			PList list = (PList) target;
			// We know this index is valid since the canDrop method returned true
			int index = list.getIndexAt(x, y);
			// We know the model is not null since the canDrop method returned true
			PListModel model = list.getModel();
			
			Object data = transfer.getElement();
			// We might get a transfer from a different kind of component.
			// We assume that a PList does not hold Collections as elements which might not always be true
			// In case a user wants to store Collections inside a PList a custom PDnDSupport is required
			if (data instanceof Collection) {
				for (Object element : (Collection) data) {
					model.addElement(index++, element);
				}
			} else {
				model.addElement(index, data);
			}
		} catch (Exception e) {
			// Just in case
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
		try {
			PList list = (PList) source;
			PListSelection selection = list.getSelection();
			// We drag what is selected. No selection => no drag
			if (selection == null) {
				return false;
			}
			PListModel model = list.getModel();
			if (model == null) {
				return false;
			}
			for (Integer index : selection.getSelection()) {
				if (!model.canRemoveElement(index.intValue())) {
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
	
	public PDnDTransfer startDrag(PComponent source, int x, int y) {
		// Throws NullPointerExceptions as well
		if (!canDrag(source, x, y)) {
			throw new IllegalArgumentException();
		}
		try {
			// We know that this is a PList since the canDrag method returned true
			PList list = (PList) source;
			// We know the selection is not null since the canDrag method returned true
			PListSelection selection = list.getSelection();
			
			Set<Integer> selectedIndices = selection.getSelection();
			List<Object> data = new ArrayList<>(selectedIndices.size());
			
			// We know the model is not null since the canDrag method returned true
			PListModel model = list.getModel();
			for (Integer index : selectedIndices) {
				data.add(model.getElement(index.intValue()));
			}
			
			return new ImmutablePDnDTransfer(source, x, y, data, null);
		} catch (Exception e) {
			// Just in case
			throw new IllegalArgumentException(e);
		}
	}
	
	public void finishDrag(PComponent source, PDnDTransfer transfer) {
		if (source == null) {
			throw new NullPointerException();
		}
		try {
			@SuppressWarnings("unchecked")
			// We know that this is a List since we created it in the startDrag method
			List<Object> elementList = (List<Object>) transfer.getElement();
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
			throw new IllegalArgumentException(e);
		}
	}
	
	public void abortDrag(PComponent source, PDnDTransfer data) {
		if (source == null) {
			throw new NullPointerException();
		}
		// do nothing
	}
	
}