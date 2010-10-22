package android.os;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom (simplified) implementation of an Android Bundle. It uses
 * a HashMap as underlying implementation and does not check the
 * consistency of data (casting).
 * 
 * @author erickok
 */
public class Bundle {

	Map<String, Object> hash;
	
	public Bundle() {
		hash = new HashMap<String, Object>();
	}

	public void putBoolean(String key, boolean value) {
		hash.put(key, new Boolean(value));
	}

	public void putInt(String key, int value) {
		hash.put(key, new Integer(value));
	}

	public void putString(String key, String value) {
		hash.put(key, value);
	}

	public void putParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
		hash.put(key, value);
	}

	public boolean getBoolean(String key) {
		return (Boolean) hash.get(key);
	}

	public int getInt(String key) {
		return (Integer) hash.get(key);
	}

	public String getString(String key) {
		return (String) hash.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T extends Parcelable> ArrayList<T> getParcelableArrayList(String key) {
		return (ArrayList<T>) hash.get(key);
	}

}
