package edu.udo.piq.components.containers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.ImmutablePBounds;
import edu.udo.piq.util.ThrowException;

public class PSizeTestArea extends AbstractPLayoutOwner {
	
	public static final int ANCHOR_WIDTH = 10;
	public static final int ANCHOR_HEIGHT = 10;
	public static final PColor DEFAULT_BACKGROUND_COLOR = PColor.WHITE;
	
	private PColor backgroundColor = DEFAULT_BACKGROUND_COLOR;
	private PComponent content;
	private int contentX = ANCHOR_WIDTH;
	private int contentY = ANCHOR_HEIGHT;
	private int contentFx = 100;
	private int contentFy = 100;
	
	private Anchor dragAnchor = null;
	private int dragOffsetX;
	private int dragOffsetY;
	
	public PSizeTestArea() {
		super();
		setLayout(new PFreeLayout(this));
		
		addObs(new PMouseObs() {
			public void onMouseMoved(PMouse mouse) {
				PSizeTestArea.this.onMouseMoved(mouse);
			}
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				PSizeTestArea.this.onMouseTriggerd(mouse, btn);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				PSizeTestArea.this.onMouseReleased(mouse, btn);
			}
		});
	}
	
	protected void onMouseMoved(PMouse mouse) {
		if (dragAnchor != null) {
			PBounds ownBounds = getBounds();
			int x = ownBounds.getX();
			int y = ownBounds.getY();
			int w = ownBounds.getWidth();
			int h = ownBounds.getHeight();
			
			int ax = x + dragAnchor.getX(this);
			int ay = y + dragAnchor.getY(this);
			int newAx = mouse.getX() - dragOffsetX;
			int newAy = mouse.getY() - dragOffsetY;
			if (ax != newAx || ay != newAy) {
				dragAnchor.setPosition(this, newAx, newAy, w, h);
			}
		}
	}
	
	protected void onMouseTriggerd(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT) {
			int mx = mouse.getX();
			int my = mouse.getY();
			
			dragAnchor = getAnchorAt(mx, my);
			if (dragAnchor != null) {
				dragOffsetX = mx - dragAnchor.getX(this);
				dragOffsetY = my - dragAnchor.getY(this);
			}
		}
	}
	
	protected Anchor getAnchorAt(int x, int y) {
		for (Anchor anchor : Anchor.ALL) {
			if (getAnchorBounds(anchor).contains(x, y)) {
				return anchor;
			}
		}
		return null;
	}
	
	protected PBounds getAnchorBounds(Anchor anchor) {
		PBounds ownBounds = getBounds();
		int x = ownBounds.getX();
		int y = ownBounds.getY();
		
		int ax = x + anchor.getX(this);
		int ay = y + anchor.getY(this);
		return new ImmutablePBounds(ax, ay, ANCHOR_WIDTH, ANCHOR_HEIGHT);
	}
	
	protected void onMouseReleased(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT) {
			dragAnchor = null;
		}
	}
	
	protected PFreeLayout getLayoutInternal() {
		return (PFreeLayout) super.getLayout();
	}
	
	protected FreeConstraint createContentConstraint() {
		return new FreeConstraint(
				getContentX(), getContentY(), 
				getContentWidth(), getContentHeight());
	}
	
	protected void updateContentConstraint() {
		if (getContent() == null) {
			return;
		}
		getLayoutInternal().updateConstraint(getContent(), 
				getContentX(), getContentY(), 
				getContentWidth(), getContentHeight());
	}
	
	public void setContent(PComponent component) {
		if (getContent() != null) {
			getLayoutInternal().removeChild(getContent());
		}
		content = component;
		if (getContent() != null) {
			getLayoutInternal().addChild(getContent(), 
					createContentConstraint());
		}
	}
	
	public PComponent getContent() {
		return content;
	}
	
	public void setContentX(int value) {
		if (contentX == value) {
			return;
		}
		contentX = value;
		if (contentFx < contentX) {
			contentFx = contentX;
		}
		updateContentConstraint();
		fireReRenderEvent();
	}
	
	public int getContentX() {
		return contentX;
	}
	
	public void setContentY(int value) {
		if (contentY == value) {
			return;
		}
		contentY = value;
		if (contentFy < contentY) {
			contentFy = contentY;
		}
		updateContentConstraint();
		fireReRenderEvent();
	}
	
	public int getContentY() {
		return contentY;
	}
	
	public void setContentFinalX(int value) {
		if (contentFx == value) {
			return;
		}
		contentFx = value;
		if (contentX > contentFx) {
			contentX = contentFx;
		}
		updateContentConstraint();
		fireReRenderEvent();
	}
	
	public int getContentFinalX() {
		return contentFx;
	}
	
	public void setContentFinalY(int value) {
		if (contentFy == value) {
			return;
		}
		contentFy = value;
		if (contentY > contentFy) {
			contentY = contentFy;
		}
		updateContentConstraint();
		fireReRenderEvent();
	}
	
	public int getContentFinalY() {
		return contentFy;
	}
	
	public int getContentWidth() {
		return contentFx - contentX;
	}
	
	public int getContentHeight() {
		return contentFy - contentY;
	}
	
	public void setBackgroundColor(PColor value) {
		ThrowException.ifNull(value, "value == null");
		if (getBackgroundColor() != value) {
			backgroundColor = value;
			fireReRenderEvent();
		}
	}
	
	public PColor getBackgroundColor() {
		return backgroundColor;
	}
	
	public void defaultRender(PRenderer renderer) {
		PBounds bounds = getBounds();
		int x = bounds.getX();
		int y = bounds.getY();
		
		renderer.setColor(getBackgroundColor());
		renderer.drawQuad(x, y, bounds.getFinalX(), bounds.getFinalY());
		
		for (Anchor anchor : Anchor.ALL) {
			if (anchor == dragAnchor) {
				renderer.setColor(PColor.RED);
			} else {
				renderer.setColor(PColor.BLACK);
			}
			int ax = x + anchor.getX(this);
			int ay = y + anchor.getY(this);
			renderAnchor(renderer, ax, ay);
		}
	}
	
	protected void renderAnchor(PRenderer renderer, int x, int y) {
		renderer.drawQuad(x, y, x + ANCHOR_WIDTH, y + ANCHOR_HEIGHT);
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	protected static enum Anchor {
		TOP_LEFT {
			public void setPosition(PSizeTestArea area, int x, int y, int w, int h) {
				x = limit(x, 0, w - ANCHOR_WIDTH * 2);
				y = limit(y, 0, h - ANCHOR_HEIGHT * 2);
				
				area.setContentX(x + ANCHOR_WIDTH);
				area.setContentY(y + ANCHOR_HEIGHT);
			}
			public int getX(PSizeTestArea area) {
				return area.getContentX() - ANCHOR_WIDTH;
			}
			public int getY(PSizeTestArea area) {
				return area.getContentY() - ANCHOR_HEIGHT;
			}
		},
		TOP_RIGHT {
			public void setPosition(PSizeTestArea area, int x, int y, int w, int h) {
				x = limit(x, ANCHOR_WIDTH, w - ANCHOR_WIDTH);
				y = limit(y, 0, h - ANCHOR_HEIGHT * 2);
				
				area.setContentFinalX(x - 1);
				area.setContentY(y + ANCHOR_HEIGHT);
			}
			public int getX(PSizeTestArea area) {
				return area.getContentFinalX() + 1;
			}
			public int getY(PSizeTestArea area) {
				return area.getContentY() - ANCHOR_HEIGHT;
			}
		},
		BOTTOM_LEFT {
			public void setPosition(PSizeTestArea area, int x, int y, int w, int h) {
				x = limit(x, 0, w - ANCHOR_WIDTH * 2);
				y = limit(y, ANCHOR_HEIGHT, h - ANCHOR_HEIGHT);
				
				area.setContentX(x + ANCHOR_WIDTH);
				area.setContentFinalY(y - 1);
			}
			public int getX(PSizeTestArea area) {
				return area.getContentX() - ANCHOR_WIDTH;
			}
			public int getY(PSizeTestArea area) {
				return area.getContentFinalY() + 1;
			}
		},
		BOTTOM_RIGHT {
			public void setPosition(PSizeTestArea area, int x, int y, int w, int h) {
				x = limit(x, ANCHOR_WIDTH, w - ANCHOR_WIDTH);
				y = limit(y, ANCHOR_HEIGHT, h - ANCHOR_HEIGHT);
				
				area.setContentFinalX(x - 1);
				area.setContentFinalY(y - 1);
			}
			public int getX(PSizeTestArea area) {
				return area.getContentFinalX() + 1;
			}
			public int getY(PSizeTestArea area) {
				return area.getContentFinalY() + 1;
			}
		},
		;
		public static final List<Anchor> ALL = 
				Collections.unmodifiableList(Arrays.asList(values()));
		
		public abstract void setPosition(PSizeTestArea area, int x, int y, int w, int h);
		
		public abstract int getX(PSizeTestArea area);
		
		public abstract int getY(PSizeTestArea area);
		
		private static int limit(int value, int min, int max) {
			if (value < min) {
				return min;
			} else if (value > max) {
				return max;
			}
			return value;
		}
	}
	
}