package edu.udo.piq;

import java.util.Map;

/**
 * <p>A {@link UserDataStore} stores values bound to {@link UserDataKey UserDataKeys}. 
 * Every {@link PRoot} gives access to one {@link PRoot#getUserDataStore() data store} 
 * which can be used by applications to store arbitrary user data across instantiations.
 * 
 * <p>Here is an example of how data stores can be used to store the last 
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
 */
public interface UserDataStore {
	
	/**
	 * <p>Binds the given value to the given key within this data store and returns 
	 * the value that was previously bound to this key.
	 * <p>If no value was bound to the given key prior, the 
	 * {@link UserDataKey#getDefaultValue() default value} of the key is returned.
	 * <p>The behaviour of this method is undefined if the previously bound value 
	 * is not an instance of the keys {@link UserDataKey#getDataType() data type}. 
	 *  
	 * @param key							a non-null UserDataKey.
	 * @param value							any value. Can be {@code null}.
	 * @throws IllegalArgumentException		if {@code key} is {@code null}.
	 * @return								the previously bound value or the {@link UserDataKey#getDefaultValue() default value} of {@code key}.
	 */
	public <T> T set(UserDataKey<T> key, T value);
	
	/**
	 * <p>Returns the most recently {@link #set(UserDataKey, Object) bound} value for 
	 * the given key within this data store.
	 * <p>If no value was bound to the given key prior, the 
	 * {@link UserDataKey#getDefaultValue() default value} of the key is returned.
	 * <p>The behaviour of this method is undefined if the currently bound value 
	 * is not an instance of the keys {@link UserDataKey#getDataType() data type}. 
	 * 
	 * @param key							a non-null UserDataKey.
	 * @throws IllegalArgumentException		if {@code key} is {@code null}.
	 * @return								the currently bound value or the {@link UserDataKey#getDefaultValue() default value} of {@code key}.
	 */
	public <T> T get(UserDataKey<T> key);
	
	/**
	 * <p>Returns a {@link Map} representing this data store.
	 * <p>The keys for the map will be identical to the 
	 * {@link UserDataKey#getKeyName() identifiers} of the bound 
	 * {@link UserDataKey UserDataKeys}.
	 * @return								a non-null {@link Map} representing this data store.
	 */
	public Map<String, Object> asMap();
	
}