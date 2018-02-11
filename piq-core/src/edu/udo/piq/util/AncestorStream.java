package edu.udo.piq.util;

import java.util.function.Predicate;

import edu.udo.piq.PComponent;

public class AncestorStream<COMP_TYPE extends PComponent> implements ComponentStream<COMP_TYPE> {
	
	private final PComponent start;
	private Predicate<COMP_TYPE> filters = null;
	private PComponent current;
	
	public AncestorStream(PComponent component, boolean includeSelf) {
		if (includeSelf) {
			current = component;
		} else {
			current = component.getParent();
		}
		start = current;
	}
	
	@Override
	public ComponentStream<COMP_TYPE> rewindIteration() {
		current = start;
		return this;
	}
	
	@Override
	public ComponentStream<COMP_TYPE> resetAllFilters() {
		filters = null;
		return this;
	}
	
	@Override
	public ComponentStream<COMP_TYPE> require(Predicate<COMP_TYPE> condition) {
		if (filters == null) {
			filters = condition;
		}
		final Predicate<COMP_TYPE> oldFilter = filters;
		filters = comp -> oldFilter.test(comp) && condition.test(comp);
		return this;
	}
	
	@Override
	public boolean isAllowed(COMP_TYPE component) {
		return filters == null || filters.test(component);
	}
	
	@Override
	public COMP_TYPE peekNext() {
		PComponent current = this.current;
		while (current != null) {
			@SuppressWarnings("unchecked")
			COMP_TYPE comp = (COMP_TYPE) current;
			current = current.getParent();
			if (isAllowed(comp)) {
				return comp;
			}
		}
		return null;
	}
	
	@Override
	public COMP_TYPE getNext() {
		while (current != null) {
			@SuppressWarnings("unchecked")
			COMP_TYPE comp = (COMP_TYPE) current;
			current = current.getParent();
			if (isAllowed(comp)) {
				return comp;
			}
		}
		return null;
	}
}