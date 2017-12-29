package edu.udo.piq.tools;

import edu.udo.piq.PFocusTraversal;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PRoot;

public class AbstractPFocusTraversal implements PFocusTraversal {
	
	protected final PKeyboardObs keyObs = new PKeyboardObs() {
		@Override
		public void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
			AbstractPFocusTraversal.this.onKeyTriggered(keyboard, key);
		}
		@Override
		public void onKeyPressed(PKeyboard keyboard, ActualKey key) {
			AbstractPFocusTraversal.this.onKeyPressed(keyboard, key);
		}
		@Override
		public void onKeyReleased(PKeyboard keyboard, ActualKey key) {
			AbstractPFocusTraversal.this.onKeyReleased(keyboard, key);
		}
	};
	protected PRoot curRoot;
	
	@Override
	public void install(PRoot root) {
		curRoot = root;
		curRoot.addObs(keyObs);
	}
	
	@Override
	public void uninstall(PRoot root) {
		curRoot.removeObs(keyObs);
		curRoot = null;
	}
	
	protected void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
	}
	
	protected void onKeyPressed(PKeyboard keyboard, ActualKey key) {
	}
	
	protected void onKeyReleased(PKeyboard keyboard, ActualKey key) {
	}
}