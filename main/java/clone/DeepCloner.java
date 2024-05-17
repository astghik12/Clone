package clone;

import java.util.Map;

public interface DeepCloner {
    /**
     * deep clones o
     *
     * @param object the object to be deep cloned
     * @param clones pass on the same map from IFastCloner
     * @param <T>    the type of o
     * @return a clone of o
     */
    <T> T deepClone(final T object, final Map<Object, Object> clones);
}
