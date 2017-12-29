package edu.udo.piq.components.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboardObs;
import edu.udo.piq.components.util.PAccelerator.FocusPolicy;
import edu.udo.piq.components.util.PAccelerator.KeyInputType;

public class PAcceleratorMap {
	
	private final PComponent owner;
	/**
	 * Lazily initialized in the {@link #defineInput(Object, PAccelerator, Consumer)} 
	 * method. <br>This will usually be a HashMap since the nature of the keys is not known.
	 */
	private Map<Object, InputReactionTuple<?>> inputMap;
	/**
	 * Lazily initialized in the {@link #defineInput(Object, PAccelerator, Consumer)} 
	 * method.
	 */
	private PKeyboardObs keyObs;
	/**
	 * The most permissive {@link FocusPolicy} of all {@link PAccelerator key inputs}. This 
	 * is used to filter out unneeded events before iterating over all known inputs.<br>
	 * This value needs to be updated each time an input is added or removed.<br>
	 */
	private FocusPolicy mostPermissivePlcy = FocusPolicy.NEVER;
	
	public PAcceleratorMap(PComponent component) {
		owner = component;
	}
	
	public <OWNER_TYPE extends PComponent> void defineInput(Object identifier, PAccelerator<OWNER_TYPE> input, 
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
				public void onKeyPressed(PKeyboard keyboard, ActualKey key) {
					PAcceleratorMap.this.onEvent(keyboard, key, KeyInputType.PRESS);
				}
				public void onKeyTriggered(PKeyboard keyboard, ActualKey key) {
					PAcceleratorMap.this.onEvent(keyboard, key, KeyInputType.TRIGGER);
				}
				public void onKeyReleased(PKeyboard keyboard, ActualKey key) {
					PAcceleratorMap.this.onEvent(keyboard, key, KeyInputType.RELEASE);
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
	
	protected void onEvent(PKeyboard kb, ActualKey key, KeyInputType inputType) {
		if (inputMap == null) {
			throw new IllegalArgumentException("An event was recorded without having any "
					+ "key input defined. This can only happen when the "+PAcceleratorMap.class.getSimpleName()
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
			PAccelerator<OWNER_TYPE> input, PKeyboard kb, ActualKey key, KeyInputType inputType) 
	{
		if (!input.getFocusPolicy().canTriggerFor(owner)) {
			return false;
		}
		if (input.getKeyInputType() != inputType || !kb.isEqualKey(key, input.getKey())) {
			return false;
		}
		for (int i = 0; i < input.getModifierCount(); i++) {
			if (!kb.isModifierToggled(input.getModifier(i))) {
				return false;
			}
		}
		Predicate<OWNER_TYPE> cond = input.getCondition();
		if (cond != null && !cond.test((OWNER_TYPE) getOwner())) {
			return false;
		}
		return true;
	}
	
	protected static class InputReactionTuple<COMP_TYPE extends PComponent> {
		
		public final PAccelerator<COMP_TYPE> input;
		public final Consumer<COMP_TYPE> reaction;
		
		public InputReactionTuple(PAccelerator<COMP_TYPE> input, 
				Consumer<COMP_TYPE> action) 
		{
			this.input = input;
			this.reaction = action;
		}
		
	}
	
}