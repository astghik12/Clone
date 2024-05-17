package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.ArrayList;
import java.util.Map;

public class ArrayListCloner implements FastCloner {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(final Object object, final DeepCloner cloner, final Map<Object, Object> clones) {
        ArrayList originalArray = (ArrayList) object;
        ArrayList clonedArray = new ArrayList(originalArray.size());
        for (Object o : originalArray) {
            clonedArray.add(cloner.deepClone(o, clones));
        }
        return clonedArray;
    }
}
