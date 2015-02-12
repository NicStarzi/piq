package edu.udo.piq.layouts;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.PRootOverlay;
import edu.udo.piq.PSize;
import edu.udo.piq.tools.AbstractPLayout;

public class PRootLayout extends AbstractPLayout {
	
	private final PRoot owner;
	private final List<PComponent> components = Arrays.asList(new PComponent[Constraint.values().length]);
	
	public PRootLayout(PRoot owner) {
		super(owner);
		this.owner = owner;
		addObs(new PLayoutObs() {
			public void childAdded(PLayout layout, PComponent child, Object constraint) {
				Constraint pos = (Constraint) constraint;
				if (pos == Constraint.OVERLAY && !(child instanceof PRootOverlay)) {
					throw new IllegalArgumentException("child="+child+", constraint="+constraint);
				}
				components.set(pos.ordinal(), child);
			}
			public void childRemoved(PLayout layout, PComponent child, Object constraint) {
				Constraint pos = (Constraint) constraint;
				components.set(pos.ordinal(), null);
			}
		});
	}
	
	protected boolean canAdd(PComponent cmp, Object constraint) {
		return constraint != null && constraint instanceof Constraint && getChildForConstraint(constraint) == null;
	}
	
	public void layOut() {
		PBounds ob = getOwnerBounds();
		int x = ob.getX();
		int y = ob.getY();
		int w = ob.getWidth();
		int h = ob.getHeight();
		for (PComponent child : getChildren()) {
			setChildBounds(child, x, y, w, h);
		}
	}
	
	public PSize getPreferredSize() {
		return owner.getBounds();
	}
	
	public PRootOverlay getOverlay() {
		return (PRootOverlay) components.get(Constraint.OVERLAY.ordinal());
	}
	
	public PComponent getBody() {
		return components.get(Constraint.BODY.ordinal());
	}
	
	public PComponent getChildAt(int x, int y) {
		PComponent body = getBody();
		if (getChildBounds(body).contains(x, y)) {
			return body;
		}
		return null;
	}
	
	public Collection<PComponent> getChildren() {
		return components;
	}
	
	public static enum Constraint {
		BODY,
		OVERLAY,
		;
	}
	
}