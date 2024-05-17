package fastClone;

import clone.DeepCloner;

import java.util.Map;

public interface FastCloner {
    Object clone(Object t, DeepCloner cloner, Map<Object, Object> clones);
}
