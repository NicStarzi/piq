package edu.udo.piq.actions;

import edu.udo.piq.PKeyboard.ActualKey;
import edu.udo.piq.PKeyboard.Modifier;
import edu.udo.piq.actions.PAccelerator.FocusPolicy;
import edu.udo.piq.components.popup2.ImmutablePActionIndicator;
import edu.udo.piq.components.popup2.PComponentActionIndicator;

public class ActionCutToClipboard extends CompositePComponentAction implements PComponentAction {
	
	public static final PComponentAction INSTANCE = new ActionCutToClipboard();
	public static final Object DEFAULT_KEY = StandardComponentActionKey.CUT;
	public static final PComponentActionIndicator DEFAULT_INDICATOR = 
			new ImmutablePActionIndicator(DEFAULT_KEY, "Cut", null, 
					new PAccelerator(ActualKey.X, Modifier.COMMAND, FocusPolicy.NEVER));
	
	public ActionCutToClipboard() {
		super(ActionCopyToClipboard.DEFAULT_KEY, ActionDelete.DEFAULT_KEY);
		setAccelerator(ActualKey.X, Modifier.COMMAND, 
				FocusPolicy.THIS_OR_CHILD_HAS_FOCUS);
	}
}