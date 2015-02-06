package edu.udo.piq;

import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.PGlassPanel;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.tools.AbstractPMouseObs;
import edu.udo.piq.util.PCompUtil;

/**
 * The {@link PDnDManager} is a part of a {@link PRoot} and handles dragging and 
 * dropping of {@link PDnDTransfer PDnDTransfers} from one {@link PComponent} to 
 * another within the same GUI.<br>
 * There should be one instance of this class per {@link PRoot} that never changes 
 * within the {@link PRoot} life cycle.<br>
 * <br>
 * {@link PDnDManager PDnDManagers} make use of the {@link PRootOverlay} of the 
 * {@link PRoot} as it is returned by the {@link PRoot#getOverlay()} method to 
 * render the visible representation of a drag and drop.<br>
 * For the control of the drag a {@link PMouseObs} is installed in the 
 * {@link PRoot PRoots} {@link PMouse}.<br>
 * <br>
 * The default {@link PDnDManager} implementation can only manage one drag at a 
 * time and does not allow another drag to start until the previous drag has either 
 * finished or was aborted.<br>
 * 
 * @see PDnDSupport
 * @see PDnDTransfer
 * @see PRoot#getMouse()
 * @see PRoot#getOverlay()
 * @see PRoot#getDragAndDropManager()
 */
public class PDnDManager {
	
	/**
	 * Is registered at the roots mouse only when needed
	 */
	private final PMouseObs mouseObs;
	/**
	 * The root for which this manager was created
	 */
	private final PRoot root;
	private PComponent dropLocationComponent;
	private PDnDTransfer activeTransfer;
	private PRootOverlay currentOverlay;
	private PMouse currentMouse;
	
	/**
	 * Creates a new {@link PDnDManager} for the given {@link PRoot}.<br>
	 * The root for the manager can not be changed after creation.<br>
	 * The root will be used to get the {@link PGlassPanel} and the {@link PMouse} to 
	 * be used.<br>
	 * 
	 * @param root the root that the manager will use 
	 * @throws NullPointerException if root is null
	 * @see PRoot#getMouse()
	 * @see PRoot#getGlassPanel()
	 * @see PRoot#getDragAndDropManager()
	 */
	public PDnDManager(PRoot root) throws NullPointerException {
		if (root == null) {
			throw new NullPointerException("root == null");
		}
		this.root = root;
		mouseObs = new AbstractPMouseObs() {
			public void mouseMoved(PMouse mouse) {
				onMouseMove(mouse);
			}
			public void buttonReleased(PMouse mouse, MouseButton btn) {
				onMouseRelease(mouse, btn);
			}
		};
	}
	
	/**
	 * Is called by the {@link PMouseObs} when the mouse has moved while a drag and 
	 * drop is currently taking place.<br>
	 * This method should be used to update the position of the visual representation 
	 * of the active drag and drop.<br>
	 * 
	 * @param mouse never null
	 */
	protected void onMouseMove(PMouse mouse) {
		updatePosition(mouse.getX(), mouse.getY());
	}
	
	/**
	 * Is called by the {@link PMouseObs} when a mouse button was released while a 
	 * drag and drop is currently taking place.<br>
	 * If the released mouse button was the left mouse button the drag we will either 
	 * drop the active transfer on a component or abort the drag the component at the 
	 * mouse location is not accepting the transfer.<br>
	 * 
	 * @param mouse never null
	 * @param btn never null
	 */
	protected void onMouseRelease(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT) {
			int x = mouse.getX();
			int y = mouse.getY();
			if (canDrop(x, y)) {
				drop(x, y);
			} else {
				abortDrag();
			}
		}
	}
	
	/**
	 * Returns the root for this drag and drop manager.<br>
	 * The returned root will never be null.<br>
	 * 
	 * @return the root of this {@link PDnDManager}
	 */
	public PRoot getRoot() {
		return root;
	}
	
	/**
	 * If this method returns true then the given transfer can be used as an argument to the 
	 * {@link #startDrag(PDnDTransfer)} method without causing an exception to be thrown.<br>
	 * 
	 * @param transfer
	 * @return
	 */
	public boolean canDrag() {
		return getActiveTransfer() == null;
	}
	
	/**
	 * Initializes a drag.<br>
	 * If the root supports an overlay and the transfer supports a visual representation 
	 * then the visual representation will be added to the overlay and updated as needed.<br>
	 * <br>
	 * If a drag is already taking place when this method is called an exception is 
	 * thrown since a {@link PDnDManager} does not support multiple drags at once.<br>
	 * <br>
	 * The drag can be aborted with the {@link #abortDrag()} method.<br>
	 * 
	 * @param transfer the transfer that will be dragged and potentially dropped
	 * @throws IllegalStateException if a drag is already active when the method is called
	 * @throws IllegalArgumentException if transfer is null
	 * @see PDnDSupport
	 * @see PDnDSupport#startDrag(PComponent, int, int)
	 * @see PDnDTransfer
	 * @see #abortDrag()
	 */
	public void startDrag(PDnDTransfer transfer) throws IllegalStateException, IllegalArgumentException {
		if (getActiveTransfer() != null) {
			throw new IllegalStateException("activeTransfer != null");
		}
		if (transfer == null) {
			throw new IllegalArgumentException("transfer == null");
		}
		activeTransfer = transfer;
		
		currentOverlay = getRoot().getOverlay();
		if (hasVisualRepresentation()) {
			currentOverlay.getLayout().addChild(
					transfer.getVisibleRepresentation(), 
					makeInitialConstraint(transfer));
		}
		
		currentMouse = root.getMouse();
		if (currentMouse != null) {
			currentMouse.addObs(mouseObs);
		}
	}
	
	/**
	 * Returns the {@link PDnDTransfer} that is currently active, that is, being actively 
	 * dragged and shown on the overlay.<br>
	 * If no transfer is taking place at the moment the returned value will be null.<br>
	 * 
	 * @return the active drag and drop transfer or null if no drag is taking place
	 * @see #startDrag(PDnDTransfer)
	 * @see #abortDrag()
	 */
	public PDnDTransfer getActiveTransfer() {
		return activeTransfer;
	}
	
	/**
	 * Called when the drag is supposed to be successfully finished.<br>
	 * Invokes {@link PDnDSupport#finishDrag(PComponent, PComponent, PDnDTransfer)} on the drag 
	 * and drop support of the source of the active drag. Also removes the visual representation 
	 * from the overlay if it exists and unregisters the {@link PMouseObs}.<br>
	 * Afterwards the active transfer will be null.<br>
	 * <br>
	 * This method is invoked before the transfer has been dropped on the target component.<br>
	 * 
	 * @param target that component that the data is dropped to
	 * @throws IllegalStateException if no drag is taking place
	 * @see #finishDrag()
	 * @see #startDrag(PDnDTransfer)
	 */
	protected void finishDrag(PComponent target) throws IllegalStateException {
		throwExceptionIfNoDragActive();
		
		PComponent source = getActiveTransfer().getSource();
		PDnDSupport dndSup = source.getDragAndDropSupport();
		dndSup.finishDrag(source, target, getActiveTransfer());
		
		endDrag();
	}
	
	/**
	 * Aborts the drag that is currently taking place by calling the 
	 * {@link PDnDSupport#abortDrag(PComponent, PDnDTransfer)} method on the {@link PDnDSupport} 
	 * of the source of the active {@link PDnDTransfer}.<br>
	 * After this method has finished no {@link PDnDTransfer} will be active and the visual 
	 * representation of the transfer will be removed from the overlay if it exists.<br>
	 * 
	 * @throws IllegalStateException if no drag is currently taking place
	 * @see #startDrag(PDnDTransfer)
	 */
	public void abortDrag() throws IllegalStateException {
		throwExceptionIfNoDragActive();
		
		PComponent source = getActiveTransfer().getSource();
		PDnDSupport dndSup = source.getDragAndDropSupport();
		dndSup.abortDrag(source, getActiveTransfer());
		
		endDrag();
	}
	
	/**
	 * Unregisters the {@link PMouseObs} and removes the visible representation of the drag 
	 * from the overlay if it exists.<br>
	 * After this method has finished the active transfer is null.<br>
	 * 
	 * @throws IllegalStateException if no drag is currently taking place
	 * @see #abortDrag()
	 * @see #finishDrag()
	 * @see #startDrag(PDnDTransfer)
	 */
	protected void endDrag() throws IllegalStateException {
		throwExceptionIfNoDragActive();
		
		if (currentMouse != null) {
			currentMouse.removeObs(mouseObs);
		}
		
		if (dropLocationComponent != null) {
			PDnDSupport dndSup = dropLocationComponent.getDragAndDropSupport();
			dndSup.hideDropLocation(dropLocationComponent, activeTransfer, -1, -1);
		}
		
		if (hasVisualRepresentation()) {
			currentOverlay.getLayout().removeChild(
					getActiveTransfer().getVisibleRepresentation());
		}
		
		activeTransfer = null;
	}
	
	/**
	 * Finishes the drag as defined by {@link #finishDrag()}, then drops the transfer on 
	 * the target component by invoking {@link PDnDSupport#drop(PComponent, PDnDTransfer, int, int)} 
	 * on the components drag and drop support.<br>
	 * 
	 * @param x where to drop the transfer
	 * @param y where to drop the transfer
	 * @throws IllegalStateException if no drag is currently taking place
	 * @throws NullPointerException if the drop target is null
	 * @see #canDrop(int, int)
	 * @see #onMouseRelease(PMouse, MouseButton)
	 */
	protected void drop(int x, int y) throws IllegalStateException, NullPointerException {
		throwExceptionIfNoDragActive();
		
		PComponent dropTarget = getDropTarget(x, y);
		// finishDrag() will reset the activeTransfer to null
		PDnDTransfer transfer = getActiveTransfer();
		finishDrag(dropTarget);
		
		// Indicates an internal error as this method should only be called after checking if drop is possible
		if (dropTarget == null) {
			throw new NullPointerException("getDropTarget("+x+", "+y+")="+dropTarget);
		}
		PDnDSupport dndSup = dropTarget.getDragAndDropSupport();
		// can drop has already been invoked by getDropTarget(x, y) and returned true
		dndSup.drop(dropTarget, transfer, x, y);
	}
	
	/**
	 * Returns true if the active transfer can be dropped on the given point.<br>
	 * If this method returns true the {@link #getDropTarget(int, int)} method will return a 
	 * non null component as the drop target.<br>
	 * Furthermore the drop target will have a {@link PDnDSupport} that returns true with 
	 * its {@link PDnDSupport#canDrop(PComponent, PDnDTransfer, int, int)} method for the 
	 * active transfer.<br>
	 * 
	 * @param x where to drop the transfer
	 * @param y where to drop the transfer
	 * @return true if the active transfer can be dropped
	 * @throws IllegalStateException if no drag is currently taking place
	 */
	protected boolean canDrop(int x, int y) throws IllegalStateException {
		return getDropTarget(x, y) != null;
	}
	
	protected PComponent getDropTarget(int x, int y) throws IllegalStateException {
		throwExceptionIfNoDragActive();
		
		PComponent current = PCompUtil.getComponentAt(getRoot(), x, y);
		PDnDSupport dndSup = null;
		while (current != null) {
			dndSup = current.getDragAndDropSupport();
			if (dndSup == null) {
				current = current.getParent();
			} else {
				break;
			}
		}
		if (dndSup != null && dndSup.canDrop(current, getActiveTransfer(), x, y)) {
			return current;
		} else {
			return null;
		}
	}
	
	protected void updatePosition(int x, int y) throws IllegalStateException {
		throwExceptionIfNoDragActive();
		
		// Hide old drop location if drop target changed
		PComponent dropTarget = getDropTarget(x, y);
		if (dropLocationComponent != null 
				&& dropLocationComponent != dropTarget) 
		{
			PDnDSupport dndSup = dropLocationComponent.getDragAndDropSupport();
			dndSup.hideDropLocation(dropLocationComponent, getActiveTransfer(), x, y);
		}
		dropLocationComponent = dropTarget;
		// Show drop location on drop target component
		if (dropLocationComponent != null) {
			PDnDSupport dndSup = dropLocationComponent.getDragAndDropSupport();
			dndSup.showDropLocation(dropLocationComponent, getActiveTransfer(), x, y);
		}
		// Update position of visual representation of transfer
		if (hasVisualRepresentation()) {
			PComponent cmp = getActiveTransfer().getVisibleRepresentation();
			PSize prefSize = PCompUtil.getPreferredSizeOf(cmp);
			int w = prefSize.getWidth();
			int h = prefSize.getHeight();
			x -= w / 2;
			y -= h / 2;
			currentOverlay.getLayout().updateConstraint(cmp, x, y, w, h, Integer.MAX_VALUE);
		}
	}
	
	protected FreeConstraint makeInitialConstraint(PDnDTransfer transfer) throws IllegalStateException {
		throwExceptionIfNoDragActive();
		
		PComponent cmp = activeTransfer.getVisibleRepresentation();
		PSize prefSize = PCompUtil.getPreferredSizeOf(cmp);
		int w = prefSize.getWidth();
		int h = prefSize.getHeight();
		int x = transfer.getDragStartX() - w / 2;
		int y = transfer.getDragStartY() - h / 2;
		return new FreeConstraint(x, y, w, h, Integer.MAX_VALUE);
	}
	
	protected boolean hasVisualRepresentation() {
		return getActiveTransfer() != null && currentOverlay != null 
				&& getActiveTransfer().getVisibleRepresentation() != null;
	}
	
	protected void throwExceptionIfNoDragActive() {
		if (getActiveTransfer() == null) {
			throw new IllegalStateException("activeTransfer == null");
		}
	}
	
}