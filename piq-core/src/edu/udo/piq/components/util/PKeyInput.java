package edu.udo.piq.components.util;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;

public interface PKeyInput {
	
	public Key getKey();
	
	public int getModifierCount();
	
	public Modifier getModifier(int index);
	
	public default OptionalCondition getOptionalCondition() {
		return null;
	}
	
	public default KeyInputType getKeyInputType() {
		return KeyInputType.TRIGGER;
	}
	
	public default Object getDefaultIdentifier() {
		StringBuilder sb = new StringBuilder();
		sb.append(getKeyInputType().name());
		sb.append("::");
		for (int i = 0; i < getModifierCount(); i++) {
			sb.append(getModifier(i).name());
			sb.append('+');
		}
		sb.append(getKey().name());
		return sb.toString();
	}
	
	@FunctionalInterface
	public static interface OptionalCondition {
		public boolean canTrigger(PComponent comp);
	}
	
	public static enum KeyInputType {
		PRESS,
		RELEASE,
		TRIGGER, 
		;
		
	}
	
}