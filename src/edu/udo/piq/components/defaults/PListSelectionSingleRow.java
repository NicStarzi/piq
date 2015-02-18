package edu.udo.piq.components.defaults;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.components.PListSelection;
import edu.udo.piq.tools.AbstractPListSelection;

public class PListSelectionSingleRow extends AbstractPListSelection implements PListSelection {
	
	private final List<Object> oneElemList = Arrays.asList(new Object[] {null});
//	private final Set<Object> selection = new HashSet<>();
	
	public void addSelection(Object element) {
		clearSelection();
		oneElemList.set(0, element);
		fireSelectionAddedEvent(element);
	}
	
	public void removeSelection(Object element) {
		if (isSelected(element)) {
			clearSelection();
		}
	}
	
	public void clearSelection() {
		Object elem = getElement();
		if (elem != null) {
			oneElemList.set(0, null);
			fireSelectionRemovedEvent(elem);
		}
	}
	
	public List<Object> getSelection() {
		return Collections.unmodifiableList(oneElemList);
	}
	
	public boolean isSelected(Object element) {
		return oneElemList.get(0) == element;
	}
	
	protected Object getElement() {
		return oneElemList.get(0);
	}
	
//	public void addSelection(Object element) {
//		switch (getSelectionMode()) {
//		case ARBITRARY_ROWS:
//			if (selection.add(element)) {
//				fireSelectionAddedEvent(element);
//			}
//			break;
//		case CONTIGUOUS_ROWS:
//			if (selection.isEmpty()) {
//				selection.add(element);
//				fireSelectionAddedEvent(element);
//			}
//			
//			PListModel model = getModel();
//			List<Integer> selectedIndices = getSelectedIndices();
//			int max = Collections.max(selectedIndices);
//			int min = Collections.min(selectedIndices);
//			int index = model.getIndexOfElement(element);
//			if (index == max + 1 || index == min - 1) {
//				selection.add(element);
//				fireSelectionAddedEvent(element);
//			}
//			break;
//		case SINGLE_ROW:
//			if (!selection.contains(element)) {
//				clearSelection();
//				selection.add(element);
//				fireSelectionAddedEvent(element);
//			}
//			break;
//		}
//	}
	
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
	
//	public void clearSelection() {
//		if (!selection.isEmpty()) {
//			List<Object> copy = new ArrayList<>(selection);
//			selection.clear();
//			for (Object element : copy) {
//				fireSelectionRemovedEvent(element);
//			}
//		}
//	}
	
//	public List<Object> getSelection() {
//		return null;
//		return Collections.unmodifiableSet(selection);
//	}
	
//	public boolean isSelected(Object element) {
//		return selection.contains(element);
//	}
	
}