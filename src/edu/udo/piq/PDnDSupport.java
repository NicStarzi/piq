package edu.udo.piq;

import edu.udo.piq.components.PList;
import edu.udo.piq.components.PTable;
import edu.udo.piq.components.PTextArea;

/**
 * A {@link PDnDSupport} is used in all {@link PComponent PComponents} that allow the user 
 * to drag data from one component and drop it onto another or the same at a different location.<br>
 * Each kind of {@link PComponent} that uses such a support should provide its own 
 * {@link PDnDSupport} implementation specifically created for the components content.<br>
 * Components that support drag and drop are, amongst others, {@link PList}, {@link PTable}, 
 * {@link PTextArea} and PTree.<br>
 * The {@link PDnDSupport} is designed in a way that the same support can be reused for any 
 * number of components as by the Flyweight pattern but this is not enforced.<br>
 * <br>
 * A typical drag and drop will go like this:<br>
 * 1) The user will indicate that a drag should happen<br><br>
 * 
 * 2) The {@link #canDrag(PComponent, int, int)} method of the {@link PDnDSupport} of the source 
 * component will be called<br><br>
 * 
 * 3) If the drag can happen the {@link #startDrag(PComponent, int, int)} method will be called on 
 * the same {@link PDnDSupport}<br><br>
 * 
 * 4) The user drags the created {@link PDnDTransfer} across a {@link PComponent} that has a 
 * {@link PDnDSupport} installed<br><br>
 * 
 * 5) The {@link #canDrop(PComponent, PDnDTransfer, int, int)} method is called on the {@link PDnDSupport} 
 * of the target component<br><br>
 * 
 * 6) If the user indicates that the drop should happen and the drop is allowed the 
 * {@link #finishDrag(PComponent, PDnDTransfer)} method is called on the {@link PDnDSupport} of the source 
 * component, then the {@link #drop(PComponent, PDnDTransfer, int, int)} method is called on the 
 * {@link PDnDSupport} of the target component<br><br>
 * 
 * 7) If the drop is not allowed the {@link #abortDrag(PComponent, PDnDTransfer)} method is called on the 
 * {@link PDnDSupport} of the source component<br><br>
 */
public interface PDnDSupport {
	
	/**
	 * Returns true if the element from the {@link PDnDTransfer} can be dropped on the target 
	 * {@link PComponent} at the given x and y coordinates.<br>
	 * This method is called continuously if there is data currently being dragged by the user 
	 * and hovered on top of the target component. The method should therefore be rather fast 
	 * and reliable.<br>
	 * This method should not throw any other {@link Exception Exceptions} then specified here.<br>
	 * If this method returns true it does not necessarily mean that a drop will actually take 
	 * place, it might be that the user is merely dragging the data across the target component.<br> 
	 * 
	 * @param target the component that will be dropped onto
	 * @param transfer contains the source of the drag and the element to be dropped
	 * @param x coordinate on screen where the data is supposed to be dropped to
	 * @param y coordinate on screen where the data is supposed to be dropped to
	 * @return true if the drop can happen
	 * @throws NullPointerException if either target or transfer is null
	 */
	public boolean canDrop(PComponent target, PDnDTransfer transfer, int x, int y) throws NullPointerException;
	
	/**
	 * Adds the dropped element from the {@link PDnDTransfer} to the model of the target component.<br>
	 * This method is called automatically by the drag and drop mechanics only if the 
	 * {@link #canDrop(PComponent, PDnDTransfer, int, int)} method of this {@link PDnDSupport} 
	 * has just returned true for the same target, transfer and coordinates.<br>
	 * If for whatever reason the drop can still not happen an {@link IllegalArgumentException}
	 * should be thrown.<br>
	 * This method is called after {@link #finishDrag(PComponent, PDnDTransfer)} was successfully 
	 * invoked on the source of the drag.<br>  
	 * 
	 * @param target the component that will be dropped onto
	 * @param transfer contains the source of the drag and the element to be dropped
	 * @param x coordinate on screen where the data is supposed to be dropped to
	 * @param y coordinate on screen where the data is supposed to be dropped to
	 * @throws NullPointerException if either target or transfer is null
	 * @throws IllegalArgumentException if the drop can not happen for any reason
	 */
	public void drop(PComponent target, PDnDTransfer transfer, int x, int y) throws NullPointerException, IllegalArgumentException;
	
	public boolean canDrag(PComponent source, int x, int y) throws NullPointerException;
	
	public PDnDTransfer startDrag(PComponent source, int x, int y) throws NullPointerException, IllegalArgumentException;
	
	public void finishDrag(PComponent source, PDnDTransfer transfer) throws NullPointerException, IllegalArgumentException;
	
	public void abortDrag(PComponent source, PDnDTransfer transfer) throws NullPointerException, IllegalArgumentException;
	
}