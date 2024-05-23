package instantiator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CustomInstantiator<T> {

    private final Class<T> clazz;
    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULT_VALUES = new HashMap<>();

    static {
        PRIMITIVE_DEFAULT_VALUES.put(boolean.class, false);
        PRIMITIVE_DEFAULT_VALUES.put(byte.class, (byte) 0);
        PRIMITIVE_DEFAULT_VALUES.put(short.class, (short) 0);
        PRIMITIVE_DEFAULT_VALUES.put(int.class, 0);
        PRIMITIVE_DEFAULT_VALUES.put(long.class, 0L);
        PRIMITIVE_DEFAULT_VALUES.put(float.class, 0.0f);
        PRIMITIVE_DEFAULT_VALUES.put(double.class, 0.0);
        PRIMITIVE_DEFAULT_VALUES.put(char.class, '\u0000');
    }

    public CustomInstantiator(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Creates a new instance of the class using the default constructor.
     *
     * @return A new instance of the class.
     * @throws NoSuchMethodException if no suitable constructor found.
     */
    public T newInstance() throws NoSuchMethodException {
        try {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                try {
                    Class<?>[] parameterTypes = constructor.getParameterTypes();
                    Object[] args = new Object[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        args[i] = getDefaultValue(parameterTypes[i]);
                    }
                    return clazz.cast(constructor.newInstance(args));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                }
            }
            // If no constructor succeeded, throw an exception
            throw new NoSuchMethodException("No suitable constructor found for class " + clazz.getName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Helper method to get default values for primitive types.
     *
     * @param type The type for which to get the default value.
     * @return The default value for the specified type.
     */
    private Object getDefaultValue(Class<?> type) {
        return PRIMITIVE_DEFAULT_VALUES.getOrDefault(type, null);
    }
}
