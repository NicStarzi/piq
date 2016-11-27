package edu.udo.piq.style.standard;

import edu.udo.piq.PInsets;
import edu.udo.piq.PReadOnlyLayout;
import edu.udo.piq.PStyleLayout;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.tools.AbstractPLayout;
import edu.udo.piq.tools.ImmutablePInsets;

public class StandardButtonLayoutStyle implements PStyleLayout {
	
	public static final PInsets INSETS = new ImmutablePInsets(4);
	
	@SuppressWarnings("unchecked")
	@Override
	public <E> E getAttribute(PReadOnlyLayout layout,
			Object attrKey, E defaultValue)
	{
		if (attrKey == AbstractPLayout.ATTRIBUTE_KEY_INSETS
				&& defaultValue instanceof PInsets)
		{
			return (E) INSETS;
		}
		if (attrKey == AbstractPLayout.ATTRIBUTE_KEY_ALIGNMENT_X
				&& defaultValue instanceof AlignmentX)
		{
			return (E) AlignmentX.LEFT;
		}
		if (attrKey == AbstractPLayout.ATTRIBUTE_KEY_ALIGNMENT_Y
				&& defaultValue instanceof AlignmentY)
		{
			return (E) AlignmentY.CENTER;
		}
		return defaultValue;
	}
	
}