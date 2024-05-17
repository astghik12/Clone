package fastClone;

import clone.CloneUtil;
import clone.DeepCloner;

import java.util.Map;

public class FastClonerDeepCloner implements DeepCloner {
    private final FastCloner fastCloner;
    private final CloneUtil cloneUtil;

    public FastClonerDeepCloner(FastCloner fastCloner, CloneUtil cloneUtil) {
        this.fastCloner = fastCloner;
        this.cloneUtil = cloneUtil;
    }

    public <T> T deepClone(T object, Map<Object, Object> clones) {
        @SuppressWarnings("unchecked") T clone = (T) fastCloner.clone(object, cloneUtil::cloneInternally, clones);
        if (clones != null) clones.put(object, clone);
        return clone;
    }
}
