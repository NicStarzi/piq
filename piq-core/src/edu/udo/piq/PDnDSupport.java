package edu.udo.piq;

import edu.udo.piq.components.PTextArea;
import edu.udo.piq.comps.selectcomps.PList;
import edu.udo.piq.comps.selectcomps.PTable;
import edu.udo.piq.comps.selectcomps.PTree;

/**
 * A {@link PDnDSupport} is used in all {@link PComponent PComponents} that allow the user 
 * to drag data from one component and drop it onto another or the same at a different location.<br>
 * Each kind of {@link PComponent} that uses such a support should provide its own 
 * {@link PDnDSupport} implementation specifically created for the components content.<br>
 * Components that support drag and drop are, amongst others, {@link PList}, {@link PTable}, 
 * {@link PTextArea} and {@link PTree}.<br>
 * The {@link PDnDSupport} is designed in a way that the same support can be reused for any 
 * number of components as by the Flyweight pattern but this is not enforced.<br>
 * <br>
 * A typical drag and drop will go like this:<br>
 * 1) A component notices, that the user wants to initiate a drag-and-drop transfer. 
 * This component will hence be called the source of the drag.<br><br>
 * 
 * 2) The {@link #canDrag(PComponent, int, int)} method of the {@link PDnDSupport} of the source 
 * component will be called<br><br>
 * 
 * 3) If the drag can happen the {@link #startDrag(PComponent, int, int)} method will be called on 
 * the same {@link PDnDSupport}<br><br>
 * 
 * 4) The user drags the created {@link PDnDTransfer} across the screen until he or she stops on top 
 * of a {@link PComponent} that has a {@link PDnDSupport} installed. This component will hence be 
 * called the target of the drag.<br><br>
 * 
 * 5) The {@link #canDrop(PComponent, PDnDTransfer, int, int)} method is called on the {@link PDnDSupport} 
 * of the target component<br><br>
 * 
 * 6) If the user indicates that the drop should happen and the drop is allowed the 
 * {@link #finishDrag(PComponent, PComponent, PDnDTransfer)} method is called on the {@link PDnDSupport} 
 * of the source component, then the {@link #drop(PComponent, PDnDTransfer, int, int)} method is called 
 * on the {@link PDnDSupport} of the target component<br><br>
 * 
 * 7) If the drop is not allowed the {@link #abortDrag(PComponent, PDnDTransfer)} method is called on the 
 * {@link PDnDSupport} of the source component<br><br>
 */
public interface PDnDSupport {
	
	/**
	 * Returns the {@link PDnDTransfer} that was previously started by this {@link PDnDSupport} 
	 * and not yet finished nor aborted.<br>
	 * If there is no such transfer null is returned.<br>
	 * If the transfer returned by this method is not null it will be equal to the transfer 
	 * returned by the {@link PDnDManager#getActiveTransfer()} method of the {@link PDnDManager} 
	 * of the source component.<br>
	 * 
	 * @return	an instance of {@link PDnDTransfer} previously generated by the 
	 * 			{@link #startDrag(PComponent, int, int)} method or null
	 * @see #startDrag(PComponent, int, int)
	 * @see #finishDrag(PComponent, PComponent, PDnDTransfer)
	 * @see #abortDrag(PComponent, PDnDTransfer)
	 * @see PDnDManager#getActiveTransfer()
	 */
	public PDnDTransfer getActiveTransfer();
	
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
	 * This method is called after {@link #finishDrag(PComponent, PComponent, PDnDTransfer)} was successfully 
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
	
	/**
	 * Returns true if a drag can be started on the source component at the given coordinates.<br>
	 * If this method returns true it means that source is part of a GUI that has a 
	 * {@link PDnDManager} installed at its root and that the {@link PDnDManager} is accepting 
	 * a new drag as defined by {@link PDnDManager#canDrag()}.<br>
	 * A return value of true also implies that there is data that can be dragged at the given 
	 * coordinates for the given source component and that the data can be removed from the 
	 * components model.<br> 
	 * 
	 * @param source the component from which data will be dragged away from
	 * @param x coordinate on X-axis in screen space
	 * @param y coordinate on Y-axis in screen space
	 * @return true if a drag can be started, false otherwise
	 * @throws NullPointerException if source is null
	 * @see #startDrag(PComponent, int, int)
	 * @see PDnDManager#canDrag()
	 */
	public boolean canDrag(PComponent source, int x, int y) throws NullPointerException;
	
	/**
	 * Starts a {@link PDnDTransfer} from the source component with whatever data can be obtained 
	 * at the given coordinates.<br>
	 * This method will create a {@link PDnDTransfer}, containing data from the source component 
	 * and possibly a visual representation of that data, and make it active in the 
	 * {@link PDnDManager} of the source components root via the 
	 * {@link PDnDManager#startDrag(PDnDTransfer)} method.<br>
	 * <br>
	 * This method may only be called if {@link #canDrag(PComponent, int, int)} has returned true 
	 * for the same arguments immediately before.<br>
	 * 
	 * @param source the component from which data will be dragged away from
	 * @param x coordinate on X-axis in screen space
	 * @param y coordinate on Y-axis in screen space
	 * @throws NullPointerException			if source is null
	 * @throws IllegalStateException		if the previous transfer generated by this 
	 * 										{@link PDnDSupport} has neither been aborted nor 
	 * 										finished yet
	 * @throws IllegalArgumentException		if the drag can not be started for any other reason
	 * @see #canDrag(PComponent, int, int)
	 * @see PDnDManager#startDrag(PDnDTransfer)
	 */
	public void startDrag(PComponent source, int x, int y) throws NullPointerException, IllegalArgumentException;
	
	/**
	 * This method is called by the {@link PDnDManager} of the GUI that source is a part of when 
	 * a drag that was previously started by this {@link PDnDSupport} has been successfully 
	 * finished. A successful finish means that the data was dropped onto the target without an 
	 * exception being thrown.<br>
	 * This method should make sure that the data from the transfer is removed from the model 
	 * of the source component.<br>
	 * <br>
	 * The source and target may be the same component. In this case data is supposed to be moved 
	 * from one point within the model to the other. Note that in this case the 
	 * {@link #drop(PComponent, PDnDTransfer, int, int)} method will be called on this drag and 
	 * drop support immediately after this method returns.<br>
	 * 
	 * @param source the component from which data was dragged from
	 * @param target the component that was dropped on
	 * @param transfer the transfer that was successfully finished
	 * @throws NullPointerException 		if either source or transfer are null
	 * @throws IllegalStateException		if transfer is not a transfer started by this {@link PDnDSupport}
	 * @throws IllegalArgumentException		if the drag can not be finished gracefully for any reason. 
	 * 										If this exception is thrown the drop on the target component 
	 * 										will not be taken back, the program will be left in an error 
	 * 										state.
	 */
	public void finishDrag(PComponent source, PComponent target, PDnDTransfer transfer) throws NullPointerException, IllegalArgumentException;
	
	/**
	 * This method is called by the {@link PDnDManager} of the GUI that source is a part of when 
	 * a drag that was previously started by this {@link PDnDSupport} has been aborted by the user. 
	 * When a drag is aborted no data will be transfered, the model of source should be unchanged.<br>
	 * This method can be used if any clean-up is necessary for the source component in case of an 
	 * aborted drag and drop.<br>
	 * 
	 * @param source the component from which the data was taken for the drag
	 * @param transfer the transfer that was created by this drag and drop support
	 * @throws NullPointerException 	if either source or transfer are null
	 * @throws IllegalStateException	if transfer is not a transfer started by this {@link PDnDSupport}
	 */
	public void abortDrag(PComponent source, PDnDTransfer transfer) throws NullPointerException;
	
	/**
	 * TODO
	 * @param source
	 * @param transfer
	 * @param x
	 * @param y
	 */
	public void showDropLocation(PComponent source, PDnDTransfer transfer, int x, int y);
	
	/**
	 * TODO
	 * @param source
	 * @param transfer
	 * @param x
	 * @param y
	 */
	public void hideDropLocation(PComponent source, PDnDTransfer transfer, int x, int y);
	
}