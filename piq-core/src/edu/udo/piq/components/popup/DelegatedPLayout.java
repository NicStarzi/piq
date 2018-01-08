package edu.udo.piq.components.popup;

import java.util.function.Consumer;

import edu.udo.piq.PComponent;
import edu.udo.piq.layouts.AbstractMapPLayout;
import edu.udo.piq.layouts.AlignmentX;
import edu.udo.piq.layouts.AlignmentY;
import edu.udo.piq.layouts.PComponentLayoutData;

public class DelegatedPLayout extends AbstractMapPLayout {
	
	public DelegatedPLayout(PComponent component) {
		super(component);
	}
	
	public void forEach(Consumer<PComponentLayoutData> action) {
		for (PComponentLayoutData data : getAllData()) {
			action.accept(data);
		}
	}
	
	public void delegatedSetChildBounds(PComponent child, int x, int y, 
			int width, int height, AlignmentX alignX, AlignmentY alignY) 
	{
		setChildCell(child, x, y, width, height, alignX, alignY);
	}
	
	@Override
	protected boolean canAdd(PComponent component, Object constraint) {
		return component != null;
	}
	
	@Override
	protected void layOutInternal() {}
	
}