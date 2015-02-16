package edu.udo.piq.util;

import java.util.Deque;
import java.util.LinkedList;

import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;

public class PCompUtil {
	
	private PCompUtil() {}
	
//	/**
//	 * If component has a parent then the {@link PBounds} of component will be 
//	 * retrieved from its parents {@link PLayout}.<br>
//	 * If component is an instance of {@link PRoot} then the PBounds will be 
//	 * retrieved using the {@link PRoot#getBounds()} method.<br>
//	 * If component does neither have a parent nor is a root null will be 
//	 * returned.<br>
//	 * 
//	 * @param component
//	 * @return the PBounds of component or null if component does not have any
//	 * @throws NullPointerException if component is null
//	 */
//	public static PBounds getBoundsOf(PComponent component) throws NullPointerException {
//		if (component.getParent() != null) {
//			return component.getParent().getLayout().getChildBounds(component);
//		}
//		if (component instanceof PRoot) {
//			return ((PRoot) component).getBounds();
//		}
//		return null;
//	}
	
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
	
//	/**
//	 * Returns a {@link Collection} of all child components of component.<br>
//	 * If component has a {@link PLayout} then the method {@link PLayout#getChildren()} 
//	 * is used on the components layout to retrieve the children.<br>
//	 * If component does not have a PLayout an unmodifiable empty list is returned.<br>
//	 * This method does never return null.<br>
//	 * <br>
//	 * The returned Collection is unmodifiable and is not synchronized with the 
//	 * layout.<br>
//	 * 
//	 * @param component
//	 * @return a Collection of PComponents that are children of component
//	 * @throws NullPointerException if component is null
//	 */
//	public static Collection<PComponent> getChildrenOf(PComponent component) throws NullPointerException {
//		if (component.getLayout() != null) {
//			return component.getLayout().getChildren();
//		}
//		return Collections.emptyList();
//	}
	
//	/**
//	 * Traverses up the component hierarchy of the GUI until a 
//	 * {@link PComponent} is found that has no parent and is an instance of 
//	 * {@link PRoot}. If such a component is found it will be returned. 
//	 * If no such component can be found null will be returned.<br>
//	 * 
//	 * @param component
//	 * @return a PRoot that has no parent and is an ancestor of component
//	 * @throws NullPointerException if component is null
//	 */
//	public static PRoot getRootOf(PComponent component) throws NullPointerException {
//		if (component == null) {
//			throw new NullPointerException("component="+component);
//		}
//		PComponent comp = component;
//		while (comp != null) {
//			if (comp.getParent() == null && comp instanceof PRoot) {
//				return (PRoot) comp;
//			}
//			comp = comp.getParent();
//		}
//		return null;
//	}
	
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
//	
//	/**
//	 * Returns true if any positive number of steps up in the GUI hierarchy, 
//	 * starting from descendant, will get to maybeAncestor.<br>
//	 * A component is not an ancestor of itself.<br>
//	 * 
//	 * @param maybeAncestor the component for which is tested whether its the ancestor of descendant
//	 * @param descendant the component for which is tested if maybeAncestor is actually an ancestor
//	 * @return true if maybeAncestor is an ancestor of descendant, false otherwise
//	 * @throws NullPointerException if maybeAncestor or descendant are null
//	 */
//	public static boolean isAncestor(PComponent maybeAncestor, PComponent descendant) throws NullPointerException {
//		if (maybeAncestor == null) {
//			throw new NullPointerException();
//		}
//		PComponent comp = descendant.getParent();
//		while (comp != null) {
//			if (comp == maybeAncestor) {
//				return true;
//			}
//			comp = comp.getParent();
//		}
//		return false;
//	}
//	
//	/**
//	 * Returns true if any positive number of steps down in the GUI hierarchy, 
//	 * starting from ancestor, will get to maybeDescendant.<br>
//	 * A component is not a descendant of itself.<br>
//	 * 
//	 * @param ancestor the component for which is tested if maybeDescendant is actually a descendant
//	 * @param maybeDescendant the component for which is tested whether its a descendant of ancestor
//	 * @return true if maybeDescendant is a descendant of ancestor, false otherwise
//	 * @throws NullPointerException if ancestor or maybeDescendant are null
//	 */
//	public static boolean isDescendant(PComponent ancestor, PComponent maybeDescendant) throws NullPointerException {
//		return isAncestor(ancestor, maybeDescendant);
//	}
//	
//	/**
//	 * Returns an instance of {@link PMouse} that belongs to the GUI hierarchy 
//	 * of component or null if no such PMouse exists.<br>
//	 * It might be that component is not part of a GUI hierarchy with a 
//	 * {@link PRoot} or that the PRoot of component does not support a mouse.<br>
//	 * 
//	 * @param component
//	 * @return an instance of PMouse or null
//	 */
//	public static PMouse getMouseOf(PComponent component) {
//		PRoot root = getRootOf(component);
//		if (root == null) {
//			return null;
//		}
//		return root.getMouse();
//	}
//	
//	/**
//	 * Returns an instance of {@link PKeyboard} that belongs to the GUI hierarchy 
//	 * of component or null if no such PKeyboard exists.<br>
//	 * It might be that component is not part of a GUI hierarchy with a 
//	 * {@link PRoot} or that the PRoot of component does not support a keyboard.<br>
//	 * 
//	 * @param component
//	 * @return an instance of PKeyboard or null
//	 */
//	public static PKeyboard getKeyboardOf(PComponent component) {
//		PRoot root = getRootOf(component);
//		if (root == null) {
//			return null;
//		}
//		return root.getKeyboard();
//	}
//	
//	/**
//	 * Returns the drag and drop manager installed in the {@link PRoot} that component 
//	 * is a descendant of.<br>
//	 * If component is not a descendant of a root or if the root of descendant does not 
//	 * support a drag and drop manager null is returned.<br>
//	 * 
//	 * @param component
//	 * @return an instance of {@link PDnDManager} or null
//	 */
//	public static PDnDManager getDragAndDropManagerOf(PComponent component) {
//		PRoot root = getRootOf(component);
//		if (root == null) {
//			return null;
//		}
//		return root.getDragAndDropManager();
//	}
//	
//	/**
//	 * Returns the portion of the components {@link PBounds} that is not 
//	 * clipped by the bounds of an ancestor of component.<br>
//	 * More specifically the returned PBounds are those bounds that are 
//	 * created by recursively intersecting the components bounds with its 
//	 * parents bounds.<br>
//	 * The returned PBounds are immutable and not synchronized with the 
//	 * components bounds.<br>
//	 * <br>
//	 * If the bounds of component are completely obstructed null is 
//	 * returned.<br>
//	 * 
//	 * @param component for which the clipped bounds are returned
//	 * @return the clipped {@link PBounds} of component or null
//	 * @throws IllegalArgumentException if component is null
//	 */
//	public static PBounds getClippedBoundsOf(PComponent component) throws IllegalArgumentException {
//		if (component == null) {
//			throw new IllegalArgumentException("component == null");
//		}
//		PComponent current = component;
//		int clipX = Integer.MIN_VALUE;
//		int clipY = Integer.MIN_VALUE;
//		int clipFx = Integer.MAX_VALUE;
//		int clipFy = Integer.MAX_VALUE;
//		while (current != null) {
//			PBounds bounds = getBoundsOf(current);
//			clipX = Math.max(bounds.getX(), clipX);
//			clipY = Math.max(bounds.getY(), clipY);
//			clipFx = Math.min(bounds.getFinalX(), clipFx);
//			clipFy = Math.min(bounds.getFinalY(), clipFy);
//			int clipW = clipFx - clipX;
//			int clipH = clipFy - clipY;
//			if (clipW < 0 || clipH < 0) {
//				return null;
//			}
//			current = current.getParent();
//		}
//		return new ImmutablePBounds(clipX, clipY, clipFx - clipX, clipFy - clipY);
//	}
//	
//	/**
//	 * Returns true if the point defined by the given x and y coordinates lies 
//	 * within the clipped bounds of the given component.<br>
//	 * 
//	 * @param component the component for which the test is performed
//	 * @param x coordinate on the X-axis in window space
//	 * @param y coordinate on the Y-axis in window space
//	 * @return true if (x, y) is within the clipped bounds of component
//	 * @throws IllegalArgumentException if component is null
//	 * @see #getClippedBoundsOf(PComponent)
//	 */
//	public static boolean isWithinClippedBounds(PComponent component, int x, int y) {
//		// throws IllegalArgumentException if component is null
//		PBounds bounds = getClippedBoundsOf(component);
//		return bounds != null && bounds.contains(x, y);
//	}
//	
//	/**
//	 * Returns true if the given {@link PComponent} has focus within its GUI.<br>
//	 * If the component is not part of a GUI false is returned.<br>
//	 * This is a convenience method.<br>
//	 * 
//	 * @param component the component to check for focus
//	 * @return true if the component is part of a GUI and has focus in it
//	 * @throws NullPointerException if component is null
//	 * @see PRoot#getFocusOwner()
//	 * @see PRoot#setFocusOwner(PComponent)
//	 * @see #takeFocus(PComponent)
//	 * @see #releaseFocus(PComponent)
//	 */
//	public static boolean hasFocus(PComponent component) {
//		PRoot root = getRootOf(component);
//		if (root == null) {
//			return false;
//		}
//		return root.getFocusOwner() == component;
//	}
//	
//	/**
//	 * Makes component gain the focus in the GUI that it is a part of.<br>
//	 * If component is not part of a GUI an {@link IllegalStateException} 
//	 * is thrown.<br>
//	 * 
//	 * @param component the component to take focus
//	 * @throws NullPointerException if component is null
//	 * @throws IllegalStateException if component is not part of a GUI
//	 * @see PRoot#getFocusOwner()
//	 * @see PRoot#setFocusOwner(PComponent)
//	 * @see #hasFocus(PComponent)
//	 * @see #releaseFocus(PComponent)
//	 */
//	public static void takeFocus(PComponent component) {
//		PRoot root = getRootOf(component);
//		if (root == null) {
//			throw new IllegalStateException(component+".getRoot() == null");
//		}
//		root.setFocusOwner(component);
//	}
//	
//	/**
//	 * Sets the focused component of the GUI that component is a part of to null.<br>
//	 * If component is not part of a GUI an {@link IllegalStateException} is thrown.<br>
//	 * 
//	 * @param component a part of the GUI that has its focus owner reset
//	 * @throws NullPointerException if component is null
//	 * @throws IllegalStateException if component is not part of a GUI
//	 * @see PRoot#getFocusOwner()
//	 * @see PRoot#setFocusOwner(PComponent)
//	 * @see #hasFocus(PComponent)
//	 * @see #takeFocus(PComponent)
//	 */
//	public static void releaseFocus(PComponent component) {
//		PRoot root = getRootOf(component);
//		if (root == null) {
//			throw new IllegalStateException(component+".getRoot() == null");
//		}
//		root.setFocusOwner(null);
//	}
	
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
	 * @see #getClippedBoundsOf(PComponent)
	 * @see #isWithinClippedBounds(PComponent, int, int)
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
		// Will always stop because in each step we go down 1 level in the GUI
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
	
}