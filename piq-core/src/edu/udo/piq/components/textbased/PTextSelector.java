package edu.udo.piq.components.textbased;

import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PMouseObs;
import edu.udo.piq.components.collections.PListIndex;
import edu.udo.piq.util.PCompUtil;

public class PTextSelector {
	
	protected final PTextComponent owner;
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
	protected PListIndex pressedIndex;
	
	public PTextSelector(PTextComponent component) {
		owner = component;
	}
	
	public PMouseObs getMouseObs() {
		return mouseObs;
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
				char charAtIndex = text.charAt(from);
				boolean isWS = Character.isWhitespace(charAtIndex);
				while (from > 0 && Character.isWhitespace(text.charAt(from - 1)) == isWS) {
					from--;
				}
				while (to < text.length() && Character.isWhitespace(text.charAt(to)) == isWS) {
					to++;
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