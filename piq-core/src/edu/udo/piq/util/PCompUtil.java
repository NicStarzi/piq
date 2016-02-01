package edu.udo.piq.util;

import java.util.Deque;
import java.util.LinkedList;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PLayout;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.MutablePBounds;

public class PCompUtil {
	
	private PCompUtil() {}
	
	/**
	 * Returns the portion of the components {@link PBounds} that is not 
	 * clipped by the bounds of an ancestor of the component.<br>
	 * More specifically the returned {@link PBounds} are those bounds that 
	 * are created by recursively intersecting the components bounds with 
	 * its parents bounds.<br>
	 * The returned {@link PBounds} are not synchronized with the components 
	 * actual bounds and may or may not be immutable.<br>
	 * <br>
	 * If the clipped bounds of the component would have negative size, or 
	 * if the component has no parent, null is returned instead.<br>
	 * 
	 * @param result					used to store the result. If this is 
	 * 									null a new instance of {@link PBounds} 
	 * 									will be created and returned. If the  
	 * 									argument is not null it will be returned.
	 * @param comp						the component for which the clipped 
	 * 									bounds are calculated. Must not be null.
	 * @return							the clipped {@link PBounds} of this 
	 * 									component or null
	 * @throws NullPointerException		if comp is null
	 * @see PComponent#getBounds()
	 * @see PComponent#getClippedBounds()
	 * @see PBounds#makeIntersection(PBounds)
	 */
	public static PBounds fillClippedBounds(MutablePBounds result, PComponent comp) {
		ThrowException.ifNull(comp, "component == null");
		if (comp.getParent() == null) {
			return null;
		}
		PComponent current = comp;
		int clipX = Integer.MIN_VALUE;
		int clipY = Integer.MIN_VALUE;
		int clipFx = Integer.MAX_VALUE;
		int clipFy = Integer.MAX_VALUE;
		int clipW = Integer.MAX_VALUE;
		int clipH = Integer.MAX_VALUE;
		while (current != null) {
			PBounds bounds = current.getBounds();
			if (bounds == null) {
				break;
			}
			clipX = Math.max(bounds.getX(), clipX);
			clipY = Math.max(bounds.getY(), clipY);
			clipFx = Math.min(bounds.getFinalX(), clipFx);
			clipFy = Math.min(bounds.getFinalY(), clipFy);
			clipW = clipFx - clipX;
			clipH = clipFy - clipY;
			if (clipW < 0 || clipH < 0) {
				return null;
			}
			current = current.getParent();
		}
		if (result == null) {
			return new ImmutablePBounds(clipX, clipY, clipW, clipH);
		}
		result.setX(clipX);
		result.setY(clipY);
		result.setWidth(clipW);
		result.setHeight(clipH);
		return result;
	}
	
	/**
	 * Creates a new instance of {@link ObserverList} and returns it.<br>
	 * Use this method if you are not sure which implementation of 
	 * {@link ObserverList} is the best for your use case. This method 
	 * will always return the default implementation that should be used 
	 * in the most general case.<br>
	 * @return		a non-null instance of {@link ObserverList}
	 * @see ObserverList
	 */
	public static <T> ObserverList<T> createDefaultObserverList() {
		return new ArrayObsList<>();
	}
	
	/**
	 * Returns the preferred {@link PSize} of the component as returned by the 
	 * components {@link PDesign}.<br>
	 * This method never returns null.<br>
	 * 
	 * @param component
	 * @return the preferred PSize of the component as determined by the components design
	 * @throws NullPointerException if component is null
	 */
	public static PSize getPreferredSizeOf(PComponent component) throws NullPointerException {
		PDesign design = component.getDesign();
		if (design == null) {
			return component.getDefaultPreferredSize();
		}
		return design.getPreferredSize(component);
	}
	
	/**
	 * Returns whether or not the given component fills all of its pixels with its render 
	 * method. If the component has a {@link PDesign} the returned value will be taken 
	 * from the design. If the component does not have a design the 
	 * {@link PComponent#defaultFillsAllPixels()} will be used.<br>
	 * 
	 * @param component
	 * @return whether the component fills all pixels when being rendered
	 * @throws NullPointerException if component is null
	 */
	public static boolean fillsAllPixels(PComponent component) throws NullPointerException {
		if (component instanceof PRoot) {
			return true;
		}
		PDesign design = component.getDesign();
		if (design == null) {
			return component.defaultFillsAllPixels();
		}
		return design.fillsAllPixels(component);
	}
	
	/**
	 * If component is part of a GUI hierarchy with a {@link PRoot} at the 
	 * top the {@link PRoot#reRender(PComponent)} method is called on the root 
	 * of component with component as an argument.<br>
	 * If no such PRoot exists for component this method does nothing.<br>
	 * 
	 * @param component the component for which the event is fired
	 */
	public static void fireReRenderEventFor(PComponent component) {
		PRoot root = component.getRoot();
		if (root == null) {
			return;
		}
		root.reRender(component);
	}
	
	/**
	 * Returns the first descendant of root with the given ID if there is any.<br>
	 * If no such {@link PComponent} exists in the GUI null will be returned.<br>
	 * If the root itself has the given ID the root will be returned.<br>
	 * 
	 * @param root the PComponent from where we start to search
	 * @param id the ID for which we are searching for
	 * @return a PComponent with the given ID or null if no such component exists
	 * @throws IllegalArgumentException if either root or id are null
	 * @see PComponent#setID(String)
	 * @see PComponent#getID()
	 */
	public static PComponent getDescendantByID(PComponent root, String id) throws IllegalArgumentException {
		if (root == null) {
			throw new IllegalArgumentException("root == null");
		} if (id == null) {
			throw new IllegalArgumentException("id == null");
		}
		Deque<PComponent> stack = new LinkedList<>();
		stack.push(root);
		while (!stack.isEmpty()) {
			PComponent current = stack.pop();
			if (id.equals(current.getID())) {
				return current;
			}
			stack.addAll(current.getChildren());
		}
		return null;
	}
	
	/**
	 * Returns the deepest {@link PComponent} within the GUI tree of root that contains 
	 * the point at the coordinates (x, y).<br>
	 * If (x, y) is not within the clipped bounds of root null is returned, otherwise 
	 * the return value will be non-null.<br>
	 * The returned value might be root either if root does not have any children or if 
	 * no child of root contains (x, y) but root does.<br>
	 * 
	 * @param root the component that spans the GUI tree that will be searched
	 * @param x coordinate on the X-axis in window space
	 * @param y coordinate on the Y-axis in window space
	 * @return a {@link PComponent} that contains (x, y) within its clipped bounds or null if no such component exists
	 * @throws IllegalArgumentException if root is null
	 * @see PComponent#getClippedBounds()
	 * @see PBounds#contains(int, int)
	 * @see PReadOnlyLayout#getChildAt(int, int)
	 */
	public static PComponent getComponentAt(PComponent root, int x, int y) throws IllegalArgumentException {
		if (root == null) {
			throw new IllegalArgumentException("root == null");
		}
		PReadOnlyLayout current = root.getLayout();
		if (current == null) {
			return null;
		}
		// Will always stop because in each step we go down 1 level in the GUI tree
		while (true) {
			PComponent child = current.getChildAt(x, y);
			if (child == null) {
				return current.getOwner();
			} else if (child.getLayout() != null) {
				current = child.getLayout();
				continue;
			} else {
				return child;
			}
		}
	}
	
	public static boolean isAncestorOfOverlay(PComponent comp) {
		PRoot root = comp.getRoot();
		if (root == null) {
			return false;
		}
		PRootOverlay overlay = root.getOverlay();
		if (overlay == null) {
			return false;
		}
		PLayout overlayLayout = overlay.getLayout();
		while (comp != null && comp.getLayout() != overlayLayout) {
			comp = comp.getParent();
		}
		return comp != null && comp.getLayout() == overlayLayout;
	}
	
}