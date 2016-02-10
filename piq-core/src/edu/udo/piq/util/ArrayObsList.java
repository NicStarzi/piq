package edu.udo.piq.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An implementation of {@link ObserverList} that uses an 
 * array to store observers internally.<br>
 * This implementation tries to be much more efficient as 
 * the {@link CoWALObserverList} implementation.<br>
 * 
 * @param <E>	the type of the observers
 * 
 * @author NicStarzi
 */
public class ArrayObsList<E> implements ObserverList<E> {
	
	/**
	 * The initial capacity of the observer list as well as the 
	 * increment value when resizing.<br>
	 */
	protected static final int BUFFER_CAPACITY_INITIAL = 5;
	protected static final int BUFFER_CAPACITY_INCREMENT = 5;
	protected static final double BUFFER_CAPACITY_FACOTR = 1;
	
	/**
	 * Array used to store all observers. Lazily initialized by the 
	 * {@link #resize()} method as needed.<br>
	 */
	private Object[] arr = null;
	/**
	 * Array used to store time-stamps for each observer. Lazily 
	 * initialized by the {@link #resize()} method as needed.<br>
	 */
	private long[] modTimeStamp = null;
	/**
	 * The number of non-null observer instances in this list.<br>
	 */
	private int size = 0;
	/**
	 * The index where the last element was added to.<br>
	 */
	private int lastAdd = -1;
	/**
	 * Counts up by one each time an element is added to this 
	 * {@link ObserverList}. This is used for the time-stamp of 
	 * observers when added to the array.
	 */
	private long modCount = Long.MIN_VALUE;
	/**
	 * Counts how many messages are currently being sent to all observers.<br>
	 * This is used to check whether an array-copy is needed when removing 
	 * an observer.<br>
	 */
	private int msgCount = 0;
	
	public void add(E obs) {
		ThrowException.ifNull(obs, "obs == null");
		if (arr == null || arr.length == size) {
			resize();
		}
		for (int i = lastAdd + 1; i < arr.length; i++) {
			if (arr[i] == null) {
				add(i, obs);
				return;
			}
		}
		for (int i = 0; i < lastAdd; i++) {
			if (arr[i] == null) {
				add(i, obs);
				return;
			}
		}
		throw new IllegalStateException("add failed");
	}
	
	/**
	 * Adds the observer to the array, generates a time-stamp, increments 
	 * the {@link #modCount} and {@link #size} and sets the {@link #lastAdd} 
	 * index.<br>
	 * @param index		index into the array, array must be null at index
	 * @param obs		the observer to add
	 */
	private void add(int index, E obs) {
		arr[index] = obs;
		lastAdd = index;
		modTimeStamp[index] = modCount;
		incrementModCount();
		size++;
	}
	
	/**
	 * Increments the {@link #modCount} by one. If the value overflows all 
	 * time-stamps are reset to the minimum and the {@link #modCount} is set 
	 * to {@link Long#MIN_VALUE}.<br>
	 * This method is called from {@link #add(int, Object)}.<br>
	 */
	private void incrementModCount() {
		if (modCount == Long.MAX_VALUE) {
			modCount = Long.MIN_VALUE;
			for (int i = 0; i < modTimeStamp.length; i++) {
				modTimeStamp[i] = Long.MIN_VALUE;
			}
		} else {
			modCount += 1;
		}
	}
	
	public void remove(E obs) {
		ThrowException.ifNull(obs, "obs == null");
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				if (obs.equals(arr[i])) {
					remove(i);
					return;
				}
			}
		}
	}
	
	/**
	 * Resets the {@link #modCount} at index and decrements the {@link #size}.<br>
	 * Creates a copy of the backing array and clears the value of the copy at 
	 * index to null. The copy is needed to keep all ongoing messages to notify the recently 
	 * removed observer.<br>
	 * The {@link #lastAdd} index will be set to index - 1 so that the next 
	 * observer that is added will fill the gap that was opened when this 
	 * observer was removed.<br>
	 * @param index
	 */
	private void remove(int index) {
		if (msgCount > 0) {
			arr = Arrays.copyOf(arr, arr.length);
		}
		lastAdd = index - 1;
		arr[index] = null;
		modTimeStamp[index] = Long.MIN_VALUE;
		size--;
	}
	
	public int getSize() {
		return size;
	}
	
	/**
	 * If the array has not yet been initialized it will be initialized as an 
	 * empty array of size {@link #BUFFER_CAPACITY_INITIAL}.<br>
	 * If the array was already initialized it will be resized to:<br><br><code> 
	 * array.length * {@link #BUFFER_CAPACITY_FACOTR} + {@link #BUFFER_CAPACITY_INCREMENT}
	 * </code><br><br>The time-stamp array will always be sized to match the array.<br>
	 */
	private void resize() {
		if (arr == null) {
			arr = new Object[BUFFER_CAPACITY_INITIAL];
			modTimeStamp = new long[arr.length];
		} else {
			int size = (int) (arr.length * BUFFER_CAPACITY_FACOTR) + BUFFER_CAPACITY_INCREMENT;
			arr = Arrays.copyOf(arr, size);
			modTimeStamp = Arrays.copyOf(modTimeStamp, size);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void fireEvent(Message<E> msg) {
		if (arr == null || isEmpty()) {
			return;
		}
		final Object[] arr = this.arr;
		final long[] modTimeStamp = this.modTimeStamp;
		final long modCountNow = modCount;
		msgCount++;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] != null && modTimeStamp[i] <= modCountNow) {
				try {
					msg.notifyObs((E) arr[i]);
				} catch (Exception e) {
					if (PGuiUtil.getGlobalExceptionHandler() != null) {
						PGuiUtil.getGlobalExceptionHandler().onException(e);
					} else {
						e.printStackTrace();
					}
				}
			}
		}
		msgCount--;
	}
	
	public Iterator<E> iterator() {
		if (isEmpty()) {
			List<E> emptyList = Collections.emptyList();
			return emptyList.iterator();
		}
		return new ArrayObsListIterator<>(this);
	}
	
	private static class ArrayObsListIterator<E> implements Iterator<E> {
		
		private final ArrayObsList<E>	obsList;
		private final Object[]			arr;
		private final long[]			modTimeStamp;
		private final long				timeStamp;
		private int						pos;
		
		public ArrayObsListIterator(ArrayObsList<E> list) {
			obsList = list;
			arr = obsList.arr;
			modTimeStamp = obsList.modTimeStamp;
			timeStamp = obsList.modCount;
			obsList.msgCount++;
			pos = 0;
		}
		
		public boolean hasNext() {
			for (; pos < arr.length; pos++) {
				if (arr[pos] != null && modTimeStamp[pos] < timeStamp) {
					return true;
				}
			}
			return false;
		}
		
		@SuppressWarnings("unchecked")
		public E next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			E next = (E) arr[pos++];
			if (!hasNext()) {
				obsList.msgCount--;
			}
			return next;
		}
		
	}
	
}