package edu.udo.piq;

import java.util.ArrayList;
import java.util.List;

public interface PFocusTraversal {
	
	public void install(PRoot root);
	
	public void uninstall(PRoot root);
	
	public static List<PComponent> getAllFocusableComponents(PComponent root) {
		List<PComponent> result = new ArrayList<>();
		for (PComponent cmp : root.getDescendants()) {
			if (cmp.isFocusable()) {
				result.add(cmp);
			}
		}
		return result;
	}
	
}