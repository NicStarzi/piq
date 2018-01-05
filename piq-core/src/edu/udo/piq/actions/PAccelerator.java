package edu.udo.piq.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.util.Throw;
import edu.udo.piq.util.ThrowException;

public class PAccelerator {
	
	public static final FocusPolicy DEFAULT_FOCUS_POLICY = FocusPolicy.THIS_HAS_FOCUS;
	public static final KeyInputType DEFAULT_INPUT_TYPE = KeyInputType.TRIGGER;
	
	protected final List<Key> keys = new ArrayList<>();
	protected final List<Modifier> mods = new ArrayList<>();
	protected final FocusPolicy focusPolicy;
	protected final KeyInputType inputType;
	
	public PAccelerator(Key key) {
		this(key, null, DEFAULT_FOCUS_POLICY, DEFAULT_INPUT_TYPE);
	}
	
	public PAccelerator(Key key, KeyInputType inputType) {
		this(key, null, DEFAULT_FOCUS_POLICY, inputType);
	}
	
	public PAccelerator(Key key, FocusPolicy focusPolicy) {
		this(key, null, focusPolicy, DEFAULT_INPUT_TYPE);
	}
	
	public PAccelerator(Key key, FocusPolicy focusPolicy, KeyInputType inputType) {
		this(key, null, focusPolicy, inputType);
	}
	
	public PAccelerator(Key key, Modifier mod1, Modifier mod2) {
		this(key, mod1, DEFAULT_FOCUS_POLICY, DEFAULT_INPUT_TYPE);
		if (mod2 != null) {
			mods.add(mod2);
		}
	}
	
	public PAccelerator(Key key, Modifier mod) {
		this(key, mod, DEFAULT_FOCUS_POLICY, DEFAULT_INPUT_TYPE);
	}
	
	public PAccelerator(Key key, Modifier mod, KeyInputType inputType) {
		this(key, mod, DEFAULT_FOCUS_POLICY, inputType);
	}
	
	public PAccelerator(Key key, Modifier mod, FocusPolicy focusPolicy) {
		this(key, mod, focusPolicy, DEFAULT_INPUT_TYPE);
	}
	
	public PAccelerator(Key key, Modifier mod, FocusPolicy focusPolicy, KeyInputType inputType) {
		if (key != null) {
			keys.add(key);
		}
		if (mod != null) {
			mods.add(mod);
		}
		Throw.ifNull(focusPolicy, "focusPolicy == null");
		Throw.ifNull(inputType, "inputType == null");
		this.focusPolicy = focusPolicy;
		this.inputType = inputType;
//		setFocusPolicy(focusPolicy);
//		setKeyInputType(inputType);
	}
	
	public PAccelerator setKeys(Key ... values) {
		ThrowException.ifNull(values, "values == null");
		keys.clear();
		keys.addAll(Arrays.asList(values));
		return this;
	}
	
	public int getKeyCount() {
		return keys.size();
	}
	
	public Key getKey(int index) {
		ThrowException.ifNotWithin(0, getKeyCount(), index, "index out of bounds");
		return keys.get(index);
	}
	
	public List<Key> getKeys() {
		return Collections.unmodifiableList(keys);
	}
	
	public PAccelerator setModifiers(Modifier ... values) {
		ThrowException.ifNull(values, "values == null");
		mods.clear();
		mods.addAll(Arrays.asList(values));
		return this;
	}
	
	public int getModifierCount() {
		return mods.size();
	}
	
	public Modifier getModifier(int index) {
		ThrowException.ifNotWithin(0, getModifierCount(), index, "index out of bounds");
		return mods.get(index);
	}
	
	public List<Modifier> getModifiers() {
		return Collections.unmodifiableList(mods);
	}
	
//	public PAccelerator setFocusPolicy(FocusPolicy value) {
//		ThrowException.ifNull(value, "value == null");
//		focusPolicy = value;
//		return this;
//	}
	
	public FocusPolicy getFocusPolicy() {
		return focusPolicy;
	}
	
//	public PAccelerator setKeyInputType(KeyInputType value) {
//		ThrowException.ifNull(value, "value == null");
//		inputType = value;
//		return this;
//	}
	
	public KeyInputType getKeyInputType() {
		return inputType;
	}
	
	public Object getDefaultIdentifier() {
		return toString();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName());
		sb.append("{focusPolicy=");
		sb.append(getFocusPolicy().name());
		sb.append("; inputType=");
		sb.append(getKeyInputType().name());
		sb.append("; keyCombination=");
		for (int i = 0; i < getModifierCount(); i++) {
			sb.append(getModifier(i).name());
			sb.append('+');
		}
		for (int i = 0; i < getKeyCount(); i++) {
			sb.append(getKey(i).getSymbolicName());
			sb.append('+');
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
	
	public static enum KeyInputType {
		PRESS,
		RELEASE,
		TRIGGER,
		;
	}
	
	public static enum FocusPolicy {
		NEVER {
			@Override
			public boolean canTriggerFor(PComponent component) {
				return false;
			}
		},
		THIS_HAS_FOCUS {
			@Override
			public boolean canTriggerFor(PComponent component) {
				return component.hasFocus();
			}
		},
		THIS_OR_CHILD_HAS_FOCUS {
			@Override
			public boolean canTriggerFor(PComponent component) {
				return component.thisOrChildHasFocus();
			}
		},
		ALWAYS {
			@Override
			public boolean canTriggerFor(PComponent component) {
				return true;
			}
		},
		;
		public static final List<FocusPolicy> ALL =
				Collections.unmodifiableList(Arrays.asList(FocusPolicy.values()));
		
		public abstract boolean canTriggerFor(PComponent component);
		
	}
	
}