package edu.udo.piq.tools;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.IllegalIndex;
import edu.udo.piq.components.collections.PModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.RemoveImpossible;
import edu.udo.piq.components.collections.WrongIndexType;
import edu.udo.piq.util.ThrowException;

public class SingletonPModel extends AbstractPModel implements PModel {
	
	public static final PModelIndex SINGLETON_INDEX = new PModelIndex() {
		public PModelIndex withOffset(PModelIndex offset) {
			throw new UnsupportedOperationException();
		}
	};
	
	public SingletonPModel() {
		this(null);
	}
	
	public SingletonPModel(Object element) {
		singleElem = element;
	}
	
	protected Object singleElem;
	
	public Object getSingleElement() {
		return singleElem;
	}
	
	public Iterator<PModelIndex> iterator() {
		return new Iterator<PModelIndex>() {
			
			protected boolean hasNext = getSingleElement() != null;
			
			public boolean hasNext() {
				return hasNext;
			}
			public PModelIndex next() {
				if (!hasNext) {
					throw new NoSuchElementException();
				}
				hasNext = false;
				return SINGLETON_INDEX;
			}
		};
	}
	
	public boolean canSet(PModelIndex index, Object content) {
		return index == SINGLETON_INDEX;
	}
	
	public void set(PModelIndex index, Object content) {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		
		Object oldContent = singleElem;
		singleElem = content;
		if (singleElem != content) {
			if (singleElem == null) {
				if (content != null) {
					fireAddEvent(index, content);
				}
			} else if (content == null) {
				fireRemoveEvent(index, oldContent);
			} else if (!singleElem.equals(content)) {
				fireChangeEvent(index, oldContent);
			}
		}
	}
	
	public Object get(PModelIndex index) throws WrongIndexType, NullPointerException, IllegalIndex {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		return getSingleElement();
	}
	
	public boolean contains(PModelIndex index) throws WrongIndexType, NullPointerException {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		return getSingleElement() != null;
	}
	
	public PModelIndex getIndexOf(Object content) {
		if (content != null && content.equals(getSingleElement())) {
			return SINGLETON_INDEX;
		}
		return null;
	}
	
	public boolean canAdd(PModelIndex index, Object content) throws WrongIndexType, NullPointerException {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNull(content, "content == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		return getSingleElement() == null;
	}
	
	public void add(PModelIndex index, Object content) throws WrongIndexType, NullPointerException, AddImpossible {
		ThrowException.ifFalse(canAdd(index, content), "canAdd(index, content) == false");
		set(index, content);
	}
	
	public boolean canRemove(PModelIndex index) throws WrongIndexType, NullPointerException {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		return getSingleElement() != null;
	}
	
	public void remove(PModelIndex index) throws WrongIndexType, NullPointerException, RemoveImpossible {
		ThrowException.ifFalse(canRemove(index), "canRemove(index) == false");
		set(index, null);
	}
	
}