package edu.udo.piq.components.popup2;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.components.PSingleValueModel;
import edu.udo.piq.components.collections.PColumnIndex;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPMenuItem extends AbstractPLayoutOwner {
	
	public static final Map<Modifier, String> MODIFIER_NAME_MAP;
	public static final Map<Key, String> KEY_NAME_MAP;
	static {
		Map<Modifier, String> modifierNameMap = new EnumMap<>(Modifier.class);
		modifierNameMap.put(Modifier.ALT, "Alt");
		modifierNameMap.put(Modifier.ALT_GRAPH, "AltGr");
		modifierNameMap.put(Modifier.CAPS, "Shift");
		modifierNameMap.put(Modifier.COMMAND, "Cmd");
		modifierNameMap.put(Modifier.CTRL, "Ctrl");
		modifierNameMap.put(Modifier.SHIFT, "Shift");
		modifierNameMap.put(Modifier.META, "Meta");
		MODIFIER_NAME_MAP = Collections.unmodifiableMap(modifierNameMap);
		
		Map<Key, String> keyNameMap = new HashMap<>();
		keyNameMap.put(ActualKey.DELETE, "Del");
		keyNameMap.put(ActualKey.BACKSPACE, "Backspace");
		keyNameMap.put(ActualKey.UP, "Up");
		keyNameMap.put(ActualKey.DOWN, "Down");
		keyNameMap.put(ActualKey.LEFT, "Left");
		keyNameMap.put(ActualKey.RIGHT, "Right");
		keyNameMap.put(ActualKey.ENTER, "Enter");
		keyNameMap.put(ActualKey.ESCAPE, "Escape");
		keyNameMap.put(ActualKey.HOME, "Home");
		keyNameMap.put(ActualKey.SPACE, "Space");
		keyNameMap.put(ActualKey.TAB, "Tab");
		KEY_NAME_MAP = Collections.unmodifiableMap(keyNameMap);
	}
	public static final PColor DEFAULT_HIGHLIGHT_COLOR = PColor.DARK_BLUE;
	
	protected final Map<MenuEntryPart, PComponent> partsMap = new EnumMap<>(MenuEntryPart.class);
	protected PMenuIcon compIcon = new PMenuIcon();
	protected PMenuLabel compLabel = new PMenuLabel();
	protected PMenuLabel compAccelerator = new PMenuLabel();
	{
		setLayout(new DelegatedPLayout(this));
		
		compIcon.setModel(new OverwritePSingleValueModel.OverwritePPictureModel());
		compLabel.setModel(new OverwritePSingleValueModel.OverwritePTextModel());
		compAccelerator.setModel(new AcceleratorTextModel());
		
		setComponent(MenuEntryPart.ICON, compIcon);
		setComponent(MenuEntryPart.LABEL, compLabel);
		setComponent(MenuEntryPart.ACCELERATOR, compAccelerator);
	}
	
	public abstract boolean isEnabled();
	
	protected void onMouseClick(PMouse mouse, MouseButton btn, int clickCount) {
		// intentionally left blank.
		// overwritten by subclasses.
	}
	
	protected void fireActionEvent() {
		PMenuBody body = getFirstAncestorOfType(PMenuBody.class);
		if (body != null) {
			body.fireActionEvent(this);
		}
	}
	
	@Override
	public boolean isFocusable() {
		return true;
	}
	
	@Override
	public boolean isStrongFocusOwner() {
		return false;
	}
	
	public PMenuIcon getIconComponent() {
		return compIcon;
	}
	
	public PMenuLabel getLabelComponent() {
		return compLabel;
	}
	
	public PMenuLabel getAcceleratorComponent() {
		return compAccelerator;
	}
	
	public void setIconValue(Object value) {
		AbstractPMenuItem.setOverwriteValue(getIconComponent().getModel(), value);
	}
	
	public Object getIconValue() {
		return getIconComponent().getModel().getValue();
	}
	
	public void setLabelValue(Object value) {
		AbstractPMenuItem.setOverwriteValue(getLabelComponent().getModel(), value);
	}
	
	public Object getLabelValue() {
		return getLabelComponent().getModel().getValue();
	}
	
	public void setAcceleratorValue(Object value) {
		AbstractPMenuItem.setOverwriteValue(getAcceleratorComponent().getModel(), value);
	}
	
	public Object getAcceleratorValue() {
		return getAcceleratorComponent().getModel().getValue();
	}
	
	@Override
	public void defaultRender(PRenderer renderer) {
		if (isHighlighted()) {
			renderer.setColor(DEFAULT_HIGHLIGHT_COLOR);
			renderer.drawQuad(getBoundsWithoutBorder());
		}
	}
	
	@Override
	public boolean defaultFillsAllPixels() {
		return isHighlighted();
	}
	
	@Override
	protected DelegatedPLayout getLayoutInternal() {
		return (DelegatedPLayout) super.getLayout();
	}
	
	protected void setComponent(MenuEntryPart part, PComponent component) {
		PComponent oldCmp = partsMap.get(part);
		ThrowException.ifTrue(oldCmp != null && component != null, "partsMap.containsKey(part) == true");
		if (oldCmp != null) {
			partsMap.remove(part);
			getLayoutInternal().removeChild(oldCmp);
		}
		if (component != null) {
			partsMap.put(part, component);
			getLayoutInternal().addChild(component, part.getDefaultIndex());
		}
	}
	
	public PComponent getComponent(MenuEntryPart part) {
		ThrowException.ifNull(part, "part == null");
		return partsMap.get(part);
	}
	
	public boolean isHighlighted() {
		PRoot root = getRoot();
		if (root == null) {
			return false;
		}
		PMenuBody container = getFirstAncestorOfType(PMenuBody.class);
		PComponent focusOwner = root.getFocusOwner();
		return focusOwner != null
				&& focusOwner != container
				&& (focusOwner == this 
				|| isAncestorOf(focusOwner) 
				|| isDescendantOf(focusOwner));
	}
	
	public static enum MenuEntryPart {
		CHECKBOX			(AlignmentX.CENTER, AlignmentY.CENTER, Growth.PREFERRED),
		ICON				(AlignmentX.CENTER, AlignmentY.CENTER, Growth.PREFERRED),
		LABEL				(AlignmentX.LEFT, AlignmentY.CENTER, Growth.MAXIMIZE),
		ACCELERATOR			(AlignmentX.LEFT, AlignmentY.CENTER, Growth.PREFERRED),
		SUBMENU_INDICATOR	(AlignmentX.RIGHT, AlignmentY.CENTER, Growth.PREFERRED),
		;
		public static final List<AbstractPMenuItem.MenuEntryPart> ALL
			= Collections.unmodifiableList(Arrays.asList(MenuEntryPart.values()));
		public static final int COUNT = ALL.size();
		
		private final PColumnIndex DEFAULT_INDEX;
		private final AlignmentX DEFAULT_COLUMN_ALIGNMENT_X;
		private final AlignmentY DEFAULT_COLUMN_ALIGNMENT_Y;
		private final Growth DEFAULT_COLUMN_GROWTH;
		
		private MenuEntryPart(AlignmentX prefAlignX, AlignmentY prefAlignY, Growth prefGrowth) {
			DEFAULT_INDEX = new PColumnIndex(ordinal());
			DEFAULT_COLUMN_ALIGNMENT_X = prefAlignX;
			DEFAULT_COLUMN_ALIGNMENT_Y = prefAlignY;
			DEFAULT_COLUMN_GROWTH = prefGrowth;
		}
		
		public PColumnIndex getDefaultIndex() {
			return DEFAULT_INDEX;
		}
		
		public AlignmentX getDefaultColumnAlignmentX() {
			return DEFAULT_COLUMN_ALIGNMENT_X;
		}
		
		public AlignmentY getDefaultColumnAlignmentY() {
			return DEFAULT_COLUMN_ALIGNMENT_Y;
		}
		
		public Growth getDefaultColumnGrowth() {
			return DEFAULT_COLUMN_GROWTH;
		}
		
	}
	
	public static void setOverwriteValue(PSingleValueModel model, Object value) {
		if (model instanceof OverwritePSingleValueModel) {
			OverwritePSingleValueModel overwriteModel = (OverwritePSingleValueModel) model;
			overwriteModel.setOverwrite(value);
		} else {
			model.setValue(value);
		}
	}
	
	public static String getDefaultTextForAccelerator(PAccelerator accelerator) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < accelerator.getModifierCount(); i++) {
			Modifier mod = accelerator.getModifier(i);
			String modName = MODIFIER_NAME_MAP.get(mod);
			if (modName == null) {
				modName = mod.toString();
			}
			sb.append(modName);
			sb.append('+');
		}
		for (int i = 0; i < accelerator.getKeyCount(); i++) {
			Key key = accelerator.getKey(i);
			String keyName = KEY_NAME_MAP.get(key);
			if (keyName == null) {
				keyName = key.toString();
			}
			sb.append(keyName);
			sb.append('+');
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
	
	public static class AcceleratorTextModel extends OverwritePSingleValueModel.OverwritePTextModel {
		
		protected PAccelerator accelerator;
		
//		@Override
//		public void clearOverwrite() {
//			overwriteValue = null;
//			if (overwriteEnabled) {
//				overwriteEnabled = false;
//				cachedStr = null;
//				fireTextChangeEvent();
//			}
//		}
		
//		@Override
//		public void setOverwrite(Object value) {
//			if (!overwriteEnabled || !Objects.equals(overwriteValue, value)) {
//				overwriteEnabled = true;
//				overwriteValue = value;
//				cachedStr = null;
//				fireTextChangeEvent();
//			}
//		}
		
		@Override
		public void setValue(Object value) {
			if (accelerator == value) {
				return;
			}
			if (value instanceof PAccelerator) {
				accelerator = (PAccelerator) value;
//				cachedStr = null;
//				fireTextChangeEvent();
			} else {
//				cachedStr = value.toString();
//				fireTextChangeEvent();
			}
		}
		
//		@Override
//		protected void refreshCachedStringValue() {
//			cachedStr = AbstractPMenuItem.getDefaultTextForAccelerator(accelerator);
//		}
		
		@Override
		public Object getValue() {
			if (overwriteEnabled) {
				return overwriteValue;
			}
			return accelerator;
		}
		
	}
	
}