package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.LinkedList;
import java.util.Map;

public class LinkedListCloner implements FastCloner {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(final Object object, final DeepCloner cloner, final Map<Object, Object> clones) {
        final LinkedList original = (LinkedList) object;
        final LinkedList cloned = new LinkedList();
        for (final Object obj : original) {
            cloned.add(cloner.deepClone(obj, clones));
        }
        return cloned;
    }
}
