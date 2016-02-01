package edu.udo.piq.tutorial;

import edu.udo.piq.PBounds;
import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentObs;
import edu.udo.piq.PLayoutObs;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.PTimer;
import edu.udo.piq.PTimerCallback;
import edu.udo.piq.tools.AbstractMapPLayout;
import edu.udo.piq.tools.MutablePSize;

public class MovingPLayout extends AbstractMapPLayout {
	
	protected final MutablePSize prefSize = new MutablePSize();
	protected final PTimer timer = new PTimer(new PTimerCallback() {
		public void onTimerEvent() {
			time++;
			fireInvalidateEvent();
		}
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
		addObs(new PLayoutObs() {
			public void onChildAdded(PReadOnlyLayout layout, PComponent child, Object constraint) {
				content = child;
			}
			public void onChildRemoved(PReadOnlyLayout layout, PComponent child, Object constraint) {
				content = null;
			}
		});
		component.addObs(ownerObs);
		timer.setOwner(component);
		timer.setRepeating(true);
		timer.setDelay(1);
	}
	
	public void dispose() {
		getOwner().removeObs(ownerObs);
		timer.stop();
		timer.setOwner(null);
	}
	
	public void setRadius(int value) {
		radius = value;
		fireInvalidateEvent();
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
	
	public void layOut() {
		PBounds ob = getOwner().getBounds();
		int centerX = ob.getCenterX();
		int centerY = ob.getCenterY();
		
		int degree = time % (360 * timer.getDelay());
		double rad = Math.toRadians(degree);
		int circleX = centerX + (int) (radius * Math.cos(rad));
		int circleY = centerY + (int) (radius * Math.sin(rad));
		
		PSize childSize = getPreferredSizeOf(content);
		int childW = childSize.getWidth();
		int childH = childSize.getHeight();
		int childX = circleX - childW / 2;
		int childY = circleY - childH / 2;
		
		setChildBounds(content, childX, childY, childW, childH);
	}
	
	public PSize getPreferredSize() {
		if (content == null) {
			return PSize.ZERO_SIZE;
		} else {
			return getPreferredSizeOf(content);
		}
	}
	
}