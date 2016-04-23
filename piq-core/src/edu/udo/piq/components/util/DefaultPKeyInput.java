package edu.udo.piq.components.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.util.ThrowException;

public class DefaultPKeyInput implements PKeyInput {
	
	public static final KeyInputType DEFAULT_KEY_INPUT_TYPE = KeyInputType.TRIGGER;
	public static final FocusPolicy DEFAULT_FOCUS_POLICY = FocusPolicy.THIS_HAS_FOCUS;
	public static final Modifier[] DEFAULT_MODIFIERS = new Modifier[0];
	
	protected List<Modifier> mods;
	protected KeyInputType type = DEFAULT_KEY_INPUT_TYPE;
	protected FocusPolicy focusPlcy = DEFAULT_FOCUS_POLICY;
	protected Key key;
	protected OptionalCondition triggerCond;
	
	public DefaultPKeyInput(Key key) {
		this(DEFAULT_FOCUS_POLICY, DEFAULT_KEY_INPUT_TYPE, key, null, DEFAULT_MODIFIERS);
	}
	
	public DefaultPKeyInput(KeyInputType inputType, Key key) {
		this(DEFAULT_FOCUS_POLICY, inputType, key, null, DEFAULT_MODIFIERS);
	}
	
	public DefaultPKeyInput(FocusPolicy focusPolicy, Key key) {
		this(focusPolicy, DEFAULT_KEY_INPUT_TYPE, key, null, DEFAULT_MODIFIERS);
	}
	
	public DefaultPKeyInput(FocusPolicy focusPolicy, KeyInputType inputType, Key key) {
		this(focusPolicy, inputType, key, null, DEFAULT_MODIFIERS);
	}
	
	public DefaultPKeyInput(Key key, OptionalCondition condition) {
		this(DEFAULT_FOCUS_POLICY, DEFAULT_KEY_INPUT_TYPE, key, condition, DEFAULT_MODIFIERS);
	}
	
	public DefaultPKeyInput(KeyInputType inputType, Key key, OptionalCondition condition) {
		this(DEFAULT_FOCUS_POLICY, inputType, key, condition, DEFAULT_MODIFIERS);
	}
	
	public DefaultPKeyInput(FocusPolicy focusPolicy, Key key, OptionalCondition condition) {
		this(focusPolicy, DEFAULT_KEY_INPUT_TYPE, key, condition, DEFAULT_MODIFIERS);
	}
	
	public DefaultPKeyInput(FocusPolicy focusPolicy, KeyInputType inputType, Key key, OptionalCondition condition) {
		this(focusPolicy, inputType, key, condition, DEFAULT_MODIFIERS);
	}
	
	public DefaultPKeyInput(Key key, Modifier ... mods) {
		this(DEFAULT_FOCUS_POLICY, DEFAULT_KEY_INPUT_TYPE, key, null, mods);
	}
	
	public DefaultPKeyInput(KeyInputType inputType, Key key, Modifier ... mods) {
		this(DEFAULT_FOCUS_POLICY, inputType, key, null, mods);
	}
	
	public DefaultPKeyInput(FocusPolicy focusPolicy, Key key, Modifier ... mods) {
		this(focusPolicy, DEFAULT_KEY_INPUT_TYPE, key, null, mods);
	}
	
	public DefaultPKeyInput(FocusPolicy focusPolicy, KeyInputType inputType, Key key, Modifier ... mods) {
		this(focusPolicy, inputType, key, null, mods);
	}
	
	public DefaultPKeyInput(Key key, OptionalCondition condition, Modifier ... mods) {
		this(DEFAULT_FOCUS_POLICY, DEFAULT_KEY_INPUT_TYPE, key, condition, mods);
	}
	
	public DefaultPKeyInput(KeyInputType inputType, Key key, OptionalCondition condition, Modifier ... mods) {
		this(DEFAULT_FOCUS_POLICY, inputType, key, condition, mods);
	}
	
	public DefaultPKeyInput(FocusPolicy focusPolicy, Key key, OptionalCondition condition, Modifier ... mods) {
		this(focusPolicy, DEFAULT_KEY_INPUT_TYPE, key, condition, mods);
	}
	
	public DefaultPKeyInput(FocusPolicy focusPolicy, KeyInputType inputType, Key key, OptionalCondition condition, Modifier ... mods) {
		this.focusPlcy = ThrowException.ifNull(focusPolicy, "focusPolicy == null");
		this.type = ThrowException.ifNull(inputType, "inputType == null");
		this.key = ThrowException.ifNull(key, "key == null");
		this.triggerCond = condition;
		if (mods != null && mods.length > 0) {
			this.mods = new ArrayList<>(Arrays.asList(mods));
		}
	}
	
	public DefaultPKeyInput setKey(Key value) {
		key = value;
		return this;
	}
	
	public Key getKey() {
		return key;
	}
	
	public DefaultPKeyInput addModifier(Modifier modifier) {
		if (mods == null) {
			mods = new ArrayList<>(2);
		}
		mods.add(modifier);
		return this;
	}
	
	public DefaultPKeyInput removeModifier(Modifier modifier) {
		if (mods != null) {
			mods.remove(modifier);
		}
		return this;
	}
	
	public int getModifierCount() {
		if (mods == null) {
			return 0;
		}
		return mods.size();
	}
	
	public Modifier getModifier(int index) {
		if (mods == null) {
			throw new IndexOutOfBoundsException("index="+index+", size=0");
		}
		return mods.get(index);
	}
	
	public DefaultPKeyInput setKeyInputType(KeyInputType value) {
		type = value;
		return this;
	}
	
	public KeyInputType getKeyInputType() {
		return type;
	}
	
	public DefaultPKeyInput setFocusPolicy(FocusPolicy value) {
		focusPlcy = value;
		return this;
	}
	
	public FocusPolicy getFocusPolicy() {
		return focusPlcy;
	}
	
	public DefaultPKeyInput setCondition(OptionalCondition condition) {
		triggerCond = condition;
		return this;
	}
	
	public OptionalCondition getOptionalCondition() {
		return triggerCond;
	}
	
}