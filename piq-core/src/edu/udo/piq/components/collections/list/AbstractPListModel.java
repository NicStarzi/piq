package edu.udo.piq.components.collections.list;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.IllegalIndex;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.RemoveImpossible;
import edu.udo.piq.components.collections.WrongIndexType;
import edu.udo.piq.tools.AbstractPModel;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPListModel extends AbstractPModel implements PListModel {
	
	public boolean canSet(PModelIndex index, Object content) throws WrongIndexType, NullPointerException {
		PListIndex listIdx = (PListIndex) index;
		return canSet(listIdx.getIndexValue(), content);
	}
	
	public void set(PModelIndex index, Object content) {
		PListIndex listIdx = (PListIndex) index;
		int listIdxInt = listIdx.getIndexValue();
		Object oldContent = get(listIdxInt);
		set(listIdxInt, content);
		fireChangeEvent(listIdx, oldContent);
	}
	
	public boolean canAdd(PModelIndex index, Object content) throws WrongIndexType, NullPointerException {
		PListIndex listIdx = (PListIndex) index;
		return canAdd(listIdx.getIndexValue(), content);
	}
	
	public void add(PModelIndex index, Object content) throws WrongIndexType, NullPointerException, AddImpossible {
		PListIndex listIdx = (PListIndex) index;
		add(listIdx.getIndexValue(), content);
	}
	
	public boolean canRemove(PModelIndex index) throws WrongIndexType, NullPointerException {
		PListIndex listIdx = (PListIndex) index;
		return canRemove(listIdx.getIndexValue());
	}
	
	public void remove(PModelIndex index) throws WrongIndexType, NullPointerException, RemoveImpossible {
		PListIndex listIdx = (PListIndex) index;
		remove(listIdx.getIndexValue());
	}
	
	public Object get(PModelIndex index) throws WrongIndexType, NullPointerException, IllegalIndex {
		PListIndex listIdx = (PListIndex) index;
		return get(listIdx.getIndexValue());
	}
	
	public boolean contains(PModelIndex index) throws WrongIndexType, NullPointerException {
		PListIndex listIdx = (PListIndex) index;
		return contains(listIdx.getIndexValue());
	}
	
	public boolean contains(int index) {
		return index >= 0 && index < getSize();
	}
	
	public PModelIndex getIndexOf(Object content) {
		ThrowException.ifNull(content, "content == null");
		for (int i = 0; i < getSize(); i++) {
			if (content.equals(get(i))) {
				return new PListIndex(i);
			}
		}
		return null;
	}
	
	public Iterator<PModelIndex> iterator() {
		return new PListModelIterator(this);
	}
	
	protected boolean isIndexWithinBounds(int indexVal, int sizeOffset) {
		return indexVal >= 0 && indexVal < getSize() + sizeOffset;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		for (int i = 0; i < getSize(); i++) {
			sb.append(get(i));
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("]");
		return sb.toString();
	}
	
	public static class PListModelIterator implements Iterator<PModelIndex> {
		
		protected final PListModel mdl;
		protected int idx;
		
		public PListModelIterator(PListModel model) {
			mdl = model;
		}
		
		public boolean hasNext() {
			return idx < mdl.getSize();
		}
		
		public PListIndex next() {
			if (!hasNext()) {
				throw new NoSuchElementException("index == "+idx);
			}
			return new PListIndex(idx++);
		}
		
	}
}