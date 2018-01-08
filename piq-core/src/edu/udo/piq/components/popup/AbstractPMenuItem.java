package edu.udo.piq.components.popup;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import edu.udo.piq.PColor;
import edu.udo.piq.PComponent;
import edu.udo.piq.PKeyboard;
import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Key;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.PMouse;
import edu.udo.piq.PMouse.MouseButton;
import edu.udo.piq.PRenderer;
import edu.udo.piq.PRoot;
import edu.udo.piq.actions.AbstractPComponentAction;
import edu.udo.piq.actions.PAccelerator;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.actions.PActionKey;
import edu.udo.piq.actions.PComponentAction;
import edu.udo.piq.components.PSingleValueModel;
import edu.udo.piq.components.collections.PColumnIndex;
import edu.udo.piq.components.textbased.PTextModel;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PGridLayout.Growth;
import edu.udo.piq.tools.AbstractPLayoutOwner;
import edu.udo.piq.tools.AbstractPTextModel;
import edu.udo.piq.util.Throw;
import edu.udo.piq.util.ThrowException;

public abstract class AbstractPMenuItem extends AbstractPLayoutOwner {
	
	public static final PActionKey DEFAULT_ACTION_KEY_ENTER = new PActionKey("PERFORM_ON_ENTER");
	public static final PColor DEFAULT_HIGHLIGHT_COLOR = PColor.DARK_BLUE;
	
	public class ActionTriggerOnEnter extends AbstractPComponentAction implements PComponentAction {
		
		{
			setAccelerator(ActualKey.ENTER, FocusPolicy.THIS_OR_CHILD_HAS_FOCUS);
		}
		
		@Override
		public boolean isEnabled(PRoot root) {
			return AbstractPMenuItem.this.isEnabled() && thisOrChildHasFocus();
		}
		
		@Override
		public void tryToPerform(PRoot root) {
			if (AbstractPMenuItem.this.isEnabled()) {
				performAction();
			}
		}
		
	}
	
	protected final Map<MenuEntryPart, PComponent> partsMap = new EnumMap<>(MenuEntryPart.class);
	protected PMenuIcon compIcon = new PMenuIcon();
	protected PMenuLabel compLabel = new PMenuLabel();
	protected PMenuLabel compAccelerator = new PMenuLabel();
	{
		addActionMapping(AbstractPMenuItem.DEFAULT_ACTION_KEY_ENTER, new ActionTriggerOnEnter());
		
		setLayout(new DelegatedPLayout(this));
		
		compIcon.setModel(new OverwritePSingleValueModel.OverwritePPictureModel());
		compLabel.setModel(new OverwritePSingleValueModel.OverwritePTextModel());
		compAccelerator.setModel(new AcceleratorTextModel());
		
		setComponent(MenuEntryPart.ICON, compIcon);
		setComponent(MenuEntryPart.LABEL, compLabel);
		setComponent(MenuEntryPart.ACCELERATOR, compAccelerator);
	}
	
	public abstract boolean isEnabled();
	
	protected abstract void performAction();
	
	protected void onMouseClick(PMouse mouse, MouseButton btn, int clickCount) {
		if (isEnabled()) {
			performAction();
		}
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
	
	public String getDefaultTextForAccelerator(PAccelerator accelerator) {
		PKeyboard kb = getKeyboard();
		if (kb == null) {
			return accelerator.toString();
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < accelerator.getModifierCount(); i++) {
			Modifier mod = accelerator.getModifier(i);
			String modName = kb.getModifierName(mod);
			sb.append(modName);
			sb.append('+');
		}
		for (int i = 0; i < accelerator.getKeyCount(); i++) {
			Key key = accelerator.getKey(i);
			String keyName = kb.getKeyName(key);
			sb.append(keyName);
			sb.append('+');
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
	
	public static void setOverwriteValue(PSingleValueModel model, Object value) {
		if (model instanceof OverwritePSingleValueModel) {
			OverwritePSingleValueModel overwriteModel = (OverwritePSingleValueModel) model;
			overwriteModel.setOverwrite(value);
		} else {
			model.setValue(value);
		}
	}
	
	public class AcceleratorTextModel extends AbstractPTextModel implements PTextModel {
		
		protected PAccelerator accelerator;
		protected String cachedStr = null;
		
		@Override
		public void setValue(Object value) {
			if (value == null) {
				super.setValue(null);
			} else {
				Throw.ifTypeCastFails(value, PAccelerator.class,
						() -> "value.getClass()="+value.getClass().getName()+"; value="+value);
				super.setValue(value);
			}
		}
		
		@Override
		protected void setValueInternal(Object newValue) {
			accelerator = (PAccelerator) newValue;
			cachedStr = null;
		}
		
		@Override
		public Object getValue() {
			return accelerator;
		}
		
		@Override
		public String getText() {
			if (cachedStr == null) {
				if (accelerator == null) {
					cachedStr = "";
				} else {
					cachedStr = getDefaultTextForAccelerator(accelerator);
				}
			}
			return cachedStr;
		}
		
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
	
}