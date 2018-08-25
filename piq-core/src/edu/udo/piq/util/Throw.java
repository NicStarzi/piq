package edu.udo.piq.util;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Throw {
	
	public static <E> E ifNull(E obj, String optionalMsg)
			throws IllegalArgumentException
	{
		return Throw.ifNull(obj, () -> optionalMsg);
	}
	
	public static <E> E ifNull(E obj, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (obj == null) {
			Throw.iae(optionalMsg);
		}
		return obj;
	}
	
	public static void ifNotNull(Object obj, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (obj != null) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifEqual(Object guard, Object value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (guard.equals(value)) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifNotEqual(Object guard, Object value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (!guard.equals(value)) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifNotEqual(long guard, long value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (guard != value) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifNotEqual(double guard, double value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (guard != value) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifEqual(long guard, long value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (value == guard) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifEqual(double guard, double value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (value == guard) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifLess(long min, long value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (value < min) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifLess(double min, double value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (value < min) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifLessOrEqual(long min, long value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifLess(min, value, optionalMsg);
		Throw.ifEqual(min, value, optionalMsg);
	}
	
	public static void ifLessOrEqual(double min, double value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifLess(min, value, optionalMsg);
		Throw.ifEqual(min, value, optionalMsg);
	}
	
	public static void ifMore(long max, long value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (value > max) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifMore(double max, double value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (value > max) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifMoreOrEqual(long max, long value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifMore(max, value, optionalMsg);
		Throw.ifEqual(max, value, optionalMsg);
	}
	
	public static void ifMoreOrEqual(double max, double value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifMore(max, value, optionalMsg);
		Throw.ifEqual(max, value, optionalMsg);
	}
	
	public static void ifWithin(long min, long max, long value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (value >= min && value <= max) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifWithin(double min, double max, double value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (value >= min && value <= max) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static void ifNotWithinArrayBounds(int[] arr, long index, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifNotWithin(0, arr.length - 1, index, optionalMsg);
	}
	
	public static void ifNotWithinArrayBounds(double[] arr, long index, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifNotWithin(0, arr.length - 1, index, optionalMsg);
	}
	
	public static void ifNotWithinArrayBounds(boolean[] arr, long index, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifNotWithin(0, arr.length - 1, index, optionalMsg);
	}
	
	public static <E> void ifNotWithinArrayBounds(E[] arr, long index, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifNotWithin(0, arr.length - 1, index, optionalMsg);
	}
	
	public static void ifNotWithinSize(Collection<?> col, long index, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifNotWithin(0, col.size() - 1, index, optionalMsg);
	}
	
	public static void ifNotWithin(long min, long max, long value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifLess(min, value, optionalMsg);
		Throw.ifMore(max, value, optionalMsg);
	}
	
	public static void ifNotWithin(double min, double max, double value, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		Throw.ifLess(min, value, optionalMsg);
		Throw.ifMore(max, value, optionalMsg);
	}
	
	public static void ifTrue(boolean condition, Supplier<String> optionalMsg)
			throws IllegalStateException
	{
		if (condition) {
			Throw.ise(optionalMsg);
		}
	}
	
	public static void ifFalse(boolean condition, Supplier<String> optionalMsg)
			throws IllegalStateException
	{
		if (!condition) {
			Throw.ise(optionalMsg);
		}
	}
	
	public static <E> void ifTrue(E elem, Predicate<E> condition, Supplier<String> optionalMsg)
			throws IllegalStateException
	{
		if (condition.test(elem)) {
			Throw.ise(optionalMsg);
		}
	}
	
	public static <E> void ifFalse(E elem, Predicate<E> condition, Supplier<String> optionalMsg)
			throws IllegalStateException
	{
		if (!condition.test(elem)) {
			Throw.ise(optionalMsg);
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static void ifContains(Collection<?> collection, Object obj, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (collection.contains(obj)) {
			Throw.iae(optionalMsg);
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static void ifNotContains(Collection<?> collection, Object obj, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		if (!collection.contains(obj)) {
			Throw.iae(optionalMsg);
		}
	}
	
	public static <E> void ifContains(E[] array, E elem, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		for (E other : array) {
			if (Objects.equals(elem, other)) {
				Throw.iae(optionalMsg);
				return;
			}
		}
	}
	
	public static <E> void ifNotContains(E[] array, E elem, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		for (E other : array) {
			if (Objects.equals(elem, other)) {
				return;
			}
		}
		Throw.iae(optionalMsg);
	}
	
	public static void ifContains(int[] array, int elem, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		for (int i = 0; i < array.length; i++) {
			if (array[i] == elem) {
				Throw.iae(optionalMsg);
				return;
			}
		}
	}
	
	public static void ifNotContains(int[] array, int elem, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		for (int i = 0; i < array.length; i++) {
			if (array[i] == elem) {
				return;
			}
		}
		Throw.iae(optionalMsg);
	}
	
	public static void ifContains(double[] array, double elem, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		for (int i = 0; i < array.length; i++) {
			if (array[i] == elem) {
				Throw.iae(optionalMsg);
				return;
			}
		}
	}
	
	public static void ifNotContains(double[] array, double elem, Supplier<String> optionalMsg)
			throws IllegalArgumentException
	{
		for (int i = 0; i < array.length; i++) {
			if (array[i] == elem) {
				return;
			}
		}
		Throw.iae(optionalMsg);
	}
	
	public static <T> void ifAnyMatch(Collection<T> collection, Predicate<T> cond, Supplier<String> msg)
			throws IllegalArgumentException
	{
		for (T t : collection) {
			if (cond.test(t)) {
				Throw.iae(msg);
			}
		}
	}
	
	public static <T> void ifNoneMatch(Collection<T> collection, Predicate<T> cond, Supplier<String> msg)
			throws IllegalArgumentException
	{
		for (T t : collection) {
			if (cond.test(t)) {
				return;
			}
		}
		Throw.iae(msg);
	}
	
	public static <T> void ifAnyMatch(T[] arr, Predicate<T> cond, Supplier<String> msg)
			throws IllegalArgumentException
	{
		for (T t : arr) {
			if (cond.test(t)) {
				Throw.iae(msg);
			}
		}
	}
	
	public static <T> void ifNoneMatch(T[] arr, Predicate<T> cond, Supplier<String> msg)
			throws IllegalArgumentException
	{
		for (T t : arr) {
			if (cond.test(t)) {
				return;
			}
		}
		Throw.iae(msg);
	}
	
	public static void ifAnyMatch(int[] arr, Predicate<Integer> cond, Supplier<String> msg)
			throws IllegalArgumentException
	{
		for (int i : arr) {
			if (cond.test(i)) {
				Throw.iae(msg);
			}
		}
	}
	
	public static void ifNoneMatch(int[] arr, Predicate<Integer> cond, Supplier<String> msg)
			throws IllegalArgumentException
	{
		for (int i : arr) {
			if (cond.test(i)) {
				return;
			}
		}
		Throw.iae(msg);
	}
	
	public static void ifAnyMatch(double[] arr, Predicate<Double> cond, Supplier<String> msg)
			throws IllegalArgumentException
	{
		for (double i : arr) {
			if (cond.test(i)) {
				Throw.iae(msg);
			}
		}
	}
	
	public static void ifNoneMatch(double[] arr, Predicate<Double> cond, Supplier<String> msg)
			throws IllegalArgumentException
	{
		for (double i : arr) {
			if (cond.test(i)) {
				return;
			}
		}
		Throw.iae(msg);
	}
	
	public static void always(Supplier<String> optionalMsg)
			throws IllegalOperationException
	{
		if (optionalMsg == null) {
			throw new IllegalOperationException();
		}
		throw new IllegalOperationException(optionalMsg.get());
	}
	
	@SuppressWarnings("unchecked") // this is safe because of the instanceof check
	public static <E> E ifTypeCastFails(Object obj, Class<E> clazz, Supplier<String> optionalMsg) {
		Throw.ifNull(obj, optionalMsg);
		if (clazz.isInstance(obj)) {
			return (E) obj;
		} else {
			Throw.iae(optionalMsg);
			// this can never happen!
			throw new UnsupportedOperationException();
		}
	}
	
	private static void iae(Supplier<String> msg) throws IllegalArgumentException {
		if (msg == null) {
			throw new IllegalArgumentException();
		}
		throw new IllegalArgumentException(msg.get());
	}
	
	/**
	 * Throws an IllegalStateException with msg as argument.
	 * @param msg
	 * @throws IllegalStateException
	 */
	private static void ise(Supplier<String> msg) throws IllegalStateException {
		if (msg == null) {
			throw new IllegalStateException();
		}
		throw new IllegalStateException(msg.get());
	}
	
	public static class IllegalOperationException extends RuntimeException {
		private static final long serialVersionUID = -101150367304790537L;
		
		public IllegalOperationException() {
			super();
		}
		
		public IllegalOperationException(String msg) {
			super(msg);
		}
	}
	
}