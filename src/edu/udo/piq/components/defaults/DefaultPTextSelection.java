package edu.udo.piq.components.defaults;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import edu.udo.piq.components.PTextModel;
import edu.udo.piq.components.PTextSelection;
import edu.udo.piq.tools.AbstractPTextSelection;

public class DefaultPTextSelection extends AbstractPTextSelection implements PTextSelection {
	
	private int from;
	private int to;
	
	public void addSelection(Integer index) {
		int value = index.intValue();
		
		PTextModel model = getModel();
		String text = model.getText().toString();
		int maxIndex = text.length();
		
		if (value < 0) {
			value = 0;
		} else if (value > maxIndex) {
			value = maxIndex;
		}
		
		if (!isSelected(Integer.valueOf(value))) {
			if (value < from) {
				for (int i = value; i < from; i++) {
					fireSelectionAddedEvent(i);
				}
				from = value;
			} else if (value > to) {
				for (int i = value; i > to; i--) {
					fireSelectionAddedEvent(i);
				}
				to = value;
			} else {
				throw new IllegalStateException("from="+from+", to="+to+", value="+value);
			}
		}
	}
	
	public void removeSelection(Integer index) {
		if (isSelected(index)) {
			int newTo = index.intValue() - 1;
			for (int i = newTo; i < to; i++) {
				fireSelectionRemovedEvent(i);
			}
			to = newTo;
		}
	}
	
	public void clearSelection() {
		int oldFrom = from;
		int oldTo = to;
		from = Integer.MAX_VALUE;
		to = Integer.MIN_VALUE;
		for (int i = oldFrom; i < oldTo; i++) {
			fireSelectionRemovedEvent(i);
		}
	}
	
	public Set<Integer> getSelection() {
		return new RangeSet(from, to);
	}
	
	public boolean isSelected(Integer index) {
		int value = index.intValue();
		return value >= from && value <= to;
	}
	
	public static class RangeSet extends AbstractSet<Integer> implements Set<Integer> {
		
		protected final int from;
		protected final int to;
		
		public RangeSet(int from, int to) {
			this.from = from;
			this.to = to;
		}
		
		public int size() {
			return to - from;
		}
		
		public Iterator<Integer> iterator() {
			return new RangeIterator(this);
		}
		
	}
	
	public static class RangeIterator implements Iterator<Integer> {
		
		protected final RangeSet set;
		protected int pos;
		
		public RangeIterator(RangeSet set) {
			this.set = set;
			pos = set.from;
		}
		
		public boolean hasNext() {
			return pos < set.to;
		}
		
		public Integer next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			Integer value = Integer.valueOf(pos);
			pos += 1;
			return value;
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
}