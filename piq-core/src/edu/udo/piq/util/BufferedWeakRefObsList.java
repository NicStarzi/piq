package edu.udo.piq.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BufferedWeakRefObsList<E> implements ObserverList<E> {
	
	/**
	 * Contains the actual elements of this ObserverList
	 */
	private final List<WeakReference<E>> list = new ArrayList<>(3);
	private final ReferenceQueue<E> refQueue = new ReferenceQueue<>();
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
			buffer.add(() -> list.add(new WeakReference<>(element, refQueue)));
		} else {
			list.add(new WeakReference<>(element, refQueue));
		}
	}
	
	@Override
	public void remove(E element) {
		if (forEachCount > 0) {
			if (buffer == null) {
				buffer = new ArrayList<>(2);
			}
			// Use a lambda for the write operation and add it to the buffer
			buffer.add(() -> removeReferenceOf(element));
		} else {
			removeReferenceOf(element);
		}
	}
	
	private void removeReferenceOf(E elem) {
		for (int i = 0; i < list.size(); i++) {
			if (Objects.equals(list.get(i).get(), elem)) {
				list.remove(i);
				break;
			}
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
		for (Reference<?> ref = refQueue.poll();
			ref != null;
			ref = refQueue.poll()) 
		{
			new Throwable("Lapsed-Observer detected. size="+list.size()).printStackTrace();
//			list.remove(ref);
		}
		
		forEachCount++;
		List<WeakReference<E>> tempList = list;
		for (WeakReference<E> ref : tempList) {
			E element = ref.get();
			if (element != null) {
				msg.notifyObs(element);
			}
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