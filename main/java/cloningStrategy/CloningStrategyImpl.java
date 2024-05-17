package cloningStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class CloningStrategyImpl {
    public static CloningStrategy annotatedField(final Class<? extends Annotation> annotationClass, final CloningStrategy.Strategy strategy) {
        return new CloningStrategy() {
            public Strategy strategyFor(Object toBeCloned, Field field) {
                if (toBeCloned == null) return Strategy.IGNORE;
                if (field.getDeclaredAnnotation(annotationClass) != null) return strategy;
                return Strategy.IGNORE;
            }
        };
    }
}
