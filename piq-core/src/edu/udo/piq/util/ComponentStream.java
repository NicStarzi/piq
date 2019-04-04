package edu.udo.piq.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;

import edu.udo.piq.PComponent;
import edu.udo.piq.PRoot;

/**
 * <p>A tool for iterating over components within a GUI tree in an orderly fashion.
 * A component stream allows to {@link #require(Predicate) filter} the kinds of
 * components that other methods such as {@link #forEach(Consumer)} are working on.
 * Besides performing {@link #forEachWhile(Predicate) bulk operations} on subsets of
 * components within a GUI tree, a component stream also supports more sophisticated
 * queries via the {@link #accumulate(Supplier, Function, BiFunction)} and
 * {@link #collect(Supplier, BiConsumer)} methods.</p>
 * 
 * <p>The most important implementations are {@link AncestorStream} which will
 * operate only on the ancestors of a component up to the {@link PRoot root} and the
 * {@link BreadthFirstDescendantStream} and {@link DepthFirstDescendantStream} which
 * operate on the entire subtree below a component. Instances of these streams can
 * be constructed with the use of the public constructors or by calling
 * {@link PComponent#getAncestors() convenience methods in PComponent}</p>
 * 
 * <p>Component streams can be converted to regular {@link Collection collections}
 * by using the {@link #toList()} or {@link #toSet()} methods.</p>
 * 
 * @author Nic Starzi
 * 
 * @param <COMPONENT_TYPE> The type of {@link PComponent components} that this stream operates on
 */
public interface ComponentStream<COMPONENT_TYPE extends PComponent> {
	
	public ComponentStream<COMPONENT_TYPE> require(Predicate<COMPONENT_TYPE> condition);
	
	public ComponentStream<COMPONENT_TYPE> rewindIteration();
	
	public ComponentStream<COMPONENT_TYPE> resetAllFilters();
	
	public boolean isAllowed(COMPONENT_TYPE component);
	
	public COMPONENT_TYPE peekNext();
	
	public COMPONENT_TYPE getNext();
	
	public default boolean contains(PComponent component) {
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			if (current == component) {
				return true;
			}
		}
		return false;
	}
	
	public default ComponentStream<COMPONENT_TYPE> forEach(Consumer<COMPONENT_TYPE> action) {
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			action.accept(current);
		}
		return this;
	}
	
	public default ComponentStream<COMPONENT_TYPE> forEachUntil(Predicate<COMPONENT_TYPE> action) {
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			if (action.test(current)) {
				return this;
			}
		}
		return this;
	}
	
	public default ComponentStream<COMPONENT_TYPE> forEachWhile(Predicate<COMPONENT_TYPE> action) {
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			if (!action.test(current)) {
				return this;
			}
		}
		return this;
	}
	
	public default ComponentStream<COMPONENT_TYPE> run(Runnable code) {
		code.run();
		return this;
	}
	
	public default ComponentStream<COMPONENT_TYPE> exclude(Predicate<COMPONENT_TYPE> condition) {
		return require(comp -> !condition.test(comp));
	}
	
	public default ComponentStream<COMPONENT_TYPE> skip(int count) {
		for (	COMPONENT_TYPE next = getNext();
				next != null && count > 0;
				next = getNext(), count--) {/*Intentionally left blank*/}
		return this;
	}
	
	public default boolean hasRemaining() {
		return peekNext() != null;
	}
	
	public default COMPONENT_TYPE getNextMatching(Predicate<? super COMPONENT_TYPE> condition) {
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			if (condition.test(current)) {
				return current;
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public default <SUB_TYPE extends COMPONENT_TYPE> SUB_TYPE getNextOfType(Class<SUB_TYPE> clazz) {
		return (SUB_TYPE) getNextMatching(clazz::isInstance);
	}
	
	public default COMPONENT_TYPE getLastMatching(Predicate<? super COMPONENT_TYPE> condition) {
		COMPONENT_TYPE last = null;
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			if (condition.test(current)) {
				last = current;
			}
		}
		return last;
	}
	
	@SuppressWarnings("unchecked")
	public default <SUB_TYPE extends COMPONENT_TYPE> SUB_TYPE getLastOfType(Class<SUB_TYPE> clazz) {
		return (SUB_TYPE) getLastMatching(clazz::isInstance);
	}
	
	public default ComponentStream<COMPONENT_TYPE> without(PComponent excludedComponent) {
		return require(component -> component != excludedComponent);
	}
	
	public default ComponentStream<COMPONENT_TYPE> withId(String id) {
		return require(component -> Objects.equals(component.getID(), id));
	}
	
	@SuppressWarnings("unchecked")
	public default <SUB_TYPE extends COMPONENT_TYPE> ComponentStream<SUB_TYPE> ofType(Class<SUB_TYPE> clazz) {
		return (ComponentStream<SUB_TYPE>) require(clazz::isInstance);
	}
	
	public default <V> V accumulate(
			Supplier<V> initialValue,
			Function<COMPONENT_TYPE, V> generator,
			BiFunction<V, V, V> accumulator)
	{
		V val = initialValue.get();
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			V newVal = generator.apply(current);
			val = accumulator.apply(val, newVal);
		}
		return val;
	}
	
	public default int accumulateInt(
			int initialValue,
			ToIntFunction<COMPONENT_TYPE> generator,
			IntBinaryOperator accumulator)
	{
		int val = initialValue;
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			int newVal = generator.applyAsInt(current);
			val = accumulator.applyAsInt(val, newVal);
		}
		return val;
	}
	
	public default double accumulateDouble(
			double initialValue,
			ToDoubleFunction<COMPONENT_TYPE> generator,
			DoubleBinaryOperator accumulator)
	{
		double val = initialValue;
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			double newVal = generator.applyAsDouble(current);
			val = accumulator.applyAsDouble(val, newVal);
		}
		return val;
	}
	
	public static interface BooleanBinaryOperator {
		public boolean applyAsBoolean(boolean b1, boolean b2);
	}
	
	public default boolean accumulateBoolean(
			boolean initialValue,
			Predicate<COMPONENT_TYPE> generator,
			BooleanBinaryOperator accumulator)
	{
		boolean val = initialValue;
		for (	COMPONENT_TYPE current = getNext();
				current != null;
				current = getNext())
		{
			boolean newVal = generator.test(current);
			val = accumulator.applyAsBoolean(val, newVal);
		}
		return val;
	}
	
	public default <ACCUMULATOR_TYPE> ACCUMULATOR_TYPE collect(
			Supplier<ACCUMULATOR_TYPE> initializer,
			BiConsumer<ACCUMULATOR_TYPE, COMPONENT_TYPE> accumulator)
	{
		ACCUMULATOR_TYPE acc = initializer.get();
		forEach(component -> accumulator.accept(acc, component));
		return acc;
	}
	
	public default <RESULT_TYPE, ACCUMULATOR_TYPE> RESULT_TYPE collect(
			Supplier<ACCUMULATOR_TYPE> initializer,
			BiConsumer<ACCUMULATOR_TYPE, COMPONENT_TYPE> accumulator,
			Function<ACCUMULATOR_TYPE, RESULT_TYPE> transformer)
	{
		ACCUMULATOR_TYPE acc = initializer.get();
		forEach(component -> accumulator.accept(acc, component));
		return transformer.apply(acc);
	}
	
	/**
	 * <p>Creates and returns a new instance of {@link List} which is populated with
	 * all remaining components within this stream in the same order as they would
	 * be consumed by a call to the {@link #forEach(Consumer)} method.</p>
	 * 
	 * <p>The returned list will only contain components that pass all the filters
	 * of this stream. It will not contain any {@code null} pointers. The list will
	 * not reflect any changes to the GUI tree after its creation. The list will also
	 * be mutable.</p>
	 * 
	 * @return				a new List filled with the remaining components of this stream
	 * @see #toSet()
	 * @see #forEach(Consumer)
	 * @see #collect(Supplier, BiConsumer, Function)
	 */
	public default List<COMPONENT_TYPE> toList() {
		return collect(ArrayList::new, List::add);
	}
	
	public default Set<COMPONENT_TYPE> toSet() {
		return collect(HashSet::new, Set::add);
	}
	
}