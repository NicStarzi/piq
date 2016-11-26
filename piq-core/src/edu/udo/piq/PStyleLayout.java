package edu.udo.piq;

public interface PStyleLayout {
	
	public <E> E getAttribute(PReadOnlyLayout layout, 
			Object attrKey, Class<E> attrType);
	
}