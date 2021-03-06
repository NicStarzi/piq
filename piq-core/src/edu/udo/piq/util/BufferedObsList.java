package edu.udo.piq.util;

import java.util.ArrayList;
import java.util.List;

public class BufferedObsList<E> implements ObserverList<E> {
	
	/**
	 * Contains the actual elements of this ObserverList
	 */
	private final List<E> list = new ArrayList<>(3);
	/**
	 * Buffers all modifications (add / remove operations) as {@link Runnable} instances.
	 * @see #add(Object)
	 * @see #remove(Object)
	 */
	private List<Runnable> buffer = null;
	/**
	 * Counts ongoing iterations.
	 * @see #fireEvent(edu.udo.piq.util.ObserverList.Message)
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
		Throw.ifLess(0, forEachCount, () -> "forEachCount="+forEachCount);
		if (forEachCount == 0 && buffer != null) {
//			forEachCount = 0;
			for (Runnable r : buffer) {
				r.run();
			}
			buffer.clear();
		}
	}
	
}