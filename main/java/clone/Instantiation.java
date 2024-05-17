package clone;

import org.springframework.objenesis.instantiator.ObjectInstantiator;

public interface Instantiation {
    <T> T newInstance(final Class<T> c);

    <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> c);
}
