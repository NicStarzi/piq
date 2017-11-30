package edu.udo.piq.components.defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import edu.udo.piq.components.collections.AddImpossible;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PListModel;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.RemoveImpossible;
import edu.udo.piq.components.collections.WrongIndexType;
import edu.udo.piq.tools.AbstractPListModel;
import edu.udo.piq.util.ThrowException;

public class DefaultPListModel extends AbstractPListModel implements PListModel {
	
	protected final List<Object> list = createListImplementation();
	protected BiPredicate<Integer, Object> testCanSet;
	protected BiPredicate<Integer, Object> testCanAdd;
	protected Predicate<Integer> testCanRemove;
	
	protected List<Object> createListImplementation() {
		return new ArrayList<>();
	}
	
	public DefaultPListModel() {
	}
	
	public DefaultPListModel(Iterable<?> contents) {
		for (Object o : contents) {
			add(getSize(), o);
		}
	}
	
	public DefaultPListModel(Object ... contents) {
		for (Object o : contents) {
			add(getSize(), o);
		}
	}
	
	@Override
	public int getSize() {
		return list.size();
	}
	
	public boolean canSet(int index, Object content) {
		return contains(index)
				&& (testCanSet == null || testCanSet.test(index, content));
	}
	
	public void set(int indexVal, Object content) {
		if (!isIndexWithinBounds(indexVal, 0)) {
			throw new IllegalArgumentException("indexVal == "+indexVal);
		}
		list.set(indexVal, content);
	}
	
	@Override
	public Object get(int indexVal) {
		if (!isIndexWithinBounds(indexVal, 0)) {
			throw new IllegalArgumentException("indexVal == "+indexVal);
		}
		return list.get(indexVal);
	}
	
	@Override
	public boolean contains(int index) {
		return isIndexWithinBounds(index, 0);
	}
	
	@Override
	public PListIndex getIndexOf(Object content) {
		int indexVal = list.indexOf(content);
		if (indexVal == -1) {
			return null;
		}
		return new PListIndex(indexVal);
	}
	
	@Override
	public boolean canAdd(int index, Object content) {
		return content != null && isIndexWithinBounds(index, 1)
				&& (testCanAdd == null || testCanAdd.test(index, content));
	}
	
	@Override
	public void add(PModelIndex index, Object content) {
		if (!canAdd(index, content)) {
			throw new AddImpossible(this, index, content);
		}
		list.add(asListIndex(index), content);
		fireAddEvent(index, content);
	}
	
	@Override
	public void add(int index, Object content) {
		add(new PListIndex(index), content);
	}
	
	@Override
	public boolean canRemove(int index) {
		return contains(index)
				&& (testCanRemove == null || testCanRemove.test(index));
	}
	
	@Override
	public void removeAll(Iterable<PModelIndex> indices) {
		ArrayList<PModelIndex> c = new ArrayList<>();
		indices.forEach(c::add);
		c.sort((i1, i2) -> {
			PListIndex l1 = (PListIndex) i1;
			PListIndex l2 = (PListIndex) i2;
			return Integer.compare(l2.getIndexValue(), l1.getIndexValue());
		});
		for (PModelIndex idx : c) {
			remove(idx);
		}
	}
	
	@Override
	public void removeAll(PModelIndex ... indices) {
		Arrays.sort(indices, (i1, i2) -> {
			PListIndex l1 = (PListIndex) i1;
			PListIndex l2 = (PListIndex) i2;
			return Integer.compare(l2.getIndexValue(), l1.getIndexValue());
		});
		for (PModelIndex idx : indices) {
			remove(idx);
		}
	}
	
	@Override
	public void remove(PModelIndex index) {
		if (!canRemove(index)) {
			throw new RemoveImpossible(this, index);
		}
		Object oldContent = list.remove(asListIndex(index));
		fireRemoveEvent(index, oldContent);
	}
	
	@Override
	public void remove(int index) {
		remove(new PListIndex(index));
	}
	
	protected int asListIndex(PModelIndex index) {
		ThrowException.ifNull(index, "index == null");
		if (index instanceof PListIndex) {
			return ((PListIndex) index).getIndexValue();
		}
		throw new WrongIndexType(index, PListIndex.class);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append(list.toString());
		return sb.toString();
	}
	
}