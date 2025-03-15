package sleppynavigators.studyupbackend.infrastructure.common.util;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ReflectionField<T> {

    private static final Map<Class<?>, Field> cache = new ConcurrentHashMap<>();

    private final Class<T> fieldClazz;
    private final Object instance;

    public ReflectionField(Object instance, Class<T> fieldClazz) {
        this.instance = instance;
        this.fieldClazz = fieldClazz;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        try {
            return (T) getField().get(instance);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void set(T value) {
        try {
            getField().set(instance, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Field getField() {
        return cache.computeIfAbsent(instance.getClass(), clazz -> {
            Field target = Stream.of(instance.getClass().getDeclaredFields())
                    .filter(field -> field.getType().equals(fieldClazz))
                    .findFirst()
                    .orElseThrow(IllegalArgumentException::new);
            target.setAccessible(true);
            return target;
        });
    }
}
