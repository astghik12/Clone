package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapCloner implements FastCloner {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(final Object object, final DeepCloner cloner, final Map<Object, Object> clones) {
        final LinkedHashMap<?, ?> originalHashMap = (LinkedHashMap) object;
        final LinkedHashMap clonedHashMap = new LinkedHashMap();
        for (final Map.Entry entry : originalHashMap.entrySet()) {
            clonedHashMap.put(cloner.deepClone(entry.getKey(), clones), cloner.deepClone(entry.getValue(), clones));
        }
        return clonedHashMap;
    }
}
