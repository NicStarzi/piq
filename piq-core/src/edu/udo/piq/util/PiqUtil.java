package edu.udo.piq.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.layouts.PReadOnlyLayout;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.tools.MutablePBounds;

public class PiqUtil {
	
	private PiqUtil() {}
	
	private static Consumer<Throwable> excHndlr;
	
	public static void setStaticExceptionHandler(Consumer<Throwable> exceptionHandler) {
		PiqUtil.excHndlr = exceptionHandler;
	}
	
	public static Consumer<Throwable> getStaticExceptionHandler() {
		return PiqUtil.excHndlr;
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
//		return new ArrayObsList<>();
		return new BufferedObsList<>();
//		return new BufferedWeakRefObsList<>();// for debugging purposes only
	}
	
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
	 * @see PBounds#createIntersection(PBounds)
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
	 * If component is part of a GUI hierarchy with a {@link PRoot} at the
	 * top the {@link PRoot#scheduleReRender(PComponent)} method is called on the root
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
		root.scheduleReRender(component);
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
		ThrowException.ifNull(root, "root == null");
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
	
	public static boolean isDescendantOfOverlay(PComponent comp) {
		PRoot root = comp.getRoot();
		if (root == null) {
			return false;
		}
		PRootOverlay overlay = root.getOverlay();
		if (overlay == null) {
			return false;
		}
		return comp.isDescendantOf(overlay.getLayout().getOwner());
	}
	
	/**
	 * Returns the entire GUI hierarchy starting from root as String.<br>
	 * Every component starts at its own line.<br>
	 * Each level of the tree is properly indented with a number of tab
	 * ('\t') characters corresponding to the depth of the level.<br>
	 * 
	 * @param root the point from where the method starts constructing the String
	 * @return a String representation of root and all its descendants
	 * @throws NullPointerException if root is null
	 */
	public static String guiTreeToString(PComponent root) throws NullPointerException {
		StringBuilder sb = new StringBuilder();
		
		class PrintInfo {
			PComponent comp;
			int level;
			
			PrintInfo(PComponent comp, int level) {
				this.comp = comp;
				this.level = level;
			}
		}
		
		Deque<PrintInfo> stack = new ArrayDeque<>();
		stack.push(new PrintInfo(root, 0));
		while (!stack.isEmpty()) {
			PrintInfo current = stack.pop();
			
			Iterable<PComponent> children = current.comp.getChildren();
			for (PComponent comp : children) {
				stack.addFirst(new PrintInfo(comp, current.level + 1));
			}
			
			for (int i = 0; i < current.level; i++) {
				sb.append('\t');
			}
			sb.append(current.comp.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
	
}