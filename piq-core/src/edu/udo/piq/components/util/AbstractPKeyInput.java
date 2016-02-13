package edu.udo.piq.components.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;

public class AbstractPKeyInput implements PKeyInput {
	
	protected final List<Modifier> mods;
	protected final KeyInputType type;
	protected final Key key;
	protected OptionalCondition triggerCond;
	
	public AbstractPKeyInput(Key key, Modifier ... mods) {
		this(KeyInputType.TRIGGER, key, mods);
	}
	
	public AbstractPKeyInput(OptionalCondition condition, Key key, Modifier ... mods) {
		this(condition, KeyInputType.TRIGGER, key, mods);
	}
	
	public AbstractPKeyInput(KeyInputType inputType, Key key, Modifier ... mods) {
		this(null, inputType, key, mods);
	}
	
	public AbstractPKeyInput(OptionalCondition condition, KeyInputType inputType, Key key, Modifier ... mods) {
		this.mods = Collections.unmodifiableList(Arrays.asList(mods));
		this.type = inputType;
		this.key = key;
		setOptionalCondition(condition);
	}
	
	public AbstractPKeyInput(Key key) {
		this(null, KeyInputType.TRIGGER, key);
	}
	
	public AbstractPKeyInput(Key key, OptionalCondition condition) {
		this(condition, KeyInputType.TRIGGER, key);
	}
	
	public AbstractPKeyInput(KeyInputType inputType, Key key) {
		this(null, inputType, key);
	}
	
	public AbstractPKeyInput(KeyInputType inputType, Key key, OptionalCondition condition) {
		this.mods = Collections.emptyList();
		this.type = inputType;
		this.key = key;
		setOptionalCondition(condition);
	}
	
	public Key getKey() {
		return key;
	}
	
	public int getModifierCount() {
		return mods.size();
	}
	
	public Modifier getModifier(int index) {
		return mods.get(index);
	}
	
	public KeyInputType getKeyInputType() {
		return type;
	}
	
	public void setOptionalCondition(OptionalCondition condition) {
		triggerCond = condition;
	}
	
	public OptionalCondition getOptionalCondition() {
		return triggerCond;
	}
	
}