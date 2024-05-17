package cloningStrategy;

import java.lang.reflect.Field;

public interface CloningStrategy {
    enum Strategy {
        NULL_INSTEAD_OF_CLONE,
        SAME_INSTANCE_INSTEAD_OF_CLONE,
        IGNORE
    }

    Strategy strategyFor(Object toBeCloned, Field field);
}
