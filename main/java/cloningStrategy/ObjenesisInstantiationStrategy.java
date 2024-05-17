package cloningStrategy;

import clone.Instantiation;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

public class ObjenesisInstantiationStrategy implements Instantiation {
    private final Objenesis objenesis = new ObjenesisStd();

    public <T> T newInstance(Class<T> c) {
        return objenesis.newInstance(c);
    }

    public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> c) {
        return objenesis.getInstantiatorOf(c);
    }

    private static final ObjenesisInstantiationStrategy instance = new ObjenesisInstantiationStrategy();

    public static ObjenesisInstantiationStrategy getInstance() {
        return instance;
    }
}
