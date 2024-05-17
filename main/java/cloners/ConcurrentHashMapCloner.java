package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapCloner implements FastCloner {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(final Object object, final DeepCloner cloner, final Map<Object, Object> clones) {
        final ConcurrentHashMap<Object, Object> original = (ConcurrentHashMap) object;
        final ConcurrentHashMap cloned = new ConcurrentHashMap();
        for (final Map.Entry e : original.entrySet()) {
            cloned.put(cloner.deepClone(e.getKey(), clones), cloner.deepClone(e.getValue(), clones));
        }
        return cloned;
    }
}
