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
		@Override
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
	
	@Override
	public int getSize() {
		return singleElem == null ? 0 : 1;
	}
	
	@Override
	public Iterator<PModelIndex> iterator() {
		return new Iterator<PModelIndex>() {
			
			protected boolean hasNext = getSingleElement() != null;
			
			@Override
			public boolean hasNext() {
				return hasNext;
			}
			@Override
			public PModelIndex next() {
				if (!hasNext) {
					throw new NoSuchElementException();
				}
				hasNext = false;
				return SINGLETON_INDEX;
			}
		};
	}
	
	@Override
	public boolean canSet(PModelIndex index, Object content) {
		return index == SINGLETON_INDEX;
	}
	
	@Override
	public void set(PModelIndex index, Object content) {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		
		Object oldContent = singleElem;
		singleElem = content;
		if (oldContent != content) {
			if (oldContent == null) {
				fireAddEvent(index, content);
			} else if (content == null) {
				fireRemoveEvent(index, oldContent);
			} else if (!oldContent.equals(content)) {
				fireChangeEvent(index, oldContent);
			}
		}
	}
	
	@Override
	public Object get(PModelIndex index) throws WrongIndexType, NullPointerException, IllegalIndex {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		return getSingleElement();
	}
	
	@Override
	public boolean contains(PModelIndex index) throws WrongIndexType, NullPointerException {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		return getSingleElement() != null;
	}
	
	@Override
	public PModelIndex getIndexOf(Object content) {
		if (content != null && content.equals(getSingleElement())) {
			return SINGLETON_INDEX;
		}
		return null;
	}
	
	@Override
	public boolean canAdd(PModelIndex index, Object content) throws WrongIndexType, NullPointerException {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNull(content, "content == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		return getSingleElement() == null;
	}
	
	@Override
	public void add(PModelIndex index, Object content) throws WrongIndexType, NullPointerException, AddImpossible {
		ThrowException.ifFalse(canAdd(index, content), "canAdd(index, content) == false");
		set(index, content);
	}
	
	@Override
	public boolean canRemove(PModelIndex index) throws WrongIndexType, NullPointerException {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNotEqual(index, SINGLETON_INDEX, "index != SingletonPModel.SINGLETON_INDEX");
		return getSingleElement() != null;
	}
	
	@Override
	public void remove(PModelIndex index) throws WrongIndexType, NullPointerException, RemoveImpossible {
		ThrowException.ifFalse(canRemove(index), "canRemove(index) == false");
		set(index, null);
	}
	
}