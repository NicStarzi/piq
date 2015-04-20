package edu.udo.piq.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

import edu.udo.piq.PComponent;

public class PGuiUtil {
	
	public static String componentToString(PComponent comp) {
		return comp.toString();
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
			
			Collection<PComponent> children = current.comp.getChildren();
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
	
	/**
	 * Returns an iterator that will traverse all components within a GUI tree.<br>
	 * @param root						component used as root to the GUI tree
	 * @return							a non-null instance of {@link PGuiTreeIterator}
	 * @throws NullPointerException		if root is null
	 */
	public static Iterator<PComponent> guiTreeIterator(PComponent root) {
		return new PGuiTreeIterator(root);
	}
	
}