package edu.udo.piq.components.textbased;

import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.components.collections.list.PListIndex;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.util.PiqUtil;

public class PTextSelector {
	
	protected final PMouseObs mouseObs = new PMouseObs() {
		@Override
		public void onMouseMoved(PMouse mouse) {
			PTextSelector.this.onMouseMoved(mouse);
		}
		@Override
		public void onButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
			PTextSelector.this.onMouseButtonPressed(mouse, btn, clickCount);
		}
		@Override
		public void onButtonReleased(PMouse mouse, MouseButton btn, int clickCount) {
			PTextSelector.this.onMouseButtonReleased(mouse, btn);
		}
	};
	protected final PFocusObs focusObs = new PFocusObs() {
		@Override
		public void onFocusGained(PComponent oldOwner, PComponent newOwner) {
			PTextSelector.this.onFocusGained();
		}
	};
	protected PTextComponent owner;
	protected PListIndex pressedIndex;
	
	public void setOwner(PTextComponent component) {
		if (getOwner() != null) {
			getOwner().removeObs(mouseObs);
			getOwner().removeObs(focusObs);
		}
		owner = component;
		if (getOwner() != null) {
			getOwner().addObs(mouseObs);
			getOwner().addObs(focusObs);
		}
	}
	
	public PComponent getOwner() {
		return owner;
	}
	
	public PMouseObs getMouseObs() {
		return mouseObs;
	}
	
	protected void onFocusGained() {
		if (owner.getSelection() == null) {
			return;
		}
		if (!owner.getSelection().hasSelection()) {
			owner.getSelection().addSelection(new PListIndex(0));
		}
	}
	
	protected void onMouseMoved(PMouse mouse) {
		if (mouse.isPressed(MouseButton.LEFT) && pressedIndex != null) {
			int mx = mouse.getX();
			int my = mouse.getY();
			PListIndex index = owner.getTextIndexAt(mx, my);
			if (index == null) {
				return;
			}
			owner.getSelection().addSelection(index);
			owner.takeFocus();
			PiqUtil.fireReRenderEventFor(owner);
		}
	}
	
	protected void onMouseButtonPressed(PMouse mouse, MouseButton btn, int clickCount) {
		if (btn == MouseButton.LEFT && owner.isMouseOver(mouse)) {
			int mx = mouse.getX();
			int my = mouse.getY();
			PListIndex index = owner.getTextIndexAt(mx, my);
			if (index == null) {
				return;
			}
			
			if (clickCount > 1) {
				int from = index.getIndexValue();
				int to = from;
				
				String text = owner.getModel().getText();
				if (from < text.length()) {
					char charAtIndex = text.charAt(from);
					boolean isWS = Character.isWhitespace(charAtIndex);
					while (from > 0 && Character.isWhitespace(text.charAt(from - 1)) == isWS) {
						from--;
					}
					while (to < text.length() && Character.isWhitespace(text.charAt(to)) == isWS) {
						to++;
					}
				} else {
					from = 0;
				}
				owner.getSelection().clearSelection();
				owner.getSelection().addSelection(new PListIndex(from));
				owner.getSelection().addSelection(new PListIndex(to));
			} else {
				pressedIndex = index;
				owner.getSelection().clearSelection();
				owner.getSelection().addSelection(pressedIndex);
			}
			owner.takeFocus();
			PiqUtil.fireReRenderEventFor(owner);
		}
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && pressedIndex != null) {
			pressedIndex = null;
			PiqUtil.fireReRenderEventFor(owner);
		}
	}
	
}