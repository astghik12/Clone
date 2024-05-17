package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.LinkedHashSet;
import java.util.Map;

public class LinkedHashSetCloner implements FastCloner {
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(final Object object, final DeepCloner cloner, final Map<Object, Object> clones) {
        final LinkedHashSet<?> original = (LinkedHashSet) object;
        final LinkedHashSet cloned = new LinkedHashSet();
        for (final Object obj : original) {
            cloned.add(cloner.deepClone(obj, clones));
        }
        return cloned;
    }
}