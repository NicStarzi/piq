package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PCursor;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.util.ObserverList;
import edu.udo.piq.util.PCompUtil;
import edu.udo.piq.util.ThrowException;

public class DelegatePMouse implements PMouse {
	
	protected final ObserverList<PMouseObs> obsList
		= PCompUtil.createDefaultObserverList();
	protected final PMouseObs delegateObs = new PMouseObs() {
		public void onMouseMoved(PMouse mouse) {
			compAtMouseCacheValid = false;
			obsList.fireEvent((obs) -> obs.onMouseMoved(DelegatePMouse.this));
		}
		public void onButtonPressed(PMouse mouse, MouseButton btn) {
			obsList.fireEvent((obs) -> obs.onButtonPressed(DelegatePMouse.this, btn));
		}
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			obsList.fireEvent((obs) -> obs.onButtonTriggered(DelegatePMouse.this, btn));
		}
		public void onButtonReleased(PMouse mouse, MouseButton btn) {
			obsList.fireEvent((obs) -> obs.onButtonReleased(DelegatePMouse.this, btn));
		}
		public String toString() {
			return "Hello World!";
		}
	};
	protected final PRoot root;
	protected PMouse delegate;
	protected PComponent compAtMouseCache;
	protected boolean compAtMouseCacheValid;
	
	public DelegatePMouse(PRoot root) {
		this.root = root;
	}
	
	public void setDelegate(PMouse value) {
		ThrowException.ifEqual(this, value, "this == value");
		if (getDelegate() != null) {
			getDelegate().removeObs(delegateObs);
		}
		delegate = value;
		compAtMouseCacheValid = false;
		if (getDelegate() != null) {
			getDelegate().addObs(delegateObs);
		}
	}
	
	public PMouse getDelegate() {
		return delegate;
	}
	
	public int getX() {
		if (delegate == null) {
			return 0;
		}
		return delegate.getX();
	}
	
	public int getY() {
		if (delegate == null) {
			return 0;
		}
		return delegate.getY();
	}
	
	public int getDeltaX() {
		if (delegate == null) {
			return 0;
		}
		return delegate.getDeltaX();
	}
	
	public int getDeltaY() {
		if (delegate == null) {
			return 0;
		}
		return delegate.getDeltaY();
	}
	
	public int getClickCount() {
		if (delegate == null) {
			return 0;
		}
		return delegate.getClickCount();
	}
	
	public boolean isPressed(MouseButton btn) throws NullPointerException {
		if (delegate == null) {
			return false;
		}
		return delegate.isPressed(btn);
	}
	
	public boolean isReleased(MouseButton btn) throws NullPointerException {
		if (delegate == null) {
			return false;
		}
		return delegate.isReleased(btn);
	}
	
	public boolean isTriggered(MouseButton btn) throws NullPointerException {
		if (delegate == null) {
			return false;
		}
		return delegate.isTriggered(btn);
	}
	
	public PComponent getComponentAtMouse() {
		if (delegate == null) {
			return null;
		}
		if (!compAtMouseCacheValid) {
			compAtMouseCache = PCompUtil.getComponentAt(root, delegate.getX(), delegate.getY());
			compAtMouseCacheValid = true;
		}
		return compAtMouseCache;
	}
	
	public PCursor getCursorDefault() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorDefault();
	}
	
	public PCursor getCursorHand() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorHand();
	}
	
	public PCursor getCursorText() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorText();
	}
	
	public PCursor getCursorScroll() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorScroll();
	}
	
	public PCursor getCursorBusy() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorBusy();
	}
	
	public PCursor getCurrentCursor() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCurrentCursor();
	}
	
	public void addObs(PMouseObs obs) {
		System.out.println("DelegatePMouse.addObs()");
		obsList.add(obs);
	}
	
	public void removeObs(PMouseObs obs) {
		System.out.println("DelegatePMouse.removeObs()");
		obsList.remove(obs);
	}
}