package edu.udo.piq.tests.styles;

import edu.udo.piq.PBounds;
import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.PSize;
import edu.udo.piq.components.containers.PPanel;
import edu.udo.piq.style.MutablePStyleComponent;
import edu.udo.piq.util.PiqUtil;

public class SwingStylePPanel extends MutablePStyleComponent implements SwingPStyle {
	
	private static final PColor COLOR_FOCUS = PColor.WHITE.mult1(0.30);
	private static final PColor COLOR_UNFOCUS = PColor.WHITE.mult1(0.15);
	
	protected final PFocusObs focusObs = new PFocusObs() {
		@Override
		public void onFocusGained(PComponent oldOwner, PComponent newOwner) {
			SwingStylePPanel.this.onFocusChanged(newOwner.getRoot());
		}
		@Override
		public void onFocusLost(PComponent oldOwner) {
			SwingStylePPanel.this.onFocusChanged(oldOwner.getRoot());
		}
	};
	protected PPanel lastFocusedPanel;
	
	public void onFocusChanged(PRoot root) {
		PComponent focusOwner = root == null ? null : root.getFocusOwner();
		if (focusOwner == null) {
			if (lastFocusedPanel != null) {
				PiqUtil.fireReRenderEventFor(lastFocusedPanel);
				lastFocusedPanel = null;
			}
			return;
		}
		PPanel curFocusPnl = focusOwner.getFirstAncestorOfType(PPanel.class);
		if (curFocusPnl != lastFocusedPanel) {
			if (lastFocusedPanel != null) {
				PiqUtil.fireReRenderEventFor(lastFocusedPanel);
			}
			lastFocusedPanel = curFocusPnl;
			if (lastFocusedPanel != null) {
				PiqUtil.fireReRenderEventFor(lastFocusedPanel);
			}
		}
	}
	
	@Override
	public boolean fillsAllPixels(PComponent component) {
		return true;
	}
	
	@Override
	public PSize getPreferredSize(PComponent component) {
		return component.getDefaultPreferredSize();
	}
	
	@Override
	public void render(PRenderer renderer, PComponent component) {
		PBounds bnds = component.getBoundsWithoutBorder();
		if (component == lastFocusedPanel) {
			renderer.setColor(COLOR_FOCUS);
		} else {
			renderer.setColor(COLOR_UNFOCUS);
		}
		renderer.drawQuad(bnds);
	}
	
	@Override
	public void onAddedToRoot(PRoot root) {
		lastFocusedPanel = null;
		root.addObs(focusObs);
	}
	
	@Override
	public void onRemovedFromRoot(PRoot root) {
		root.removeObs(focusObs);
		lastFocusedPanel = null;
	}
	
}