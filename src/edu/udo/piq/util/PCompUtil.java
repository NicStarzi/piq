package edu.udo.piq.util;

import java.util.Collection;
import java.util.Collections;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PMouse;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.ImmutablePBounds;

public class PCompUtil {
	
	public static PBounds getBoundsOf(PComponent component) throws NullPointerException {
		if (component.getParent() != null) {
			return component.getParent().getLayout().getChildBounds(component);
		}
		if (component instanceof PRoot) {
			return ((PRoot) component).getBounds();
		}
		return null;
	}
	
	public static PSize getPreferredSizeOf(PComponent component) throws NullPointerException {
		return component.getDesign().getPreferredSize(component);
	}
//	public static int getPreferredWidthOf(PComponent component) throws NullPointerException {
//		return component.getDesign().getPreferredWidth(component);
//	}
//	
//	public static int getPreferredHeightOf(PComponent component) throws NullPointerException {
//		return component.getDesign().getPreferredHeight(component);
//	}
	
	public static Collection<PComponent> getChildrenOf(PComponent component) throws NullPointerException {
		if (component.getLayout() != null) {
			return component.getLayout().getChildren();
		}
		return Collections.emptyList();
	}
	
	public static PRoot getRootOf(PComponent component) throws NullPointerException {
		PComponent parent = component.getParent();
		while (parent != null) {
			if (parent.getParent() == null && parent instanceof PRoot) {
				return (PRoot) parent;
			}
			parent = parent.getParent();
		}
		return null;
	}
	
	public static void fireReRenderEventFor(PComponent component) {
		PRoot root = getRootOf(component);
		if (root == null) {
			return;
		}
		root.reRender(component);
	}
	
	public static boolean isAncestor(PComponent ancestor, PComponent descendant) throws NullPointerException {
		PComponent comp = descendant;
		while (comp != null) {
			if (comp == ancestor) {
				return true;
			}
			comp = comp.getParent();
		}
		return false;
	}
	
	public static boolean isDescendant(PComponent ancestor, PComponent descendant) throws NullPointerException {
		return isAncestor(ancestor, descendant);
	}
	
	public static PMouse getMouseOf(PComponent component) {
		PRoot root = getRootOf(component);
		if (root == null) {
			return null;
		}
		return root.getMouse();
	}
	
	public static PKeyboard getKeyboardOf(PComponent component) {
		PRoot root = getRootOf(component);
		if (root == null) {
			return null;
		}
		return root.getKeyboard();
	}
	
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
	
	public static boolean isMouseOver(PComponent component) {
		PMouse mouse = getMouseOf(component);
		if (mouse == null) {
			return false;
		}
		PBounds bounds = getClippedBoundsOf(component);
		if (bounds == null) {
			return false;
		}
		return bounds.contains(mouse.getX(), mouse.getY());
	}
	
}