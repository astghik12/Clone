package cloners;

import clone.DeepCloner;
import fastClone.FastCloner;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueCloner implements FastCloner {
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object clone(Object object, DeepCloner cloner, Map<Object, Object> clones) {
        ConcurrentLinkedQueue original = (ConcurrentLinkedQueue) object;
        ConcurrentLinkedQueue cloned = new ConcurrentLinkedQueue();
        for (Object obj : original) {
            cloned.add(cloner.deepClone(obj, clones));
        }
        return cloned;
    }
}
