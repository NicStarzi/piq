package edu.udo.piq;

public interface PLayout extends PReadOnlyLayout {
	
	/**
	 * Adds the given {@link PComponent} to this layout with the given constraint.<br>
	 * If the component is already a child of this layout an {@link IllegalStateException}
	 * will be thrown.<br>
	 * Furthermore if the constraint is already used by another component and no two
	 * components may use the same constraint in this layout an {@link IllegalArgumentException}
	 * will be thrown.<br>
	 * This method must set the parent of the newly added component to the owner of this
	 * layout.<br>
	 * 
	 * @param component the component that is to be added
	 * @param constraint the constraint to be associated with this child
	 * @throws NullPointerException if component is null
	 * @throws IllegalArgumentException if constraint is not a valid value
	 * @throws IllegalStateException if component is already a child of this layout
	 * @see #removeChild(Object)
	 * @see #removeChild(PComponent)
	 * @see #clearChildren()
	 * @see #getChildren()
	 */
	public void addChild(PComponent component, Object constraint)
			throws NullPointerException, IllegalArgumentException, IllegalStateException;
	
	/**
	 * Removes the given child from this layout.<br>
	 * If the given component is not a child of this layout an
	 * {@link IllegalArgumentException} will be thrown.<br>
	 * This method must set the parent of the removed component to null.<br>
	 * 
	 * @param child the child that is to be removed
	 * @throws NullPointerException if child is null
	 * @throws IllegalArgumentException if child is not a child of this layout
	 * @see #addChild(PComponent, Object)
	 * @see #removeChild(Object)
	 * @see #clearChildren()
	 * @see #containsChild(PComponent)
	 */
	public void removeChild(PComponent child)
			throws NullPointerException, IllegalArgumentException;
	
	/**
	 * Removes the child that is registered with the associated constraint.<br>
	 * If no child was added with the given constraint an
	 * {@link IllegalStateException} will be thrown.<br>
	 * This method must set the parent of the removed component to null.<br>
	 * 
	 * @param constraint the constraint for the child that is to be removed
	 * @throws IllegalArgumentException if constraint is not a valid constraint
	 * @throws IllegalStateException if no child was added with the given constraint
	 * @see #addChild(PComponent, Object)
	 * @see #removeChild(PComponent)
	 * @see #getChildConstraint(PComponent)
	 * @see #containsChild(Object)
	 */
	public void removeChild(Object constraint)
			throws IllegalArgumentException, IllegalStateException;
	
	/**
	 * Removes all children of this layout if this layout contains any children.<br>
	 * For an empty layout this method does nothing.<br>
	 * This method must set the parent of all removed components to null.<br>
	 * 
	 * @see #addChild(PComponent, Object)
	 * @see #removeChild(PComponent)
	 * @see #removeChild(Object)
	 * @see #clearChildren()
	 */
	public void clearChildren();
	
	public default int max(int a, int b) {
		return Math.max(a, b);
	}
	
	public default int min(int a, int b) {
		return Math.min(a, b);
	}
	
	public default int limit(int value, int min, int max) {
		if (value <= min) {
			return min;
		}
		if (value >= max) {
			return max;
		}
		return value;
	}
	
}