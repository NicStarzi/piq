package edu.udo.piq.components;

import edu.udo.piq.components.util.PModelHistory;

public interface PListModel {
	
	/**
	 * Returns the number of elements in this model.<br>
	 * The returned value will never be negative.<br>
	 * The highest index of an element in this model will be 
	 * <code>{@link #getElementCount()} - 1.</code><br>
	 * 
	 * @return the number of elements in this model
	 * @see #getElement(int)
	 * @see #getElementIndex(Object)
	 */
	public int getElementCount();
	
	/**
	 * Returns the element from this model at the given index.<br>
	 * If the index is below 0 or greater then or equal to 
	 * {@link #getElementCount()} an 
	 * {@link IndexOutOfBoundsException} will be thrown.<br>
	 * 
	 * @param index the index of the returned element
	 * @return an element from this model
	 * @throws IndexOutOfBoundsException if index < 0 or index >= {@link #getElementCount()}
	 * @see #getElementCount()
	 * @see #getElementIndex(Object)
	 */
	public Object getElement(int index) throws IndexOutOfBoundsException;
	
	/**
	 * Returns the index of the first occurrence of the given 
	 * element within this model.<br>
	 * If the element is not contained within this model the 
	 * index -1 is returned.<br>
	 * 
	 * @param element the element that is being searched
	 * @return the index of element or -1 if element is not part of this model
	 * @see #getElementCount()
	 * @see #getElement(int)
	 */
	public int getElementIndex(Object element);
	
	/**
	 * Returns true if the given element can be added with the 
	 * given index to this model via the 
	 * {@link #addElement(int, Object)} method.<br>
	 * This method should never throw an exception, even if the 
	 * element is not of the correct type for this model or the 
	 * index is out of bounds.<br>
	 * 
	 * @param index the index where the add should take place
	 * @param element the element to be added
	 * @return true if adding the element will not fail, false otherwise
	 * @see #addElement(int, Object)
	 */
	public boolean canAddElement(int index, Object element);
	
	/**
	 * Adds the given element to this model at the given index.<br>
	 * If there already is an other element at the index it will 
	 * be pushed to the right as well as any subsequent elements 
	 * in the model.<br><br>
	 * If the index is illegal or if the element can not be added 
	 * at the index an {@link IllegalArgumentException} will be 
	 * thrown.<br>
	 * Before attempting to call this method it is beneficial to 
	 * first call {@link #canAddElement(int, Object)} to make sure 
	 * the operation will succeed.<br><br>
	 * If an exception is thrown by this method no changes will be 
	 * made to the model.<br>
	 * If no exception is thrown the element was successfully added 
	 * to this model at the given index.<br><br>
	 * This method should notify all 
	 * {@link PListModelObs PListModelObservers} of the change by 
	 * calling the {@link PListModelObs#elementAdded(PListModel, Object, int)} 
	 * method <b>after</b> the insertion finished.<br>
	 * 
	 * @param index the index at which the element will be added
	 * @param element the element that will be added
	 * @throws IllegalArgumentException if index < 0 or index > {@link #getElementCount()} or element can not be added
	 * @see #canAddElement(int, Object)
	 */
	public void addElement(int index, Object element) throws IllegalArgumentException;
	
	/**
	 * Returns true if an element with the given index exists and 
	 * that element can be removed from the model via the 
	 * {@link #removeElement(int)} method.<br>
	 * This method should never throw an exception, even if the 
	 * index is out of bounds.<br>
	 * 
	 * @param index the index of the element that is to be removed
	 * @return true if the element can be removed, otherwise false
	 * @see #removeElement(int)
	 */
	public boolean canRemoveElement(int index);
	
	/**
	 * Removes the element at the given index from this model.<br>
	 * Any subsequent elements will be pushed to the left to 
	 * fill the gap.<br><br>
	 * If the index is out of bounds or if the element at the 
	 * index can not be removed an {@link IllegalArgumentException} 
	 * will be thrown.<br>
	 * Before attempting to call this method it is beneficial to 
	 * first call {@link #canRemoveElement(int)} to make sure the 
	 * operation will succeed.<br><br>
	 * If an exception is thrown by this method no changes will be 
	 * made to the model.<br>
	 * If no exception is thrown the element at the given index was 
	 * successfully removed from this model .<br><br>
	 * This method should notify all 
	 * {@link PListModelObs PListModelObservers} of the change by 
	 * calling the {@link PListModelObs#elementRemoved(PListModel, Object, int)} 
	 * method <b>after</b> the removal finished.<br>
	 * 
	 * @param index the index of the element that is to be removed
	 * @throws IllegalArgumentException if the index is illegal or the element can not be removed
	 * @see #canRemoveElement(int)
	 */
	public void removeElement(int index) throws IllegalArgumentException;
	
	/**
	 * Returns an instance of of {@link PModelHistory} if this model 
	 * supports undo and redo operations or returns null if undo and 
	 * redo is not supported.<br>
	 * 
	 * @return an instance of {@link PModelHistory} or null
	 */
	public PModelHistory getHistory();
	
	public void addObs(PListModelObs obs);
	
	public void removeObs(PListModelObs obs);
	
}