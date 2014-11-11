package edu.udo.piq.util;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;

import edu.udo.piq.PComponent;

public class PGuiUtil {
	
	public static String componentToString(PComponent comp) {
		return comp.toString();
	}
	
	public static String guiTreeToString(PComponent root) {
		StringBuilder sb = new StringBuilder();
		
		class PrintInfo {
			PComponent comp;
			int level;
			
			PrintInfo(PComponent comp, int level) {
				this.comp = comp;
				this.level = level;
			}
		}
		
		Deque<PrintInfo> stack = new LinkedList<>();
		stack.push(new PrintInfo(root, 0));
		while (!stack.isEmpty()) {
			PrintInfo current = stack.pop();
			
			Collection<PComponent> children = PCompUtil.getChildrenOf(current.comp);
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