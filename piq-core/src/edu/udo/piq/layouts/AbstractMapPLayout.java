package edu.udo.piq.layouts;

import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;

public abstract class AbstractMapPLayout extends AbstractPLayout implements PLayout {
	
	protected final Map<PComponent, PComponentLayoutData> compMap = new HashMap<>();
	
	protected AbstractMapPLayout(PComponent component) {
		super(component);
	}
	
	public int getChildCount() {
		return compMap.size();
	}
	
	public PComponentLayoutData getDataFor(PComponent child) {
		return compMap.get(child);
	}
	
	public Iterable<PComponentLayoutData> getAllData() {
		return compMap.values();
	}
	
	protected void clearAllDataInternal() {
		compMap.clear();
	}
	
	protected void addDataInternal(PComponentLayoutData info) {
		compMap.put(info.getComponent(), info);
	}
	
	protected void removeDataInternal(PComponentLayoutData info) {
		compMap.remove(info.getComponent());
	}
}