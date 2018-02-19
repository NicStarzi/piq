package edu.udo.piq.util;

import java.util.Collection;

public class ThrowException {
	
	public static <E> E ifNull(E obj, String optionalMsg)
			throws IllegalArgumentException
	{
		if (obj == null) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg));
		}
		return obj;
	}
	
	public static void ifNotNull(Object obj, String optionalMsg)
			throws IllegalArgumentException
	{
		if (obj != null) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, obj,
					"must be null but is not"));
		}
	}
	
	public static void ifEqual(Object guard, Object value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (guard.equals(value)) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "value(",
					value, ") equals (", guard, ")"));
		}
	}
	
	public static void ifNotEqual(Object guard, Object value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (!guard.equals(value)) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "(",
					value, ") not equal (", guard, ")"));
		}
	}
	
	public static void ifEqual(long guard, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (value == guard) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "value(",
					value, ") == (", guard, ")"));
		}
	}
	
	public static void ifEqual(double guard, double value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (value == guard) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "value(",
					value, ") == (", guard, ")"));
		}
	}
	
	public static void ifLess(long min, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (value < min) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "value(",
					value, ") < minimum(", min, ")"));
		}
	}
	
	public static void ifLess(double min, double value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (value < min) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "value(",
					value, ") < minimum(", min, ")"));
		}
	}
	
	public static void ifLessOrEqual(long min, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifLess(min, value, optionalMsg);
		ThrowException.ifEqual(min, value, optionalMsg);
	}
	
	public static void ifLessOrEqual(double min, double value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifLess(min, value, optionalMsg);
		ThrowException.ifEqual(min, value, optionalMsg);
	}
	
	public static void ifMore(long max, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (value > max) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "value(",
					value, ") > maximum(", max, ")"));
		}
	}
	
	public static void ifMore(double max, double value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (value > max) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "value(",
					value, ") > maximum(", max, ")"));
		}
	}
	
	public static void ifMoreOrEqual(long max, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifMore(max, value, optionalMsg);
		ThrowException.ifEqual(max, value, optionalMsg);
	}
	
	public static void ifMoreOrEqual(double max, double value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifMore(max, value, optionalMsg);
		ThrowException.ifEqual(max, value, optionalMsg);
	}
	
	public static void ifWithin(long min, long max, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (value >= min && value <= max) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "minimum(", min, ") <= value(",
					value, ") <= maximum(", max, ")"));
		}
	}
	
	public static void ifWithin(double min, double max, double value, String optionalMsg)
			throws IllegalArgumentException
	{
		if (value >= min && value <= max) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "minimum(", min, ") <= value(",
					value, ") <= maximum(", max, ")"));
		}
	}
	
	public static void ifNotWithin(int[] arr, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifNotWithin(0, arr.length - 1, value, optionalMsg);
	}
	
	public static void ifNotWithin(double[] arr, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifNotWithin(0, arr.length - 1, value, optionalMsg);
	}
	
	public static void ifNotWithin(boolean[] arr, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifNotWithin(0, arr.length - 1, value, optionalMsg);
	}
	
	public static void ifNotWithin(Object[] arr, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifNotWithin(0, arr.length - 1, value, optionalMsg);
	}
	
	public static void ifNotWithin(Collection<?> col, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifNotWithin(0, col.size() - 1, value, optionalMsg);
	}
	
	public static void ifNotWithin(long min, long max, long value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifLess(min, value, optionalMsg);
		ThrowException.ifMore(max, value, optionalMsg);
	}
	
	public static void ifNotWithin(double min, double max, double value, String optionalMsg)
			throws IllegalArgumentException
	{
		ThrowException.ifLess(min, value, optionalMsg);
		ThrowException.ifMore(max, value, optionalMsg);
	}
	
	public static void ifTrue(boolean condition, String optionalMsg)
			throws IllegalStateException
	{
		if (condition) {
			ThrowException.ise(ThrowException.createErrorMsg(optionalMsg, "success condition not met"));
		}
	}
	
	public static void ifFalse(boolean condition, String optionalMsg)
			throws IllegalStateException
	{
		if (!condition) {
			ThrowException.ise(ThrowException.createErrorMsg(optionalMsg, "error condition met"));
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static void ifIncluded(Collection<?> collection, Object obj, String optionalMsg)
			throws IllegalArgumentException
	{
		if (collection.contains(obj)) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, obj, " is included in ", collection));
		}
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public static void ifExcluded(Collection<?> collection, Object obj, String optionalMsg)
			throws IllegalArgumentException
	{
		if (!collection.contains(obj)) {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, obj, " is missing from ", collection));
		}
	}
	
	public static void always(String optionalMsg)
			throws IllegalOperationException
	{
		if (optionalMsg == null) {
			throw new IllegalOperationException();
		}
		throw new IllegalOperationException(optionalMsg);
	}
	
	@SuppressWarnings("unchecked") // this is safe because of the instanceof check
	public static <E> E ifTypeCastFails(Object obj, Class<E> clazz, String optionalMsg) {
		ThrowException.ifNull(obj, optionalMsg);
		if (clazz.isInstance(obj)) {
			return (E) obj;
		} else {
			ThrowException.iae(ThrowException.createErrorMsg(optionalMsg, "'", obj, " '", "is not of type", clazz));
			// this can never happen!
			throw new UnsupportedOperationException();
		}
	}
	
	private static void iae(String msg) throws IllegalArgumentException {
		if (msg == null) {
			throw new IllegalArgumentException();
		}
		throw new IllegalArgumentException(msg);
	}
	
	/**
	 * Throws an IllegalStateException with msg as argument.
	 * @param msg
	 * @throws IllegalStateException
	 */
	private static void ise(String msg) throws IllegalStateException {
		if (msg == null) {
			throw new IllegalStateException();
		}
		throw new IllegalStateException(msg);
	}
	
	/**
	 * Concatenates all the parts and puts the optional message at the
	 * end if it is not null.<br>
	 * @param optionalMsg		can be null
	 * @param parts				concatenated by StringBuilder
	 * @return					a non-null String
	 */
	private static String createErrorMsg(String optionalMsg, Object ... parts) {
		StringBuilder sb = new StringBuilder();
		if (optionalMsg != null) {
			sb.append(optionalMsg);
			sb.append("; ");
		}
		for (Object obj : parts) {
			sb.append(obj);
			sb.append(' ');
		}
		return sb.toString();
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