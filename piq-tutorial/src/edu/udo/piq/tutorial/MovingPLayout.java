package edu.udo.piq.tutorial;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;
import edu.udo.piq.tools.AbstractMapPLayout;

public class MovingPLayout extends AbstractMapPLayout {
	
	protected final PTimer timer = new PTimer(() -> {
			MovingPLayout.this.time++;
			invalidate();
		});
	private final PComponentObs ownerObs = new PComponentObs() {
		public void onRootChanged(PComponent component, PRoot currentRoot) {
			if (currentRoot == null) {
				timer.stop();
			} else {
				timer.start();
			}
		}
	};
	private PComponent content;
	private int time;
	private int radius = 100;
	
	public MovingPLayout(PComponent component) {
		super(component);
		component.addObs(ownerObs);
		timer.setOwner(component);
		timer.setRepeating(true);
		timer.setDelay(1);
	}
	
	protected void onChildAdded(PComponent child, Object constraint) {
		content = child;
		timer.start();
	}
	
	protected void onChildRemoved(PComponent child, Object constraint) {
		content = null;
		timer.stop();
		invalidate();
	}
	
	public void dispose() {
		getOwner().removeObs(ownerObs);
		timer.stop();
		timer.setOwner(null);
	}
	
	public void setRadius(int value) {
		radius = value;
		invalidate();
	}
	
	public int getRadius() {
		return radius;
	}
	
	public void setMoveUpdateDelay(int value) {
		timer.setDelay(value);
	}
	
	public int getMoveUpdateDelay() {
		return timer.getDelay();
	}
	
	protected boolean canAdd(PComponent component, Object constraint) {
		return constraint == null;
	}
	
	protected void layOutInternal() {
		if (content == null) {
			return;
		}
		PBounds ob = getOwner().getBounds();
		int centerX = ob.getCenterX();
		int centerY = ob.getCenterY();
		
		int degree = time % (360 * timer.getDelay());
		double rad = Math.toRadians(degree);
		int circleX = centerX + (int) (radius * Math.cos(rad));
		int circleY = centerY + (int) (radius * Math.sin(rad));
		
		int childW = prefSize.getWidth();
		int childH = prefSize.getHeight();
		int childX = circleX - childW / 2;
		int childY = circleY - childH / 2;
		
		setChildBounds(content, childX, childY, childW, childH);
	}
	
	protected void onInvalidated() {
		if (content == null) {
			prefSize.set(PSize.ZERO_SIZE);
		} else {
			prefSize.set(getPreferredSizeOf(content));
		}
	}
	
}