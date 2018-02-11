package edu.udo.piq.tools;

import java.util.Iterator;

import edu.udo.piq.CallSuper;
import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootObs;
import edu.udo.piq.TemplateMethod;
import edu.udo.piq.style.PStyleSheet;
import edu.udo.piq.util.Throw;

public abstract class AbstractPStyleSheet implements PStyleSheet {
	
	protected final PRootObs rootObs = new PRootObs() {
		@Override
		public void onComponentRemovedFromGui(PComponent parent,
				PComponent removedComponent)
		{
			AbstractPStyleSheet.this.onComponentRemoved(parent, removedComponent);
		}
		@Override
		public void onComponentAddedToGui(PComponent addedComponent) {
			AbstractPStyleSheet.this.onComponentAdded(addedComponent);
		}
	};
	protected PRoot root;
	
	@TemplateMethod
	@CallSuper
	@Override
	public void onAddedToRoot(PRoot root) {
		Throw.ifNotNull(getRoot(), () -> "getRoot() != null");
		this.root = root;
		addAllComponents();
		getRoot().addObs(rootObs);
	}
	
	@TemplateMethod
	@CallSuper
	@Override
	public void onRemovedFromRoot(PRoot root) {
		Throw.ifNull(getRoot(), "getRoot() == null");
		getRoot().removeObs(rootObs);
		removeAllComponents();
		this.root = null;
	}
	
	public PRoot getRoot() {
		return root;
	}
	
	protected void addAllComponents() {
		Iterator<PComponent> iter = getRoot().getDescendants().iterator();
		iter.next();//remove root
		while (iter.hasNext()) {
			PComponent component = iter.next();
			component.setInheritedStyle(getStyleFor(component));
		}
	}
	
	protected void removeAllComponents() {
		Iterator<PComponent> iter = getRoot().getDescendants().iterator();
		iter.next();//remove root
		while (iter.hasNext()) {
			PComponent component = iter.next();
			component.setInheritedStyle(null);
		}
	}
	
	@TemplateMethod
	@CallSuper
	protected void onComponentAdded(PComponent addedComponent) {
		addedComponent.setInheritedStyle(getStyleFor(addedComponent));
	}
	
	@TemplateMethod
	@CallSuper
	protected void onComponentRemoved(PComponent parent, PComponent removedComponent) {
		removedComponent.setInheritedStyle(null);
	}
}