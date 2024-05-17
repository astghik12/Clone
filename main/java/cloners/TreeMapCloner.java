package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.Map;
import java.util.TreeMap;

public class TreeMapCloner implements FastCloner {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(final Object object, final DeepCloner cloner, final Map<Object, Object> clones) {
        final TreeMap<Object, Object> original = (TreeMap) object;
        final TreeMap cloned = new TreeMap(original.comparator());
        for (final Map.Entry e : original.entrySet()) {
            cloned.put(cloner.deepClone(e.getKey(), clones), cloner.deepClone(e.getValue(), clones));
        }
        return cloned;
    }
}