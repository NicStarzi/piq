package edu.udo.piq.tutorial;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PDnDSupport;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.layouts.PFreeLayout;
import edu.udo.piq.layouts.PFreeLayout.FreeConstraint;
import edu.udo.piq.tools.AbstractPLayoutOwner;

public class DnDArea extends AbstractPLayoutOwner {
	
	private static final int DRAG_DISTANCE = 30;
	
	private final DnDAreaSupport dndSup = new DnDAreaSupport();
	private int dragX, dragY;
	private boolean dragSelected;
	private int dropX, dropY, dropFx, dropFy;
	private boolean dropHighlight = false;
	
	public DnDArea() {
		super();
		setLayout(new PFreeLayout(this));
		
		addObs(new PMouseObs() {
			public void onButtonTriggered(PMouse mouse, MouseButton btn) {
				if (btn != MouseButton.LEFT) {
					return;
				}
				if (!isMouseOverThisOrChild()) {
					return;
				}
				dragX = mouse.getX();
				dragY = mouse.getY();
				dragSelected = dndSup.canDrag(DnDArea.this, dragX, dragY);
			}
			public void onButtonReleased(PMouse mouse, MouseButton btn) {
				if (btn != MouseButton.LEFT) {
					return;
				}
				dragSelected = false;
			}
			public void onMouseMoved(PMouse mouse) {
				if (dragSelected) {
					int mx = mouse.getX();
					int my = mouse.getY();
					if (Math.abs(mx - dragX) > DRAG_DISTANCE
							|| Math.abs(my - dragY) > DRAG_DISTANCE) 
					{
						tryDnd(dragX, dragY);
					}
				}
			}
		});
	}
	
	public PFreeLayout getLayout() {
		return (PFreeLayout) super.getLayout();
	}
	
	public void addChild(PComponent component, int x, int y) {
		getLayout().addChild(component, new FreeConstraint(x, y));
	}
	
	public PDnDSupport getDragAndDropSupport() {
		return dndSup;
	}
	
	public void defaultRender(PRenderer renderer) {
		renderer.setColor(PColor.WHITE);
		renderer.drawQuad(getBounds());
		
		if (dropHighlight) {
			renderer.setColor(PColor.YELLOW);
			renderer.drawQuad(dropX, dropY, dropFx, dropFy);
		}
	}
	
	public boolean defaultFillsAllPixels() {
		return true;
	}
	
	public void showDropLoc(int x, int y, int w, int h) {
		dropX = x;
		dropY = y;
		dropFx = dropX + w;
		dropFy = dropY + h;
		dropHighlight = true;
		fireReRenderEvent();
	}
	
	public void hideDropLoc() {
		dropX = 0;
		dropY = 0;
		dropFx = 0;
		dropFy = 0;
		dropHighlight = false;
		fireReRenderEvent();
	}
	
	private void tryDnd(int x, int y) {
		if (dndSup.canDrag(this, x, y)) {
			dndSup.startDrag(this, x, y);
		}
	}
	
}