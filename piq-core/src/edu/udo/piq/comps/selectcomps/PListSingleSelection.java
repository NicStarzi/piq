package edu.udo.piq.comps.selectcomps;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class PListSingleSelection extends AbstractPSelection implements PListSelection {
	
	protected final List<PModelIndex> indices = new SingletonList<PModelIndex>();
	
	public void addSelection(PModelIndex index) {
		if (indices.get(0) == index) {
			return;
		}
		fireRemovedIfNeeded();
		indices.set(0, index);
		fireSelectionAdded(indices.get(0));
	}
	
	public void removeSelection(PModelIndex index) {
		if (indices.get(0) == index) {
			fireSelectionRemoved(indices.get(0));
			indices.set(0, null);
		}
	}
	
	public void clearSelection() {
		if (indices.get(0) != null) {
			PModelIndex index = indices.get(0);
			indices.set(0, null);
			fireSelectionRemoved(index);
		}
	}
	
	public List<PModelIndex> getAllSelected() {
		return Collections.unmodifiableList(indices);
	}
	
	public boolean isSelected(PModelIndex index) {
		return indices.get(0) == index;
	}
	
	protected void fireRemovedIfNeeded() {
		if (indices.get(0) != null) {
			fireSelectionRemoved(indices.get(0));
		}
	}
	
	protected static class SingletonList<K> extends AbstractList<K> {
		
		private K content = null;
		
		public K set(int index, K element) {
			if (index != 0) {
				throw new IndexOutOfBoundsException();
			}
			K oldContent = content;
			content = element;
			return oldContent;
		}
		
		public K get(int index) {
			if (index != 0) {
				throw new IndexOutOfBoundsException();
			}
			return content;
		}
		
		public int size() {
			return content == null ? 0 : 1;
		}
		
	}
	
}