package edu.udo.piq.components.textbased;

import edu.udo.piq.PComponent;
import edu.udo.piq.PFocusObs;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.util.PCompUtil;

public class PTextSelector {
	
	protected final PMouseObs mouseObs = new PMouseObs() {
		public void onMouseMoved(PMouse mouse) {
			PTextSelector.this.onMouseMoved(mouse);
		}
		public void onButtonTriggered(PMouse mouse, MouseButton btn) {
			PTextSelector.this.onMouseButtonTriggred(mouse, btn);
		}
		public void onButtonReleased(PMouse mouse, MouseButton btn) {
			PTextSelector.this.onMouseButtonReleased(mouse, btn);
		}
	};
	protected final PFocusObs focusObs = new PFocusObs() {
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
			PCompUtil.fireReRenderEventFor(owner);
		}
	}
	
	protected void onMouseButtonTriggred(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && owner.isMouseOver()) {
			int mx = mouse.getX();
			int my = mouse.getY();
			PListIndex index = owner.getTextIndexAt(mx, my);
			if (index == null) {
				return;
			}
			
			int clickCount = mouse.getClickCount();
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
			PCompUtil.fireReRenderEventFor(owner);
		}
	}
	
	protected void onMouseButtonReleased(PMouse mouse, MouseButton btn) {
		if (btn == MouseButton.LEFT && pressedIndex != null) {
			pressedIndex = null;
			PCompUtil.fireReRenderEventFor(owner);
		}
	}
	
}