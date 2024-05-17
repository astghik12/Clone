package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.HashMap;
import java.util.Map;

public class HashMapCloner implements FastCloner {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(final Object object, final DeepCloner cloner, final Map<Object, Object> clones) {
        final HashMap<Object, Object> originalMap = (HashMap) object;
        final HashMap clonedMap = new HashMap();
        for (final Map.Entry e : originalMap.entrySet()) {
            clonedMap.put(cloner.deepClone(e.getKey(), clones), cloner.deepClone(e.getValue(), clones));
        }
        return clonedMap;
    }
}