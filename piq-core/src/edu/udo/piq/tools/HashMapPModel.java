package edu.udo.piq.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.IllegalIndex;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.RemoveImpossible;
import edu.udo.piq.components.collections.WrongIndexType;
import edu.udo.piq.util.ThrowException;

public class HashMapPModel extends AbstractPModel {
	
	protected final Map<PHashIndex, Object> hashMap = new HashMap<>();
	
	public void set(PModelIndex index, Object content) {
		add(index, content);
	}
	
	public Object get(PModelIndex index) throws WrongIndexType, NullPointerException, IllegalIndex {
		return hashMap.get(index);
	}
	
	public boolean contains(PModelIndex index) throws WrongIndexType, NullPointerException {
		return hashMap.containsKey(index);
	}
	
	public PModelIndex getIndexOf(Object content) {
		ThrowException.ifNull(content, "content == null");
		for (Entry<PHashIndex, Object> e : hashMap.entrySet()) {
			if (e.getValue().equals(content)) {
				return e.getKey();
			}
		}
		return null;
	}
	
	public boolean canAdd(PModelIndex index, Object content) throws WrongIndexType, NullPointerException {
		return index != null && index instanceof PHashIndex;
	}
	
	public void add(PModelIndex index, Object content) throws WrongIndexType, NullPointerException, AddImpossible {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifNull(content, "content == null");
		ThrowException.ifFalse(canAdd(index, content), "canAdd(index, content) == false");
		Object oldContent = hashMap.put((PHashIndex) index, content);
		if (oldContent == null) {
			fireAddEvent(index, content);
		} else {
			fireChangeEvent(index, content);
		}
	}
	
	public boolean canRemove(PModelIndex index) throws WrongIndexType, NullPointerException {
		return index != null && index instanceof PHashIndex && contains(index);
	}
	
	public void remove(PModelIndex index) throws WrongIndexType, NullPointerException, RemoveImpossible {
		ThrowException.ifNull(index, "index == null");
		ThrowException.ifFalse(canRemove(index), "canRemove(index) == false");
		Object content = hashMap.remove(index);
		fireRemoveEvent(index, content);
	}
	
	public Iterator<PModelIndex> iterator() {
		Iterator<PHashIndex> hashIter = hashMap.keySet().iterator();
		return new Iterator<PModelIndex>() {
			public PModelIndex next() {
				return hashIter.next();
			}
			public boolean hasNext() {
				return hashIter.hasNext();
			}
		};
	}
	
	public static class PHashIndex implements PModelIndex {
		
		protected final Object key;
		
		public PHashIndex(Object key) {
			this.key = key;
		}
		
		public int hashCode() {
			return key.hashCode();
		}
		
		public boolean equals(Object other) {
			if (other == null || !(other instanceof PHashIndex)) {
				return false;
			}
			return ((PHashIndex) other).key.equals(key);
		}
		
	}
	
}