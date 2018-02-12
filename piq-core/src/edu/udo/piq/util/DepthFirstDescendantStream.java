package edu.udo.piq.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Predicate;

import edu.udo.piq.PComponent;

public class DepthFirstDescendantStream<COMP_TYPE extends PComponent> implements ComponentStream<COMP_TYPE> {
	
	private final Deque<PComponent> stack = new ArrayDeque<>();
	private final PComponent root;
	private final boolean includeRoot;
	private Predicate<COMP_TYPE> filters = null;
	
	public DepthFirstDescendantStream(PComponent rootComp, boolean includeRoot) {
		root = rootComp;
		this.includeRoot = includeRoot;
		rewindIteration();
	}
	
	@Override
	public ComponentStream<COMP_TYPE> rewindIteration() {
		stack.clear();
		if (includeRoot) {
			stack.push(root);
		} else {
			for (PComponent child : root.getChildren()) {
				stack.addFirst(child);
			}
		}
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
	
	@SuppressWarnings("unchecked")
	@Override
	public COMP_TYPE peekNext() {
		Deque<PComponent> stack = new ArrayDeque<>(this.stack);
		while (!stack.isEmpty()) {
			PComponent current = stack.pop();
			for (PComponent child : current.getChildren()) {
				stack.addFirst(child);
			}
			COMP_TYPE comp = (COMP_TYPE) current;
			if (isAllowed(comp)) {
				return comp;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public COMP_TYPE getNext() {
		while (!stack.isEmpty()) {
			PComponent current = stack.pop();
			for (PComponent child : current.getChildren()) {
				stack.addFirst(child);
			}
			COMP_TYPE comp = (COMP_TYPE) current;
			if (isAllowed(comp)) {
				return comp;
			}
		}
		return null;
	}
	
}