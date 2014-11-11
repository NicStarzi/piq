package edu.udo.piq.tools;

import java.util.Collection;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;

public abstract class AbstractPContainer extends AbstractPLayoutOwner {
	
	public void setLayout(PLayout layout) {
		super.setLayout(layout);
	}
	
	public PLayout getLayout() {
		return super.getLayout();
	}
	
	public void addChild(PComponent component, Object constraint) {
		getLayout().addChild(component, constraint);
	}
	
	public Collection<PComponent> getChildren() {
		return super.getChildren();
	}
	
	public boolean hasChildren() {
		return super.hasChildren();
	}
	
}