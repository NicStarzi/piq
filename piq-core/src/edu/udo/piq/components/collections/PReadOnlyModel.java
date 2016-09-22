package edu.udo.piq.components.collections;

import java.util.ConcurrentModificationException;

public interface PReadOnlyModel {
	
	public Object get(PModelIndex index) 
			throws WrongIndexType, NullPointerException, IllegalIndex;
	
	/**
	 * Returns true if this model has content stored at the given 
	 * {@link PModelIndex index}. Otherwise false is returned.<br>
	 * The index must still be of a valid type or an exception is 
	 * thrown.<br>
	 * 
	 * @param index				the queried index
	 * @return					true if there is data at this index
	 * @throws WrongIndexType	if the index is not of the right type
	 * @throws NullPointerException		if index is null
	 */
	public boolean contains(PModelIndex index) 
			throws WrongIndexType, NullPointerException;
	
	/**
	 * Returns an instance of {@link PModelIndex} at which the given 
	 * content is stored within this model. The returned index is 
	 * always a valid index for this model.<br>
	 * If the content is not contained within this model null is 
	 * returned.<br>
	 * 
	 * @param content			an object or null that may or may not be stored within this model
	 * @return					an instance of {@link PModelIndex} or null
	 */
	public PModelIndex getIndexOf(Object content);
	
	/**
	 * Adds the given {@link PModelObs observer} to this model.<br> 
	 * Observers are notified of any changes to the model, these 
	 * include:<br>
	 *  - when content is added<br>
	 *  - when content is removed<br>
	 *  - when content has changed in some way<br>
	 * An observer is usually used by the 
	 * {@link PSelectionComponent component} that represents this 
	 * model visually.<br>
	 * 
	 * @param obs						a non-null observer
	 * @throws NullPointerException		if obs is null
	 */
	public void addObs(PModelObs obs) 
			throws NullPointerException;
	
	/**
	 * Removes the {@link PModelObs observer} from this model if it 
	 * was added previously. If the observer was not added before 
	 * this method will do nothing.<br>
	 * After a call to this method the observer will no longer 
	 * receive any notifications. If the observer is removed during 
	 * a notification there will be <b>no</b> 
	 * {@link ConcurrentModificationException}.<br>
	 * 
	 * @param obs						a non-null observer
	 * @throws NullPointerException		if obs is null
	 */
	public void removeObs(PModelObs obs) 
			throws NullPointerException;
	
}