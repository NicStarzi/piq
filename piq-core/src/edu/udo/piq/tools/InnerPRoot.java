package edu.udo.piq.tools;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;

public class InnerPRoot extends AbstractPLayoutOwner {
	
	protected final DelegatePRoot root = new DelegatePRoot();
	
	public InnerPRoot() {
		root.addObs(new PComponentObs() {
			public void onBoundsChanged(PComponent component) {
				firePreferredSizeChangedEvent();
			}
		});
		root.setRepresentative(this);
	}
	
	public DelegatePRoot getDelegateRoot() {
		return root;
	}
	
	public PSize getDefaultPreferredSize() {
		return root.getBounds();
	}
	
	public void defaultRender(PRenderer renderer) {
		root.renderAll(renderer);
	}
	
	protected void onRootChanged(PRoot oldRoot) {
		root.setDelegateRoot(getRoot());
	}
	
	protected void onThisLaidOut(Object constraint) {
		root.setBounds(getBounds());
		root.reLayOutTheEntireGui();
	}
	
}