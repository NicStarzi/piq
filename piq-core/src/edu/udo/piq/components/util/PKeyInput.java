package edu.udo.piq.components.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	
	public default FocusPolicy getFocusPolicy() {
		return FocusPolicy.THIS_HAS_FOCUS;
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
	
	public static enum FocusPolicy {
		NEVER {
			public boolean canTriggerFor(PComponent component) {
				return false;
			}
		},
		THIS_HAS_FOCUS {
			public boolean canTriggerFor(PComponent component) {
				return component.hasFocus();
			}
		},
		THIS_OR_CHILD_HAS_FOCUS {
			public boolean canTriggerFor(PComponent component) {
				return component.thisOrChildHasFocus();
			}
		},
		ALWAYS {
			public boolean canTriggerFor(PComponent component) {
				return true;
			}
		},
		;
		public static final List<FocusPolicy> ALL = 
				Collections.unmodifiableList(Arrays.asList(values()));
		
		public abstract boolean canTriggerFor(PComponent component);
		
	}
	
}