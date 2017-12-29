package edu.udo.piq.components.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.util.ThrowException;

public class DefaultPAccelerator<COMP_TYPE extends PComponent> 
	implements PAccelerator<COMP_TYPE> 
{
	
	public static final KeyInputType DEFAULT_KEY_INPUT_TYPE = KeyInputType.TRIGGER;
	public static final FocusPolicy DEFAULT_FOCUS_POLICY = FocusPolicy.THIS_HAS_FOCUS;
	public static final Modifier[] DEFAULT_MODIFIERS = new Modifier[0];
	
	protected List<Modifier> mods;
	protected KeyInputType type = DEFAULT_KEY_INPUT_TYPE;
	protected FocusPolicy focusPlcy = DEFAULT_FOCUS_POLICY;
	protected ActualKey key;
	protected Predicate<COMP_TYPE> triggerCond;
	
	public DefaultPAccelerator(ActualKey key) {
		this(DEFAULT_FOCUS_POLICY, DEFAULT_KEY_INPUT_TYPE, key, null, DEFAULT_MODIFIERS);
	}
	
	public DefaultPAccelerator(KeyInputType inputType, ActualKey key) {
		this(DEFAULT_FOCUS_POLICY, inputType, key, null, DEFAULT_MODIFIERS);
	}
	
	public DefaultPAccelerator(FocusPolicy focusPolicy, ActualKey key) {
		this(focusPolicy, DEFAULT_KEY_INPUT_TYPE, key, null, DEFAULT_MODIFIERS);
	}
	
	public DefaultPAccelerator(FocusPolicy focusPolicy, KeyInputType inputType, ActualKey key) {
		this(focusPolicy, inputType, key, null, DEFAULT_MODIFIERS);
	}
	
	public DefaultPAccelerator(ActualKey key, Predicate<COMP_TYPE> condition) {
		this(DEFAULT_FOCUS_POLICY, DEFAULT_KEY_INPUT_TYPE, key, condition, DEFAULT_MODIFIERS);
	}
	
	public DefaultPAccelerator(KeyInputType inputType, ActualKey key, Predicate<COMP_TYPE> condition) {
		this(DEFAULT_FOCUS_POLICY, inputType, key, condition, DEFAULT_MODIFIERS);
	}
	
	public DefaultPAccelerator(FocusPolicy focusPolicy, ActualKey key, Predicate<COMP_TYPE> condition) {
		this(focusPolicy, DEFAULT_KEY_INPUT_TYPE, key, condition, DEFAULT_MODIFIERS);
	}
	
	public DefaultPAccelerator(FocusPolicy focusPolicy, KeyInputType inputType, ActualKey key, Predicate<COMP_TYPE> condition) {
		this(focusPolicy, inputType, key, condition, DEFAULT_MODIFIERS);
	}
	
	public DefaultPAccelerator(ActualKey key, Modifier ... mods) {
		this(DEFAULT_FOCUS_POLICY, DEFAULT_KEY_INPUT_TYPE, key, null, mods);
	}
	
	public DefaultPAccelerator(KeyInputType inputType, ActualKey key, Modifier ... mods) {
		this(DEFAULT_FOCUS_POLICY, inputType, key, null, mods);
	}
	
	public DefaultPAccelerator(FocusPolicy focusPolicy, ActualKey key, Modifier ... mods) {
		this(focusPolicy, DEFAULT_KEY_INPUT_TYPE, key, null, mods);
	}
	
	public DefaultPAccelerator(FocusPolicy focusPolicy, KeyInputType inputType, ActualKey key, Modifier ... mods) {
		this(focusPolicy, inputType, key, null, mods);
	}
	
	public DefaultPAccelerator(ActualKey key, Predicate<COMP_TYPE> condition, Modifier ... mods) {
		this(DEFAULT_FOCUS_POLICY, DEFAULT_KEY_INPUT_TYPE, key, condition, mods);
	}
	
	public DefaultPAccelerator(KeyInputType inputType, ActualKey key, Predicate<COMP_TYPE> condition, Modifier ... mods) {
		this(DEFAULT_FOCUS_POLICY, inputType, key, condition, mods);
	}
	
	public DefaultPAccelerator(FocusPolicy focusPolicy, ActualKey key, Predicate<COMP_TYPE> condition, Modifier ... mods) {
		this(focusPolicy, DEFAULT_KEY_INPUT_TYPE, key, condition, mods);
	}
	
	public DefaultPAccelerator(FocusPolicy focusPolicy, KeyInputType inputType, 
			ActualKey key, Predicate<COMP_TYPE> condition, Modifier ... mods) 
	{
		this.focusPlcy = ThrowException.ifNull(focusPolicy, "focusPolicy == null");
		this.type = ThrowException.ifNull(inputType, "inputType == null");
		this.key = ThrowException.ifNull(key, "key == null");
		this.triggerCond = condition;
		if (mods != null && mods.length > 0) {
			this.mods = new ArrayList<>(Arrays.asList(mods));
		}
	}
	
	public DefaultPAccelerator<COMP_TYPE> setKey(ActualKey value) {
		key = value;
		return this;
	}
	
	public ActualKey getKey() {
		return key;
	}
	
	public DefaultPAccelerator<COMP_TYPE> addModifier(Modifier modifier) {
		if (mods == null) {
			mods = new ArrayList<>(2);
		}
		mods.add(modifier);
		return this;
	}
	
	public DefaultPAccelerator<COMP_TYPE> removeModifier(Modifier modifier) {
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
	
	public DefaultPAccelerator<COMP_TYPE> setKeyInputType(KeyInputType value) {
		type = value;
		return this;
	}
	
	public KeyInputType getKeyInputType() {
		return type;
	}
	
	public DefaultPAccelerator<COMP_TYPE> setFocusPolicy(FocusPolicy value) {
		focusPlcy = value;
		return this;
	}
	
	public FocusPolicy getFocusPolicy() {
		return focusPlcy;
	}
	
	public DefaultPAccelerator<COMP_TYPE> setCondition(Predicate<COMP_TYPE> condition) {
		triggerCond = condition;
		return this;
	}
	
	public Predicate<COMP_TYPE> getCondition() {
		return triggerCond;
	}
	
}