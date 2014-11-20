package edu.udo.piq.util;

import java.util.Collection;
import java.util.Collections;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDesign;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PLayout;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePBounds;

public class PCompUtil {
	
	private PCompUtil() {}
	
	/**
	 * If component has a parent then the {@link PBounds} of component will be 
	 * retrieved from its parents {@link PLayout}.<br>
	 * If component is an instance of {@link PRoot} then the PBounds will be 
	 * retrieved using the {@link PRoot#getBounds()} method.<br>
	 * If component does neither have a parent nor is a root null will be 
	 * returned.<br>
	 * 
	 * @param component
	 * @return the PBounds of component or null if component does not have any
	 * @throws NullPointerException if component is null
	 */
	public static PBounds getBoundsOf(PComponent component) throws NullPointerException {
		if (component.getParent() != null) {
			return component.getParent().getLayout().getChildBounds(component);
		}
		if (component instanceof PRoot) {
			return ((PRoot) component).getBounds();
		}
		return null;
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
	 * Returns a {@link Collection} of all child components of component.<br>
	 * If component has a {@link PLayout} then the method {@link PLayout#getChildren()} 
	 * is used on the components layout to retrieve the children.<br>
	 * If component does not have a PLayout an unmodifiable empty list is returned.<br>
	 * This method does never return null.<br>
	 * <br>
	 * The returned Collection is unmodifiable and is not synchronized with the 
	 * layout.<br>
	 * 
	 * @param component
	 * @return a Collection of PComponents that are children of component
	 * @throws NullPointerException if component is null
	 */
	public static Collection<PComponent> getChildrenOf(PComponent component) throws NullPointerException {
		if (component.getLayout() != null) {
			return component.getLayout().getChildren();
		}
		return Collections.emptyList();
	}
	
	/**
	 * Traverses up the component hierarchy of the GUI until a 
	 * {@link PComponent} is found that has no parent and is an instance of 
	 * {@link PRoot}. If such a component is found it will be returned. 
	 * If no such component can be found null will be returned.<br>
	 * 
	 * @param component
	 * @return a PRoot that has no parent and is an ancestor of component
	 * @throws NullPointerException if component is null
	 */
	public static PRoot getRootOf(PComponent component) throws NullPointerException {
		if (component == null) {
			throw new NullPointerException("component="+component);
		}
		PComponent comp = component;
		while (comp != null) {
			if (comp.getParent() == null && comp instanceof PRoot) {
				return (PRoot) comp;
			}
			comp = comp.getParent();
		}
		return null;
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
		PRoot root = getRootOf(component);
		if (root == null) {
			return;
		}
		root.reRender(component);
	}
	
	/**
	 * Returns true if any positive number of steps up in the GUI hierarchy, 
	 * starting from descendant, will get to maybeAncestor.<br>
	 * A component is not an ancestor of itself.<br>
	 * 
	 * @param maybeAncestor the component for which is tested whether its the ancestor of descendant
	 * @param descendant the component for which is tested if maybeAncestor is actually an ancestor
	 * @return true if maybeAncestor is an ancestor of descendant, false otherwise
	 * @throws NullPointerException if maybeAncestor or descendant are null
	 */
	public static boolean isAncestor(PComponent maybeAncestor, PComponent descendant) throws NullPointerException {
		if (maybeAncestor == null) {
			throw new NullPointerException();
		}
		PComponent comp = descendant.getParent();
		while (comp != null) {
			if (comp == maybeAncestor) {
				return true;
			}
			comp = comp.getParent();
		}
		return false;
	}
	
	/**
	 * Returns true if any positive number of steps down in the GUI hierarchy, 
	 * starting from ancestor, will get to maybeDescendant.<br>
	 * A component is not a descendant of itself.<br>
	 * 
	 * @param ancestor the component for which is tested if maybeDescendant is actually a descendant
	 * @param maybeDescendant the component for which is tested whether its a descendant of ancestor
	 * @return true if maybeDescendant is a descendant of ancestor, false otherwise
	 * @throws NullPointerException if ancestor or maybeDescendant are null
	 */
	public static boolean isDescendant(PComponent ancestor, PComponent maybeDescendant) throws NullPointerException {
		return isAncestor(ancestor, maybeDescendant);
	}
	
	/**
	 * Returns an instance of {@link PMouse} that belongs to the GUI hierarchy 
	 * of component or null if no such PMouse exists.<br>
	 * It might be that component is not part of a GUI hierarchy with a 
	 * {@link PRoot} or that the PRoot of component does not support a mouse.<br>
	 * 
	 * @param component
	 * @return an instance of PMouse or null
	 */
	public static PMouse getMouseOf(PComponent component) {
		PRoot root = getRootOf(component);
		if (root == null) {
			return null;
		}
		return root.getMouse();
	}
	
	/**
	 * Returns an instance of {@link PKeyboard} that belongs to the GUI hierarchy 
	 * of component or null if no such PKeyboard exists.<br>
	 * It might be that component is not part of a GUI hierarchy with a 
	 * {@link PRoot} or that the PRoot of component does not support a keyboard.<br>
	 * 
	 * @param component
	 * @return an instance of PKeyboard or null
	 */
	public static PKeyboard getKeyboardOf(PComponent component) {
		PRoot root = getRootOf(component);
		if (root == null) {
			return null;
		}
		return root.getKeyboard();
	}
	
	/**
	 * Returns the portion of the components {@link PBounds} that is not 
	 * clipped by the bounds of an ancestor of component.<br>
	 * More specifically the returned PBounds are those bounds that are 
	 * created by recursively intersecting the components bounds with its 
	 * parents bounds.<br>
	 * The returned PBounds are immutable and not synchronized with the 
	 * components bounds.<br>
	 * <br>
	 * If the bounds of component are completely obstructed null is 
	 * returned.<br>
	 * 
	 * @param component for which the clipped bounds are returned
	 * @return the clipped {@link PBounds} of component or null
	 */
	public static PBounds getClippedBoundsOf(PComponent component) {
		PComponent current = component;
		int clipX = Integer.MIN_VALUE;
		int clipY = Integer.MIN_VALUE;
		int clipFx = Integer.MAX_VALUE;
		int clipFy = Integer.MAX_VALUE;
		while (current != null) {
			PBounds bounds = getBoundsOf(current);
			clipX = Math.max(bounds.getX(), clipX);
			clipY = Math.max(bounds.getY(), clipY);
			clipFx = Math.min(bounds.getFinalX(), clipFx);
			clipFy = Math.min(bounds.getFinalY(), clipFy);
			int clipW = clipFx - clipX;
			int clipH = clipFy - clipY;
			if (clipW < 0 || clipH < 0) {
				return null;
			}
			current = current.getParent();
		}
		return new ImmutablePBounds(clipX, clipY, clipFx - clipX, clipFy - clipY);
	}
	
	/**
	 * Returns true if the point defined 
	 * @param component
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean isWithinClippedBounds(PComponent component, int x, int y) {
		PBounds bounds = getClippedBoundsOf(component);
		return bounds != null && bounds.contains(x, y);
	}
	
}