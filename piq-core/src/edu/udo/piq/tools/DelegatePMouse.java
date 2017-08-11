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
		@Override
		public void onMouseMoved(PMouse mouse) {
			compAtMouseCacheValid = false;
			obsList.fireEvent((obs) -> obs.onMouseMoved(DelegatePMouse.this));
		}
		@Override
		public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
			obsList.fireEvent((obs) -> obs.onButtonPressed(DelegatePMouse.this, btn, clickCount));
		}
		@Override
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			obsList.fireEvent((obs) -> obs.onButtonTriggered(DelegatePMouse.this, btn));
		}
		@Override
		public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
			obsList.fireEvent((obs) -> obs.onButtonReleased(DelegatePMouse.this, btn, clickCount));
		}
		@Override
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
	
	@Override
	public int getX() {
		if (delegate == null) {
			return 0;
		}
		return delegate.getX();
	}
	
	@Override
	public int getY() {
		if (delegate == null) {
			return 0;
		}
		return delegate.getY();
	}
	
	@Override
	public int getDeltaX() {
		if (delegate == null) {
			return 0;
		}
		return delegate.getDeltaX();
	}
	
	@Override
	public int getDeltaY() {
		if (delegate == null) {
			return 0;
		}
		return delegate.getDeltaY();
	}
	
	@Override
	public boolean isPressed(MouseButton btn) throws NullPointerException {
		if (delegate == null) {
			return false;
		}
		return delegate.isPressed(btn);
	}
	
	@Override
	public boolean isReleased(MouseButton btn) throws NullPointerException {
		if (delegate == null) {
			return false;
		}
		return delegate.isReleased(btn);
	}
	
	@Override
	public boolean isTriggered(MouseButton btn) throws NullPointerException {
		if (delegate == null) {
			return false;
		}
		return delegate.isTriggered(btn);
	}
	
	@Override
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
	
	@Override
	public PCursor getCursorDefault() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorDefault();
	}
	
	@Override
	public PCursor getCursorHand() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorHand();
	}
	
	@Override
	public PCursor getCursorText() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorText();
	}
	
	@Override
	public PCursor getCursorScroll() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorScroll();
	}
	
	@Override
	public PCursor getCursorBusy() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCursorBusy();
	}
	
	@Override
	public PCursor getCurrentCursor() {
		if (delegate == null) {
			return null;
		}
		return delegate.getCurrentCursor();
	}
	
	@Override
	public boolean isCursorSupported(PCursor cursor) {
		return delegate.isCursorSupported(cursor);
	}
	
	@Override
	public void addObs(PMouseObs obs) {
		System.out.println("DelegatePMouse.addObs()");
		obsList.add(obs);
	}
	
	@Override
	public void removeObs(PMouseObs obs) {
		System.out.println("DelegatePMouse.removeObs()");
		obsList.remove(obs);
	}
}