package edu.udo.piq.tutorial;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;
import edu.udo.piq.layouts.AbstractMapPLayout;

public class MovingPLayout extends AbstractMapPLayout {
	
	protected final PTimer timer = new PTimer(MovingPLayout.this::onTimerTick);
	private final PComponentObs ownerObs = new PComponentObs() {
		public void onRootChanged(PComponent component, PRoot currentRoot, PRoot oldRoot) {
			if (currentRoot == null) {
				timer.stop();
			} else {
				timer.start();
			}
		}
	};
	private PComponent content;
	private double time;
	private double radius = 100;
	
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
	
	protected void onTimerTick(double deltaTime) {
		time += deltaTime;
		invalidate();
	}
	
	public void setRadius(int value) {
		radius = value;
		invalidate();
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setMoveUpdateDelay(double value) {
		timer.setDelay(value);
	}
	
	public double getMoveUpdateDelay() {
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
		
		double degree = (time / 10) % 360;
		double rad = Math.toRadians(degree);
		int circleX = centerX + (int) (radius * Math.cos(rad));
		int circleY = centerY + (int) (radius * Math.sin(rad));
		
		int childW = prefSize.getWidth();
		int childH = prefSize.getHeight();
		int childX = circleX - childW / 2;
		int childY = circleY - childH / 2;
		
		setChildCellFilled(content, childX, childY, childW, childH);
	}
	
	protected void onInvalidated() {
		if (content == null) {
			prefSize.set(PSize.ZERO_SIZE);
		} else {
			prefSize.set(getPreferredSizeOf(content));
		}
	}
	
}