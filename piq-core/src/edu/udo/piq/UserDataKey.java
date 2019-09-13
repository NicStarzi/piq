package edu.udo.piq;

import java.io.Serializable;

import edu.udo.piq.util.Throw;

/**
 * <p>{@link UserDataKey UserDataKey's} are used to identify data stored within 
 * a {@link UserDataStore}. They are {@link #getDataType() typed} and 
 * {@link #getKeyName() named} like class attributes. 
 * <p>Data can be {@link UserDataStore#set(UserDataKey, Object) bound} to a 
 * key in a data store. Afterwards the key can be used to 
 * {@link UserDataStore#get(UserDataKey) access} the data store to retrieve 
 * previously bound data.
 * <p>A key can have a {@link #getDefaultValue() default value}. This value will 
 * be bound to the key the first time the key is used to access a data store.
 * 
 * <p>Here is an example of how UserDataKeys can be used to store the last 
 * used user-name in a login form. Whether or not the user-name should be 
 * remembered is also stored.
 * <pre>
public class LoginForm extends PPanel {
	
	static final UserDataKey<String> KEY_LAST_USER_NAME = 
			new UserDataKey<>("lastUserName", "");
	
	static final UserDataKey<Boolean> KEY_REMEMBER_ME = 
			new UserDataKey<>("rememberUserName", Boolean.FALSE);
	
	//... code to instantiate and control the login form
	
	protected void onAddedToUi(PRoot newRoot) {
		boolean rememberUserName = newRoot.getUserData(KEY_REMEMBER_ME);
		checkBoxRememberUserName.setChecked(rememberUserName);
		
		if (rememberUserName) {
			String userName = newRoot.getUserData(KEY_LAST_USER_NAME);
			textFieldUserName.setModelValue(userName);
		}
	}
	
	protected void onRemovedFromUi(PRoot oldRoot) {
		boolean rememberUserName = checkBoxRememberUserName.isChecked();
		oldRoot.setUserData(KEY_REMEMBER_ME, rememberUserName);
		
		String userName;
		if (rememberUserName) {
			userName = textFieldUserName.getText();
		} else {
			userName = "";
		}
		oldRoot.setUserData(KEY_LAST_USER_NAME, userName);
	}
	
}
 * </pre>
 * <p>The last user-name and whether or not it should be remembered can now be 
 * stored across instances of LoginForm. It can also easily be serialized to a 
 * configuration file for the application.
 * 
 * @param <T> the type of data that can be bound to this key.
 */
public class UserDataKey<T> implements Serializable {
	protected static final long serialVersionUID = 3700772721699225163L;
	
	/**
	 * <p>Either creates and returns an object of the same type as 
	 * {@code dataCls} or returns {@code null}.
	 * <p> A non-null value will be returned for all wrappers of 
	 * primitive data types and for Strings.
	 * @param dataCls		a non {@code null} type
	 * @return				{@code null} or an object of type {@code dataCls}
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T createDefault(Class<T> dataCls) {
		if (dataCls == Byte.class) {
			return (T) Byte.valueOf((byte) 0);
		} else if (dataCls == Short.class) {
			return (T) Short.valueOf((short) 0);
		} else if (dataCls == Integer.class) {
			return (T) Integer.valueOf(0);
		} else if (dataCls == Long.class) {
			return (T) Long.valueOf(0L);
		} else if (dataCls == Float.class) {
			return (T) Float.valueOf(0F);
		} else if (dataCls == Double.class) {
			return (T) Double.valueOf(0);
		} else if (dataCls == Boolean.class) {
			return (T) Boolean.valueOf(true);
		} else if (dataCls == Character.class) {
			return (T) Character.valueOf(' ');
		} else if (dataCls == String.class) {
			return (T) "";
		}
		return null;
	}
	
	/**
	 * Must not be null (check in constructor). Identifies the UserDataKey uniquely (used in equals & hashCode).
	 */
	protected final String name;
	/**
	 * Type of data that can be stored with this key. Must not be null (check in constructor). Not part of equality.
	 */
	protected final Class<T> dataCls;
	/**
	 * Default value associated with this key. May be null.
	 */
	protected final T defVal;
	
	/**
	 * <p>Creates a new key with the given {@link #getKeyName() name} and the given 
	 * {@link #getDefaultValue() default value}. The {@link #getDataType() type} of 
	 * the stored data will be equivalent to the runtime type of the default value.
	 * 
	 * @param keyName		the unique identifier for this key. Should not be {@code null}.
	 * @param defaultValue	the default value associated with this key. Should not be {@code null}.
	 * @throws NullPointerException			if {@code defaultValue} is {@code null}
	 * @throws IllegalArgumentException		if {@code keyName} is {@code null}
	 * @see #UserDataKey(String, Class)
	 * @see #UserDataKey(String, Class, Object)
	 */
	@SuppressWarnings("unchecked")
	public UserDataKey(String keyName, T defaultValue) {
		this(keyName, (Class<T>) defaultValue.getClass(), defaultValue);
	}
	
	/**
	 * <p>Creates a new key with the given {@link #getKeyName() name} for the given 
	 * {@link #getDataType() data type}. The {@link #getDefaultValue() default value} 
	 * can be {@code null}.
	 * 
	 * @param keyName		the unique identifier for this key. Should not be {@code null}.
	 * @param dataType		the data type associated with this key. Should not be {@code null}.
	 * @param defaultValue	the default value associated with this key. Can be {@code null}.
	 * @throws IllegalArgumentException		if {@code keyName} is {@code null}
	 * @throws IllegalArgumentException		if {@code dataType} is {@code null}
	 * @see #UserDataKey(String, Object)
	 * @see #UserDataKey(String, Class)
	 */
	public UserDataKey(String keyName, Class<T> dataClass, T defaultValue) {
		Throw.ifNull(keyName, "keyName == null");
		Throw.ifNull(dataClass, "dataClass == null");
		name = keyName;
		dataCls = dataClass;
		defVal = defaultValue;
	}
	
	/**
	 * <p>Creates a new key with the given {@link #getKeyName() name} for the given 
	 * {@link #getDataType() data type}. A {@link #getDefaultValue() default value} 
	 * for this key will be generated according to the data type. For wrappers of 
	 * primitive data types the default value of the primitive data type will be 
	 * used. For Strings the default value is an empty String. For all other 
	 * reference types {@code null} will be the default value.
	 * 
	 * @param keyName		the unique identifier for this key. Should not be {@code null}.
	 * @param dataType		the data type associated with this key. Should not be {@code null}.
	 * @throws IllegalArgumentException		if {@code keyName} is {@code null}
	 * @throws IllegalArgumentException		if {@code dataType} is {@code null}
	 * @see #UserDataKey(String, Object)
	 * @see #UserDataKey(String, Class, Object)
	 */
	public UserDataKey(String keyName, Class<T> dataType) {
		this(keyName, dataType, UserDataKey.createDefault(dataType));
	}
	
	/**
	 * <p>Returns the unique identifier for this key. The name is used to differentiate 
	 * between instances of {@link UserDataKey}. Two keys are considered 
	 * {@link #equals(Object) equal} if their names are {@link String#equals(Object) equal}.
	 * <p>It is considered an error if two keys share the same name but have different 
	 * {@link #getDataType() types}. Any attempt to {@link UserDataStore#get(UserDataKey) access} 
	 * the same {@link UserDataStore data store} with both keys may result in exceptions being thrown.
	 * <p>The name of a key is immutable and never {@code null}. 
	 * @return		the unique identifier for this key. Is never {@code null}.
	 */
	public String getKeyName() {
		return name;
	}
	
	/**
	 * <p>Returns the type of data that can be {@link UserDataStore#set(UserDataKey, Object) bound} 
	 * to this key. When {@link UserDataStore#get(UserDataKey) accessing} a 
	 * {@link UserDataStore data store} with this key, any data that is returned will be of this type or 
	 * {@code null}.
	 * <p>The data type of a key is immutable and never {@code null}. 
	 * @return		the data type associated with this key. Is never {@code null}.
	 */
	public Class<T> getDataType() {
		return dataCls;
	}
	
	/**
	 * <p>Returns the default value associated with this key. This value will be 
	 * {@link UserDataStore#set(UserDataKey, Object) bound} to the key the first time 
	 * the key is used to {@link UserDataStore#get(UserDataKey) access} a 
	 * {@link UserDataStore data store}. The default value might be {@code null}.
	 * <p>This value should be treated as immutable.
	 * @return	the default value associated with this key. Might be {@code null}.
	 */
	public T getDefaultValue() {
		return defVal;
	}
	
	/**
	 * Returns the {@link Class#getSimpleName() simple name} of the 
	 * {@link #getDataType() data type} followed by a whitespace and the 
	 * {@link #getKeyName() name} of this {@link UserDataKey}.
	 */
	@Override
	public String toString() {
		String dataTypeName = getDataType().getSimpleName();
		String keyName = getKeyName();
		StringBuilder sb = new StringBuilder(dataTypeName.length() + 1 + keyName.length());
		sb.append(dataTypeName);
		sb.append(' ');
		sb.append(keyName);
		return sb.toString();
	}
	
	/**
	 * The hash code of a {@link UserDataKey} is identical to the 
	 * {@link String#hashCode() hash code} of its {@link #getKeyName() name}.
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	/**
	 * Two instances of {@link UserDataKey} are equal if their 
	 * {@link #getKeyName() names} are equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof UserDataKey)) {
			return false;
		}
		UserDataKey<?> other = (UserDataKey<?>) obj;
		return name.equals(other.name);
	}
	
}