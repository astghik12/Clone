package instantiator;

public class InstantiationStrategy {

    private static final InstantiationStrategy instance = new InstantiationStrategy();

    public <T> CustomInstantiator<T> getInstantiatorOf(Class<T> c) {
        return new CustomInstantiator<>(c);
    }

    public static InstantiationStrategy getInstance() {
        return instance;
    }
}
