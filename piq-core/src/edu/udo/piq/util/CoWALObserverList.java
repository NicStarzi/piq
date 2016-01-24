package edu.udo.piq.util;

import java.util.Iterator;
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
		ThrowException.ifNull(obs, "obs == null");
		createLazy();
		internalList.add(obs);
	}
	
	public void remove(E obs) {
		ThrowException.ifNull(obs, "obs == null");
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
			try {
				msg.notifyObs(obs);
			} catch (Exception e) {
				if (PGuiUtil.getGlobalExceptionHandler() != null) {
					PGuiUtil.getGlobalExceptionHandler().onException(e);
				} else {
					e.printStackTrace();
				}
			}
		}
	}
	
	public Iterator<E> iterator() {
		return internalList.iterator();
	}
	
}