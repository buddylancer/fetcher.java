package Bula.Objects;

import java.util.ArrayList;

public class TArrayListBase extends Bula.Meta {
	
	public TArrayListBase() { }
	
	public TArrayListBase(Object[] $items) {
		for (Object $item : $items)
			content.add($item);
	}
	
	private ArrayList content = new ArrayList();

	public boolean add(Object value) { return content.add(value); }

	public Object get(int pos) { return content.get(pos); }

	public void set(int pos, Object value) { content.set(pos, value); }

	public int size() { return content.size(); }

	public Object[] toArray() { return content.toArray(); }

	public Object[] toArray(Object[] type) { return content.toArray(type); }

 	
}
