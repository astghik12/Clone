package clone;

import cloners.*;
import cloningStrategy.CloningStrategy;
import cloningStrategy.ObjenesisInstantiationStrategy;
import exceptions.CloningException;
import fastClone.FastCloner;
import fastClone.FastClonerDeepCloner;
import org.springframework.objenesis.instantiator.ObjectInstantiator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

public class CloneUtil {
    private final Instantiation instantiationStrategy;
    private final Set<Class<?>> immutableClasses = Collections.synchronizedSet(new HashSet<>());
    private final Map<Class<?>, FastCloner> fastCloners = new ConcurrentHashMap<>();
    private final List<CloningStrategy> cloningStrategies = Collections.synchronizedList(new ArrayList<>());
    private final Map<Object, Object> ignoredInstances = Collections.synchronizedMap(new HashMap<>());
    private volatile boolean cloningEnabled = true;

    public CloneUtil() {
        this.instantiationStrategy = ObjenesisInstantiationStrategy.getInstance();
        initialize();
    }

    private void initialize() {
        registerKnownJdkImmutableClasses();
        registerFastCloners();
    }

    protected void registerFastCloners() {
        // Register fast cloners for common JDK collection classes
        registerFastCloner(ArrayList.class, new ArrayListCloner());
        registerFastCloner(LinkedList.class, new LinkedListCloner());
        registerFastCloner(HashSet.class, new HashSetCloner());
        registerFastCloner(HashMap.class, new HashMapCloner());
        registerFastCloner(TreeMap.class, new TreeMapCloner());
        registerFastCloner(TreeSet.class, new TreeSetCloner());
        registerFastCloner(LinkedHashMap.class, new LinkedHashMapCloner());
        registerFastCloner(ConcurrentHashMap.class, new ConcurrentHashMapCloner());
        registerFastCloner(ConcurrentLinkedQueue.class, new ConcurrentLinkedQueueCloner());
        registerFastCloner(EnumMap.class, new EnumMapCloner());
        registerFastCloner(LinkedHashSet.class, new LinkedHashSetCloner());

        // Register fast cloner for private classes
        ArrayListSubListCloner subListCloner = new ArrayListSubListCloner();
        registerInaccessibleClassToBeFastCloned("java.util.AbstractList$SubList", subListCloner);
        registerInaccessibleClassToBeFastCloned("java.util.ArrayList$SubList", subListCloner);
        registerInaccessibleClassToBeFastCloned("java.util.SubList", subListCloner);
        registerInaccessibleClassToBeFastCloned("java.util.RandomAccessSubList", subListCloner);
    }

    protected void registerInaccessibleClassToBeFastCloned(String className, FastCloner fastCloner) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            Class<?> subListClz = classLoader.loadClass(className);
            fastCloners.put(subListClz, fastCloner);
        } catch (ClassNotFoundException e) {
            // Ignore if the class is not found
        }
    }

    protected void registerKnownJdkImmutableClasses() {
        registerImmutable(String.class, Integer.class, Long.class, Boolean.class, Class.class, Float.class,
                Double.class, Character.class, Byte.class, Short.class, Void.class, BigDecimal.class,
                BigInteger.class, URI.class, URL.class, UUID.class, Pattern.class);
    }

    public void registerImmutable(Class<?>... classes) {
        Collections.addAll(immutableClasses, classes);
    }

    public void registerFastCloner(Class<?> clazz, FastCloner fastCloner) {
        fastCloners.put(clazz, fastCloner);
    }

    public <T> T cloneObject(T object) {
        if (object == null) return null;
        if (!cloningEnabled) return object;
        Map<Object, Object> clones = new ClonesMap();
        synchronized (this) {
            return cloneInternally(object, clones);
        }
    }

    public <T> T cloneInternally(T object, Map<Object, Object> clones) {
        if (object == null) return null;
        if (object == this) return null;

        // Prevent cycles
        if (clones != null) {
            T clone = (T) clones.get(object);
            if (clone != null) {
                return clone;
            }
        }

        Class<?> clazz = object.getClass();
        DeepCloner cloner = findDeepCloner(clazz);
        if (cloner == IGNORE_CLONER) {
            return object;
        } else if (cloner == NULL_CLONER) {
            return null;
        }
        return cloner.deepClone(object, clones);
    }

    private class ClonesMap extends IdentityHashMap<Object, Object> {
        @Override
        public Object get(Object key) {
            if (ignoredInstances != null) {
                Object value = ignoredInstances.get(key);
                if (value != null) return value;
            }
            return super.get(key);
        }
    }

    private final ConcurrentHashMap<Class<?>, Boolean> immutableCache = new ConcurrentHashMap<>();

    protected boolean isImmutable(Class<?> clazz) {
        Boolean isImmutable = immutableCache.get(clazz);
        if (isImmutable != null) return isImmutable;
        if (considerImmutable()) return true;

        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (annotation.annotationType() == getImmutableAnnotation()) {
                immutableCache.put(clazz, Boolean.TRUE);
                return true;
            }
        }

        Class<?> superClass = clazz.getSuperclass();
        while (superClass != null && superClass != Object.class) {
            for (Annotation annotation : superClass.getDeclaredAnnotations()) {
                if (annotation.annotationType() == Immutable.class) {
                    Immutable immutable = (Immutable) annotation;
                    if (immutable.subClass()) {
                        immutableCache.put(clazz, Boolean.TRUE);
                        return true;
                    }
                }
            }
            superClass = superClass.getSuperclass();
        }

        immutableCache.put(clazz, Boolean.FALSE);
        return false;
    }

    protected boolean considerImmutable() {
        return false;
    }

    protected Class<?> getImmutableAnnotation() {
        return Immutable.class;
    }

    private DeepCloner findDeepCloner(Class<?> clazz) {
        if (Enum.class.isAssignableFrom(clazz) || immutableClasses.contains(clazz)) {
            return IGNORE_CLONER;
        } else if (fastCloners.containsKey(clazz)) {
            return new FastClonerDeepCloner(fastCloners.get(clazz),this);
        } else if (clazz.isArray()) {
            return new CloneArrayCloner(clazz);
        } else {
            return new CloneObjectCloner(clazz);
        }
    }

    private static final DeepCloner IGNORE_CLONER = new IgnoreClassCloner();
    private static final DeepCloner NULL_CLONER = new NullClassCloner();

    private static class IgnoreClassCloner implements DeepCloner {
        public <T> T deepClone(T object, Map<Object, Object> clones) {
            throw new CloningException("Don't call this directly");
        }
    }

    private static class NullClassCloner implements DeepCloner {
        public <T> T deepClone(T object, Map<Object, Object> clones) {
            throw new CloningException("Don't call this directly");
        }
    }

    private class CloneArrayCloner implements DeepCloner {
        private final boolean primitive;
        private final boolean immutable;
        private final Class<?> componentType;

        CloneArrayCloner(Class<?> clazz) {
            primitive = clazz.getComponentType().isPrimitive();
            immutable = immutableClasses.contains(clazz.getComponentType());
            componentType = clazz.getComponentType();
        }

        public <T> T deepClone(T object, Map<Object, Object> clones) {
            int length = Array.getLength(object);
            @SuppressWarnings("unchecked") T newInstance = (T) Array.newInstance(componentType, length);
            if (clones != null) {
                clones.put(object, newInstance);
            }
            if (primitive || immutable) {
                System.arraycopy(object, 0, newInstance, 0, length);
            } else {
                if (clones == null) {
                    for (int i = 0; i < length; i++) {
                        Array.set(newInstance, i, Array.get(object, i));
                    }
                } else {
                    for (int i = 0; i < length; i++) {
                        Array.set(newInstance, i, cloneInternally(Array.get(object, i), clones));
                    }
                }
            }
            return newInstance;
        }
    }

    private class CloneObjectCloner implements DeepCloner {
        private final Field[] fields;
        private final boolean[] shouldClone;
        private final int numFields;
        private final ObjectInstantiator<?> instantiator;

        CloneObjectCloner(Class<?> clazz) {
            List<Field> fieldList = new ArrayList<>();
            List<Boolean> shouldCloneList = new ArrayList<>();
            Class<?> superClass = clazz;
            do {
                Field[] declaredFields = superClass.getDeclaredFields();
                for (Field field : declaredFields) {
                    int modifiers = field.getModifiers();
                    boolean isStatic = Modifier.isStatic(modifiers);
                    if (!isStatic && !Modifier.isTransient(modifiers)) {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        fieldList.add(field);
                        boolean shouldClone = !field.isSynthetic() && !isAnonymousParent(field);
                        shouldCloneList.add(shouldClone);
                    }
                }
                superClass = superClass.getSuperclass();
            } while (superClass != Object.class && superClass != null);
            fields = fieldList.toArray(new Field[0]);
            numFields = fields.length;
            shouldClone = new boolean[numFields];
            for (int i = 0; i < shouldCloneList.size(); i++) {
                shouldClone[i] = shouldCloneList.get(i);
            }
            instantiator = instantiationStrategy.getInstantiatorOf(clazz);
        }

        public <T> T deepClone(T object, Map<Object, Object> clones) {
            try {
                @SuppressWarnings("unchecked") T newInstance = (T) instantiator.newInstance();
                if (clones != null) {
                    clones.put(object, newInstance);
                    for (int i = 0; i < numFields; i++) {
                        Field field = fields[i];
                        Object fieldObject = field.get(object);
                        Object fieldObjectClone = shouldClone[i] ? applyCloningStrategy(clones, object, fieldObject, field) : fieldObject;
                        field.set(newInstance, fieldObjectClone);
                    }
                } else {
                    // Shallow clone
                    for (int i = 0; i < numFields; i++) {
                        Field field = fields[i];
                        field.set(newInstance, field.get(object));
                    }
                }
                return newInstance;
            } catch (IllegalAccessException e) {
                throw new CloningException(e);
            }
        }
    }

    private Object applyCloningStrategy(Map<Object, Object> clones, Object object, Object fieldObject, Field field) {
        for (CloningStrategy strategy : cloningStrategies) {
            CloningStrategy.Strategy s = strategy.strategyFor(object, field);
            if (s == CloningStrategy.Strategy.NULL_INSTEAD_OF_CLONE) return null;
            if (s == CloningStrategy.Strategy.SAME_INSTANCE_INSTEAD_OF_CLONE) return fieldObject;
        }
        return cloneInternally(fieldObject, clones);
    }

    private boolean isAnonymousParent(Field field) {
        return "this$0".equals(field.getName());
    }

    public boolean isCloningEnabled() {
        return cloningEnabled;
    }

    public void setCloningEnabled(boolean enableCloning) {
        synchronized (this) {
            this.cloningEnabled = enableCloning;
        }
    }
}