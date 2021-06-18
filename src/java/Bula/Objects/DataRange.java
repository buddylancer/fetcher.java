package Bula.Objects;

import java.util.*;

public class DataRange extends HashMap {
    public String[] keys() {
        String[] $array = (String[])this.keySet().toArray(new String[] {});
        ArrayList $list = new ArrayList();
        Collections.addAll($list, $array);
        Collections.sort($list);
        return (String[])$list.toArray(new String[] {});
    }

    
    /* HashMap
    public String[] keys() {
        Set $keys = this.keySet();
        return (String[])this.keySet().toArray(new String[] {});
    }
    */
    
    public boolean contains(String $key) {
        return this.keySet().contains($key);
    }
}
