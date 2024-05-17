package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.EnumMap;
import java.util.Map;

public class EnumMapCloner implements FastCloner {
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(final Object object, final DeepCloner cloner, final Map<Object, Object> clones) {
        final EnumMap<? extends Enum<?>, ?> original = (EnumMap) object;
        final EnumMap cloned = new EnumMap(original);

        // clone the values
        for (final Map.Entry<? extends Enum<?>, ?> e : original.entrySet()) {
            // No need to clone the key, it is an Enum
            cloned.put(e.getKey(), cloner.deepClone(e.getValue(), clones));
        }
        return cloned;
    }
}