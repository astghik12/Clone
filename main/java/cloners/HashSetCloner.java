package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.HashSet;
import java.util.Map;

public class HashSetCloner implements FastCloner {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(final Object object, final DeepCloner cloner, final Map<Object, Object> clones) {
        final HashSet originalSet = (HashSet) object;
        final HashSet clonedSet = new HashSet();
        for (final Object obj : originalSet) {
            clonedSet.add(cloner.deepClone(obj, clones));
        }
        return clonedSet;
    }
}