package edu.udo.piq.components.util;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PComponent;
import edu.udo.piq.PComponentAction;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.components.util.PKeyInput.KeyInputType;
import edu.udo.piq.components.util.PKeyInput.OptionalCondition;

public class PKeyInputMap {
	
	private final Map<Object, InputReactionTuple> inputMap = new HashMap<>();
	private final PComponent owner;
	
	public PKeyInputMap(PComponent component) {
		owner = component;
		owner.addObs(new PKeyboardObs() {
			public void onKeyPressed(PKeyboard keyboard, Key key) {
				PKeyInputMap.this.onEvent(keyboard, key, KeyInputType.PRESS);
			}
			public void onKeyTriggered(PKeyboard keyboard, Key key) {
				PKeyInputMap.this.onEvent(keyboard, key, KeyInputType.TRIGGER);
			}
			public void onKeyReleased(PKeyboard keyboard, Key key) {
				PKeyInputMap.this.onEvent(keyboard, key, KeyInputType.RELEASE);
			}
		});
	}
	
	public void defineInput(Object identifier, PKeyInput input, PComponentAction reaction) {
		InputReactionTuple tuple = new InputReactionTuple(input, reaction);
		inputMap.put(identifier, tuple);
	}
	
	public void undefine(Object identifier) {
		inputMap.remove(identifier);
	}
	
	public PComponent getOwner() {
		return owner;
	}
	
	protected void onEvent(PKeyboard kb, Key key, KeyInputType inputType) {
		PComponent owner = getOwner();
		if (!owner.hasFocus()) {
			return;
		}
		for (InputReactionTuple tuple : inputMap.values()) {
			if (canTrigger(tuple.input, kb, key, inputType)) {
				tuple.reaction.act(owner);
			}
		}
	}
	
	protected boolean canTrigger(PKeyInput input, PKeyboard kb, Key key, KeyInputType inputType) {
		if (input.getKey() != key || input.getKeyInputType() != inputType) {
			return false;
		}
		for (int i = 0; i < input.getModifierCount(); i++) {
			if (!kb.isModifierToggled(input.getModifier(i))) {
				return false;
			}
		}
		OptionalCondition cond = input.getOptionalCondition();
		if (cond != null && !cond.canTrigger(getOwner())) {
			return false;
		}
		return true;
	}
	
	protected static class InputReactionTuple {
		
		public final PKeyInput input;
		public final PComponentAction reaction;
		
		public InputReactionTuple(PKeyInput input, PComponentAction runnable) {
			this.input = input;
			this.reaction = runnable;
		}
		
	}
	
}