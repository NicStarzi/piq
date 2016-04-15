package edu.udo.piq.tools;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.udo.piq.PComponent;
import edu.udo.piq.PLayout;

public abstract class AbstractMapPLayout extends AbstractPLayout implements PLayout {
	
	private final Map<PComponent, PCompInfo> compMap = new HashMap<>();
	
	protected AbstractMapPLayout(PComponent component) {
		super(component);
	}
	
	public boolean isEmpty() {
		return compMap.isEmpty();
	}
	
	public int getChildCount() {
		return compMap.size();
	}
	
	public Collection<PComponent> getChildren() {
		return Collections.unmodifiableSet(compMap.keySet());
	}
	
	protected PCompInfo getInfoFor(PComponent child) {
		return compMap.get(child);
	}
	
	protected Iterable<PCompInfo> getAllInfos() {
		return compMap.values();
	}
	
	protected void clearAllInfosInternal() {
		compMap.clear();
	}
	
	protected void addInfoInternal(PCompInfo info) {
		compMap.put(info.comp, info);
	}
	
	protected void removeInfoInternal(PCompInfo info) {
		compMap.remove(info.comp);
	}
}