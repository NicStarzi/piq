package edu.udo.piq;

import java.util.List;

public interface PFocusTraversal {
	
	public void install(PRoot root);
	
	public void uninstall(PRoot root);
	
	public static List<PComponent> getAllFocusableComponents(PComponent root) {
		return root.getDescendants().require(desc -> desc.isFocusable()).toList();
	}
	
}