package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.Map;
import java.util.TreeSet;

public class TreeSetCloner implements FastCloner {
    @Override
    @SuppressWarnings("unchecked")
    public Object clone(Object object, DeepCloner cloner, Map<Object, Object> clones) {
        TreeSet<?> originalTreeSet = (TreeSet<?>) object;
        TreeSet cloned = new TreeSet(originalTreeSet.comparator());
        for (Object obj : originalTreeSet) {
            cloned.add(cloner.deepClone(obj, clones));
        }
        return cloned;
    }
}
