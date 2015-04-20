package edu.udo.piq.components.util;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.components.util.PInput.KeyInputType;

public class PInputMap {
	
	private final Map<Object, InputReactionTuple> inputMap = new HashMap<>();
	private final PComponent owner;
	
	public PInputMap(PComponent component) {
		owner = component;
		owner.addObs(new PKeyboardObs() {
			public void keyPressed(PKeyboard keyboard, Key key) {
				if (!owner.hasFocus()) {
					return;
				}
				for (InputReactionTuple tuple : inputMap.values()) {
					if (tuple.input.getKeyInputType() == KeyInputType.PRESS
							&& tuple.input.getTriggerKey() == key 
							&& tuple.input.canBeTriggered(keyboard)) {
						
						tuple.reaction.run();
					}
				}
			}
			public void keyTriggered(PKeyboard keyboard, Key key) {
				if (!owner.hasFocus()) {
					return;
				}
				for (InputReactionTuple tuple : inputMap.values()) {
					if (tuple.input.getKeyInputType() == KeyInputType.TRIGGER 
							&& tuple.input.getTriggerKey() == key 
							&& tuple.input.canBeTriggered(keyboard)) {
						
						tuple.reaction.run();
					}
				}
			}
			public void keyReleased(PKeyboard keyboard, Key key) {
				if (!owner.hasFocus()) {
					return;
				}
				for (InputReactionTuple tuple : inputMap.values()) {
					if (tuple.input.getKeyInputType() == KeyInputType.RELEASE 
							&& tuple.input.getTriggerKey() == key 
							&& tuple.input.canBeTriggered(keyboard)) {
						
						tuple.reaction.run();
					}
				}
			}
		});
	}
	
	public void defineInput(Object identifier, PInput input, Runnable reaction) {
		InputReactionTuple tuple = new InputReactionTuple(input, reaction);
		inputMap.put(identifier, tuple);
	}
	
	public void undefine(Object identifier) {
		inputMap.remove(identifier);
	}
	
	protected static class InputReactionTuple {
		
		public final PInput input;
		public final Runnable reaction;
		
		public InputReactionTuple(PInput input, Runnable runnable) {
			this.input = input;
			this.reaction = runnable;
		}
		
	}
	
}