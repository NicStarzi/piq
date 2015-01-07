package edu.udo.piq;

import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.PGlassPanel;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.tools.AbstractPMouseObs;
import edu.udo.piq.util.PCompUtil;

public class PDnDManager {
	
	private final PMouseObs mouseObs;
	private final PRoot root;
	private PDnDTransfer activeTransfer;
	private PGlassPanel currentGlasPnl;
	private PMouse currentMouse;
	
	public PDnDManager(PRoot root) {
		this.root = root;
		mouseObs = new AbstractPMouseObs() {
			public void mouseMoved(PMouse mouse) {
				updatePosition(mouse.getX(), mouse.getY());
			}
			public void buttonReleased(PMouse mouse, MouseButton btn) {
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
		};
	}
	
	public PRoot getRoot() {
		return root;
	}
	
	public void startDrag(PDnDTransfer transfer) {
		if (activeTransfer != null) {
			throw new IllegalStateException("activeTransfer="+activeTransfer);
		}
		activeTransfer = transfer;
		currentGlasPnl = null;//getRoot().getGlassPanel();
		currentGlasPnl.getLayout().addChild(
				transfer.getVisibleRepresentation(), 
				makeInitialConstraint(transfer));
		currentMouse = root.getMouse();
		if (currentMouse != null) {
			currentMouse.addObs(mouseObs);
		}
	}
	
	protected void finishDrag() {
		PComponent source = activeTransfer.getSource();
		PDnDSupport dndSup = source.getDragAndDropSupport();
		dndSup.finishDrag(source, activeTransfer);
		endDrag();
	}
	
	protected void abortDrag() {
		PComponent source = activeTransfer.getSource();
		PDnDSupport dndSup = source.getDragAndDropSupport();
		dndSup.abortDrag(source, activeTransfer);
		endDrag();
	}
	
	protected void endDrag() {
		currentGlasPnl.getLayout().removeChild(activeTransfer.getVisibleRepresentation());
		activeTransfer = null;
		if (currentMouse != null) {
			currentMouse.removeObs(mouseObs);
		}
	}
	
	protected void drop(int x, int y) {
		// finishDrag() will reset the activeTransfer to null
		PDnDTransfer transfer = activeTransfer;
		finishDrag();
		
		PComponent dropTarget = getDropTarget(x, y);
		// Indicates an internal error as this method should only be called after checking if drop is possible
		if (dropTarget == null) {
			throw new NullPointerException("getDropTarget("+x+", "+y+")="+dropTarget);
		}
		PDnDSupport dndSup = dropTarget.getDragAndDropSupport();
		// can drop has already been invoked by #getDropTarget and returned true
		dndSup.drop(dropTarget, transfer, x, y);
	}
	
	protected boolean canDrop(int x, int y) {
		return getDropTarget(x, y) != null;
	}
	
	protected PComponent getDropTarget(int x, int y) {
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
		if (dndSup != null && dndSup.canDrop(current, activeTransfer, x, y)) {
			return current;
		} else {
			return null;
		}
	}
	
	protected void updatePosition(int x, int y) {
		PComponent cmp = activeTransfer.getVisibleRepresentation();
		PSize prefSize = PCompUtil.getPreferredSizeOf(cmp);
		int w = prefSize.getWidth();
		int h = prefSize.getHeight();
		x -= w / 2;
		y -= h / 2;
		currentGlasPnl.getLayout().updateConstraint(cmp, x, y, w, h);
	}
	
	protected FreeConstraint makeInitialConstraint(PDnDTransfer transfer) {
		PComponent cmp = activeTransfer.getVisibleRepresentation();
		PSize prefSize = PCompUtil.getPreferredSizeOf(cmp);
		int w = prefSize.getWidth();
		int h = prefSize.getHeight();
		int x = transfer.getDragStartX() - w / 2;
		int y = transfer.getDragStartY() - h / 2;
		return new FreeConstraint(x, y, w, h);
	}
	
}