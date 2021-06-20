package Bula.Objects;

import java.util.*;

public class THashtableBase {
	private HashMap content = new HashMap();

	public void add(String key, Object value) { content.put(key, value); }

	public Object get(String key) { return content.get(key); }

	public void put(String key, Object value) { content.put(key, value); }

	public void remove(String key) { content.remove(key); }

	public int size() { return content.size(); }

	public boolean containsKey(String key) { return content.containsKey(key); }

	public Set keys() { return content.keySet(); }
}
