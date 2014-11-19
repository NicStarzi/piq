package edu.udo.piq.tools;

import java.util.Collection;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PSize;

public class UnmodifiablePLayoutView implements PLayout {
	
	protected final PLayout delegate;
	
	public UnmodifiablePLayoutView(PLayout layout) {
		delegate = layout;
	}
	
	public PComponent getOwner() {
		return delegate.getOwner();
	}
	
	public void addChild(PComponent component, Object constraint)
			throws NullPointerException, IllegalArgumentException,
			IllegalStateException {
		throw new UnsupportedOperationException();
	}
	
	public void removeChild(PComponent child) throws NullPointerException,
			IllegalArgumentException {
		throw new UnsupportedOperationException();
	}
	
	public void removeChild(Object constraint) throws IllegalArgumentException,
			IllegalStateException {
		throw new UnsupportedOperationException();
	}
	
	public void clearChildren() {
		throw new UnsupportedOperationException();
	}
	
	public boolean containsChild(PComponent child) throws NullPointerException {
		return delegate.containsChild(child);
	}
	
	public boolean containsChild(Object constraint)
			throws IllegalArgumentException {
		return delegate.containsChild(constraint);
	}
	
	public PBounds getChildBounds(PComponent child)
			throws NullPointerException, IllegalArgumentException {
		return delegate.getChildBounds(child);
	}
	
	public Object getChildConstraint(PComponent child)
			throws NullPointerException, IllegalArgumentException {
		return delegate.getChildConstraint(child);
	}
	
	public Collection<PComponent> getChildren() {
		return delegate.getChildren();
	}
	
	public void layOut() {
		delegate.layOut();
	}
	
	public PSize getPreferredSize() {
		return delegate.getPreferredSize();
	}
	
	public void addObs(PLayoutObs obs) throws NullPointerException {
		delegate.addObs(obs);
	}
	
	public void removeObs(PLayoutObs obs) throws NullPointerException {
		delegate.removeObs(obs);
	}
}