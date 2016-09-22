package edu.udo.piq.components.textbased;

import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PTimer;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.components.collections.PModelIndex;
import edu.udo.piq.components.collections.PSelection;
import edu.udo.piq.components.collections.PSelectionObs;
import edu.udo.piq.util.PCompUtil;

public class PCaretRenderTimer {
	
	public static final int DEFAULT_FOCUS_RENDER_TOGGLE_TIMER_DELAY = 6;
	
	protected final PSelectionObs selectionObs = new PSelectionObs() {
		public void onSelectionAdded(PSelection selection, PModelIndex index) {
			PCaretRenderTimer.this.onSelectionAdded(selection, index);
		}
		public void onSelectionRemoved(PSelection selection, PModelIndex index) {
			PCaretRenderTimer.this.onSelectionRemoved(selection, index);
		}
		public void onLastSelectedChanged(PSelection selection, 
				PModelIndex prevLastSelected, PModelIndex newLastSelected) 
		{
			PCaretRenderTimer.this.onLastSelectedChanged(selection, 
					prevLastSelected, newLastSelected);
		}
	};
	protected final PFocusObs focusObs = new PFocusObs() {
		public void onFocusLost(PComponent oldOwner) {
			PCaretRenderTimer.this.onFocusLost();
		}
		public void onFocusGained(PComponent oldOwner, PComponent newOwner) {
			PCaretRenderTimer.this.onFocusGained();
		}
	};
	protected final PTextComponent owner;
	protected final PTimer focusToggleTimer;
	protected int focusRenderToggleTimer;
	protected boolean focusRenderToggle;
	
	public PCaretRenderTimer(PTextComponent component) {
		owner = component;
		focusToggleTimer = new PTimer(owner, this::onTimerUpdate);
	}
	
	public PTimer getTimer() {
		return focusToggleTimer;
	}
	
	public PSelectionObs getSelectionObs() {
		return selectionObs;
	}
	
	public PFocusObs getFocusObs() {
		return focusObs;
	}
	
	public boolean isFocusRender() {
		return focusRenderToggle;
	}
	
	protected void onSelectionAdded(PSelection selection, PModelIndex index) {
		resetFocusRenderTimer();
	}
	
	protected void onSelectionRemoved(PSelection selection, PModelIndex index) {
		resetFocusRenderTimer();
	}
	
	protected void onLastSelectedChanged(PSelection selection, 
			PModelIndex prevLastSelected, PModelIndex newLastSelected) 
	{
		resetFocusRenderTimer();
	}
	
	protected void onFocusLost() {
		focusToggleTimer.stop();
	}
	
	protected void onFocusGained() {
		resetFocusRenderTimer();
		focusToggleTimer.setRepeating(true);
		focusToggleTimer.setDelay(DEFAULT_FOCUS_RENDER_TOGGLE_TIMER_DELAY);
		focusToggleTimer.start();
	}
	
	protected void onTimerUpdate(double deltaTime) {
		PTextSelection selection = owner.getSelection();
		PListIndex selectionFrom = selection.getLowestSelectedIndex();
		PListIndex selectionTo = selection.getHighestSelectedIndex();
		if (owner.hasFocus() && selectionFrom != null 
				&& selectionFrom.equals(selectionTo)) 
		{
			focusRenderToggleTimer += 1;
			if (focusRenderToggleTimer >= DEFAULT_FOCUS_RENDER_TOGGLE_TIMER_DELAY) {
				focusRenderToggleTimer = 0;
				focusRenderToggle = !focusRenderToggle;
				PCompUtil.fireReRenderEventFor(owner);
			}
		}
	}
	
	protected void resetFocusRenderTimer() {
		focusRenderToggle = true;
		focusRenderToggleTimer = 0;
		PCompUtil.fireReRenderEventFor(owner);
	}
	
}