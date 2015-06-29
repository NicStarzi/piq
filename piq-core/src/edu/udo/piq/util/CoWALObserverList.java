package edu.udo.piq.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An implementation of {@link ObserverList} that uses a 
 * {@link CopyOnWriteArrayList} internally.<br>
 * 
 * @author NicStarzi
 */
public class CoWALObserverList<E> implements ObserverList<E> {
	
	/**
	 * Lazily initialized instance of {@link CopyOnWriteArrayList}.<br>
	 * @see #createLazy()
	 */
	private List<E> internalList = null;
	
	public void add(E obs) {
		createLazy();
		internalList.add(obs);
	}
	
	public void remove(E obs) {
		if (internalList != null) {
			internalList.remove(obs);
			if (internalList.isEmpty()) {
				internalList = null;
			}
		}
	}
	
	public int getSize() {
		if (internalList != null) {
			return internalList.size();
		}
		return 0;
	}
	
	/**
	 * If {@link #internalList} is null this method will creates a new 
	 * {@link CopyOnWriteArrayList} and assigns it to {@link #internalList}.<br> 
	 */
	private void createLazy() {
		if (internalList == null) {
			internalList = new CopyOnWriteArrayList<>();
		}
	}
	
	public void sendNotify(Message<E> msg) {
		for (int i = 0; i < internalList.size(); i++) {
			E obs = internalList.get(i);
			msg.notifyObs(obs);
		}
	}
	
}