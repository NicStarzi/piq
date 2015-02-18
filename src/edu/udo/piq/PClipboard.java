package edu.udo.piq;

public interface PClipboard {
	
	/**
	 * Tries to store the given object in the clipboard and returns whether 
	 * the operation was successful or not.<br>
	 * If true is returned the object was stored in the clipboard and the 
	 * next {@link #load(Class)} operation will return it.<br>
	 * If false is returned the object can not be stored in the clipboard 
	 * at the moment and the clipboard contents have not changed.<br>
	 * 
	 * @param object the object to be stored in the clipboard
	 * @throws NullPointerException if object is null
	 * @see #load(Class)
	 */
	public boolean store(Object object);
	
	/**
	 * Tries to load an object of type expectedClass from the clipboard.<br>
	 * If there is no object stored in the clipboard, or if the stored 
	 * object is not of the expected class null is returned.<br>
	 * If loading is possible the loaded object is returned.<br>
	 * If the expectedClass is null then any kind of object will be loaded 
	 * regardless of type.<br>
	 * 
	 * @param expectedClass the class to be loaded or null if any kind of object is fine
	 * @return an object of type expectedClass or null if no object can be loaded
	 * @see #store(Object)
	 */
	public <E> E load(Class<E> expectedClass);
	
}