package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.PListModel;
import edu.udo.piq.components.PListSelection;
import edu.udo.piq.tools.AbstractPListSelection;

public class PListSelectionContiguousRows extends AbstractPListSelection implements PListSelection {
	
	private ArrayList<Object> elems = new ArrayList<>();
	
	public void addSelection(Object element) {
		if (elems.isEmpty()) {
			elems.add(element);
			fireSelectionAddedEvent(element);
		} else if (!elems.contains(element)) {
			PListModel model = getModel();
			
			int newIndex = model.getElementIndex(element);
			for (Object elem : elems) {
				int index = model.getElementIndex(elem);
				if (newIndex == index + 1 || newIndex == index - 1) {
					elems.add(element);
					fireSelectionAddedEvent(element);
					break;
				}
			}
		}
	}
	
	public void removeSelection(Object element) {
	}
	
	public void clearSelection() {
		if (!elems.isEmpty()) {
			List<Object> rmvdElems = elems;
			elems = new ArrayList<>(getModel().getElementCount());
			for (Object element : rmvdElems) {
				fireSelectionRemovedEvent(element);
			}
		}
	}
	
	public List<Object> getSelection() {
		return Collections.unmodifiableList(elems);
	}
	
	public boolean isSelected(Object element) {
		return elems.contains(element);
	}
	
	public void setModel(PListModel model) {
		super.setModel(model);
		elems.ensureCapacity(getModel().getElementCount());
	}
	
//	public void removeSelection(Object element) {
//		if (!selection.contains(element)) {
//			return;
//		}
//		switch (getSelectionMode()) {
//		case ARBITRARY_ROWS:
//			if (selection.remove(element)) {
//				fireSelectionRemovedEvent(element);
//			}
//			break;
//		case CONTIGUOUS_ROWS:
//			selection.remove(element);
//			fireSelectionRemovedEvent(element);
//			if (selection.isEmpty()) {
//				return;
//			}
//			
//			List<Integer> indices = getSelectedIndices();
//			Collections.sort(indices);
//			List<Integer> lower = new ArrayList<>(indices.size());
//			List<Integer> upper = new ArrayList<>(indices.size());
//			
//			Integer previous = indices.get(0);
//			lower.add(previous);
//			int i;
//			for (i = 1; i < indices.size(); i++) {
//				Integer current = indices.get(i);
//				if (current.intValue() != previous.intValue() + 1) {
//					break;
//				}
//				lower.add(current);
//				previous = current;
//			}
//			for (; i < indices.size(); i++) {
//				upper.add(indices.get(i));
//			}
//			if (lower.size() + 1 > upper.size()) {
//				removeAll(upper);
//			} else {
//				removeAll(lower);
//			}
//			break;
//		case SINGLE_ROW:
//			clearSelection();
//			break;
//		}
//	}
	
//	private void removeAll(List<Integer> indices) {
//		PListModel model = getModel();
//		for (Integer index : indices) {
//			Object element = model.getElement(index.intValue());
//			selection.remove(element);
//			fireSelectionRemovedEvent(element);
//		}
//	}
	
}