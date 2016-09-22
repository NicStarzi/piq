package edu.udo.piq.components.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.components.util.PKeyInput.Condition;
import edu.udo.piq.components.util.PKeyInput.FocusPolicy;
import edu.udo.piq.components.util.PKeyInput.KeyInputType;

public class PKeyInputMap {
	
	private final PComponent owner;
	/**
	 * Lazily initialized in the {@link #defineInput(Object, PKeyInput, Consumer)} 
	 * method. <br>This will usually be a HashMap since the nature of the keys is not known.
	 */
	private Map<Object, InputReactionTuple<?>> inputMap;
	/**
	 * Lazily initialized in the {@link #defineInput(Object, PKeyInput, Consumer)} 
	 * method.
	 */
	private PKeyboardObs keyObs;
	/**
	 * The most permissive {@link FocusPolicy} of all {@link PKeyInput key inputs}. This 
	 * is used to filter out unneeded events before iterating over all known inputs.<br>
	 * This value needs to be updated each time an input is added or removed.<br>
	 */
	private FocusPolicy mostPermissivePlcy = FocusPolicy.NEVER;
	
	public PKeyInputMap(PComponent component) {
		owner = component;
	}
	
	public <OWNER_TYPE extends PComponent> void defineInput(Object identifier, PKeyInput<OWNER_TYPE> input, 
			Consumer<OWNER_TYPE> reaction) 
	{
		InputReactionTuple<OWNER_TYPE> tuple = new InputReactionTuple<>(input, reaction);
		if (inputMap == null) {
			inputMap = new HashMap<>();
		}
		inputMap.put(identifier, tuple);
		
		// Update most permissive focus policy
		FocusPolicy newFocusPlcy = input.getFocusPolicy();
		if (newFocusPlcy != null) {
			if (mostPermissivePlcy.ordinal() < newFocusPlcy.ordinal()) {
				mostPermissivePlcy = newFocusPlcy;
			}
		}
		
		// Register keyboard observer if needed
		if (keyObs == null) {
			keyObs = new PKeyboardObs() {
				public void onKeyPressed(PKeyboard keyboard, Key key) {
					PKeyInputMap.this.onEvent(keyboard, key, KeyInputType.PRESS);
				}
				public void onKeyTriggered(PKeyboard keyboard, Key key) {
					PKeyInputMap.this.onEvent(keyboard, key, KeyInputType.TRIGGER);
				}
				public void onKeyReleased(PKeyboard keyboard, Key key) {
					PKeyInputMap.this.onEvent(keyboard, key, KeyInputType.RELEASE);
				}
			};
			owner.addObs(keyObs);
		}
	}
	
	public void undefine(Object identifier) {
		if (inputMap == null) {
			return;
		}
		inputMap.remove(identifier);
		
		// Update most permissive focus policy
		mostPermissivePlcy = FocusPolicy.ALL.get(FocusPolicy.ALL.size() - 1);
		for (InputReactionTuple<?> tuple : inputMap.values()) {
			FocusPolicy plcy = tuple.input.getFocusPolicy();
			if (mostPermissivePlcy.ordinal() < plcy.ordinal()) {
				mostPermissivePlcy = plcy;
			}
		}
		
		// Unregister keyboard observer if needed
		if (inputMap.isEmpty()) {
			owner.removeObs(keyObs);
			keyObs = null;
		}
	}
	
	public PComponent getOwner() {
		return owner;
	}
	
	protected void onEvent(PKeyboard kb, Key key, KeyInputType inputType) {
		if (inputMap == null) {
			throw new IllegalArgumentException("An event was recorded without having any "
					+ "key input defined. This can only happen when the "+PKeyInputMap.class.getSimpleName()
					+ " class has been sub classed and its methods overwritten incorrectly.");
		}
		PComponent owner = getOwner();
		// Early exit
		if (mostPermissivePlcy == null || !mostPermissivePlcy.canTriggerFor(owner)) {
			return;
		}
		for (InputReactionTuple<?> tuple : inputMap.values()) {
			if (canTrigger(tuple.input, kb, key, inputType)) {
				trigger(tuple.reaction, owner);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <OWNER_TYPE extends PComponent> void trigger(
			Consumer<OWNER_TYPE> action, PComponent owner) 
	{
		action.accept((OWNER_TYPE) owner);
	}
	
	@SuppressWarnings("unchecked")
	protected <OWNER_TYPE extends PComponent> boolean canTrigger(
			PKeyInput<OWNER_TYPE> input, PKeyboard kb, Key key, KeyInputType inputType) 
	{
		if (!input.getFocusPolicy().canTriggerFor(owner)) {
			return false;
		}
		if (input.getKey() != key || input.getKeyInputType() != inputType) {
			return false;
		}
		for (int i = 0; i < input.getModifierCount(); i++) {
			if (!kb.isModifierToggled(input.getModifier(i))) {
				return false;
			}
		}
		Condition<OWNER_TYPE> cond = input.getCondition();
		if (cond != null && !cond.canTrigger((OWNER_TYPE) getOwner())) {
			return false;
		}
		return true;
	}
	
	protected static class InputReactionTuple<COMP_TYPE extends PComponent> {
		
		public final PKeyInput<COMP_TYPE> input;
		public final Consumer<COMP_TYPE> reaction;
		
		public InputReactionTuple(PKeyInput<COMP_TYPE> input, 
				Consumer<COMP_TYPE> action) 
		{
			this.input = input;
			this.reaction = action;
		}
		
	}
	
}
//package edu.udo.piq.components.util;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Consumer;
//
//import edu.udo.piq.PComponent;
//import edu.udo.piq.PKeyboard;
//import edu.udo.piq.PKeyboard.Key;
//import edu.udo.piq.PKeyboardObs;
//import edu.udo.piq.components.util.PKeyInput.Condition;
//import edu.udo.piq.components.util.PKeyInput.FocusPolicy;
//import edu.udo.piq.components.util.PKeyInput.KeyInputType;
//
//public class PKeyInputMap<OWNER_TYPE extends PComponent> {
//	
//	private final OWNER_TYPE owner;
//	/**
//	 * Lazily initialized in the {@link #defineInput(Object, PKeyInput, Consumer)} 
//	 * method. <br>This will usually be a HashMap since the nature of the keys is not known.
//	 */
//	private Map<Object, InputReactionTuple<OWNER_TYPE>> inputMap;
//	/**
//	 * Lazily initialized in the {@link #defineInput(Object, PKeyInput, Consumer)} 
//	 * method.
//	 */
//	private PKeyboardObs keyObs;
//	/**
//	 * The most permissive {@link FocusPolicy} of all {@link PKeyInput key inputs}. This 
//	 * is used to filter out unneeded events before iterating over all known inputs.<br>
//	 * This value needs to be updated each time an input is added or removed.<br>
//	 */
//	private FocusPolicy mostPermissivePlcy = FocusPolicy.NEVER;
//	
//	public PKeyInputMap(OWNER_TYPE component) {
//		owner = component;
//	}
//	
//	public void defineInput(Object identifier, PKeyInput<OWNER_TYPE> input, 
//			Consumer<OWNER_TYPE> reaction) 
//	{
//		InputReactionTuple<OWNER_TYPE> tuple = new InputReactionTuple<>(input, reaction);
//		if (inputMap == null) {
//			inputMap = new HashMap<>();
//		}
//		inputMap.put(identifier, tuple);
//		
//		// Update most permissive focus policy
//		FocusPolicy newFocusPlcy = input.getFocusPolicy();
//		if (newFocusPlcy != null) {
//			if (mostPermissivePlcy.ordinal() < newFocusPlcy.ordinal()) {
//				mostPermissivePlcy = newFocusPlcy;
//			}
//		}
//		
//		// Register keyboard observer if needed
//		if (keyObs == null) {
//			keyObs = new PKeyboardObs() {
//				public void onKeyPressed(PKeyboard keyboard, Key key) {
//					PKeyInputMap.this.onEvent(keyboard, key, KeyInputType.PRESS);
//				}
//				public void onKeyTriggered(PKeyboard keyboard, Key key) {
//					PKeyInputMap.this.onEvent(keyboard, key, KeyInputType.TRIGGER);
//				}
//				public void onKeyReleased(PKeyboard keyboard, Key key) {
//					PKeyInputMap.this.onEvent(keyboard, key, KeyInputType.RELEASE);
//				}
//			};
//			owner.addObs(keyObs);
//		}
//	}
//	
//	public void undefine(Object identifier) {
//		if (inputMap == null) {
//			return;
//		}
//		inputMap.remove(identifier);
//		
//		// Update most permissive focus policy
//		mostPermissivePlcy = FocusPolicy.ALL.get(FocusPolicy.ALL.size() - 1);
//		for (InputReactionTuple<?> tuple : inputMap.values()) {
//			FocusPolicy plcy = tuple.input.getFocusPolicy();
//			if (mostPermissivePlcy.ordinal() < plcy.ordinal()) {
//				mostPermissivePlcy = plcy;
//			}
//		}
//		
//		// Unregister keyboard observer if needed
//		if (inputMap.isEmpty()) {
//			owner.removeObs(keyObs);
//			keyObs = null;
//		}
//	}
//	
//	public OWNER_TYPE getOwner() {
//		return owner;
//	}
//	
//	protected void onEvent(PKeyboard kb, Key key, KeyInputType inputType) {
//		if (inputMap == null) {
//			throw new IllegalArgumentException("An event was recorded without having any "
//					+ "key input defined. This can only happen when the "+PKeyInputMap.class.getSimpleName()
//					+ " class has been sub classed and its methods overwritten incorrectly.");
//		}
//		OWNER_TYPE owner = getOwner();
//		// Early exit
//		if (mostPermissivePlcy == null || !mostPermissivePlcy.canTriggerFor(owner)) {
//			return;
//		}
//		for (InputReactionTuple<OWNER_TYPE> tuple : inputMap.values()) {
//			if (canTrigger(tuple.input, kb, key, inputType)) {
//				tuple.reaction.accept(owner);
//			}
//		}
//	}
//	
//	protected boolean canTrigger(PKeyInput<OWNER_TYPE> input, PKeyboard kb, Key key, KeyInputType inputType) {
//		if (!input.getFocusPolicy().canTriggerFor(owner)) {
//			return false;
//		}
//		if (input.getKey() != key || input.getKeyInputType() != inputType) {
//			return false;
//		}
//		for (int i = 0; i < input.getModifierCount(); i++) {
//			if (!kb.isModifierToggled(input.getModifier(i))) {
//				return false;
//			}
//		}
//		Condition<OWNER_TYPE> cond = input.getCondition();
//		if (cond != null && !cond.canTrigger(getOwner())) {
//			return false;
//		}
//		return true;
//	}
//	
//	protected static class InputReactionTuple<OWNER_TYPE extends PComponent> {
//		
//		public final PKeyInput<OWNER_TYPE> input;
//		public final Consumer<OWNER_TYPE> reaction;
//		
//		public InputReactionTuple(PKeyInput<OWNER_TYPE> input, 
//				Consumer<OWNER_TYPE> action) 
//		{
//			this.input = input;
//			this.reaction = action;
//		}
//		
//	}
//	
//}