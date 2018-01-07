package edu.udo.piq.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
public class BufferedObsList<E> implements ObserverList<E> {
	
	/**
	 * Contains the actual elements of this ObserverList
	 */
	private final List<E> list = new ArrayList<>(5);
	/**
	 * Buffers all modifications (add / remove operations) as {@link Runnable} instances.
	 * @see #add(Object)
	 * @see #remove(Object)
	 */
	private List<Runnable> buffer = null;
	/**
	 * Counts ongoing iterations.
	 * @see #forEach(Consumer)
	 * @see #forEachTuple(BiConsumer)
	 */
	private int forEachCount = 0;
	
	@Override
	public void add(E element) {
		if (forEachCount > 0) {
			if (buffer == null) {
				buffer = new ArrayList<>(2);
			}
			// Use a lambda for the write operation and add it to the buffer
			buffer.add(() -> list.add(element));
		} else {
			list.add(element);
		}
	}
	
	@Override
	public void remove(E element) {
		if (forEachCount > 0) {
			if (buffer == null) {
				buffer = new ArrayList<>(2);
			}
			// Use a lambda for the write operation and add it to the buffer
			buffer.add(() -> list.remove(element));
		} else {
			list.remove(element);
		}
	}
	
	/**
	 * Returns the current size without paying attention to the buffer.<br>
	 */
	@Override
	public int getSize() {
		return list.size();
	}
	
	@Override
	public void fireEvent(Message<E> msg) {
		if (msg == null) {
			throw new IllegalArgumentException("consumer == null");
		}
		
		forEachCount++;
		List<E> tempList = list;
		for (E element : tempList) {
			msg.notifyObs(element);
		}
		forEachCount--;
		
		// Perform buffered write operations iff this was the last concurrent iteration
		performBufferedWritesIfPossible();
	}
	
	/**
	 * Checks whether there are no more iterations going on. If so,
	 * performs all buffered write operations and clears the buffer.<br>
	 */
	private void performBufferedWritesIfPossible() {
		if (forEachCount <= 0) {
			forEachCount = 0;
			for (Runnable r : buffer) {
				r.run();
			}
			buffer.clear();
		}
	}
	
}