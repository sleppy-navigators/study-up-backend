package sleppynavigators.studyupbackend.infrastructure.common.util;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class ReflectionField<T> {

    private final Field field;
    private final Object instance;

    public ReflectionField(Object instance, Class<T> fieldClazz) {
        this.instance = instance;
        this.field = Stream.of(instance.getClass().getDeclaredFields())
                .filter(field -> field.getType().equals(fieldClazz))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        field.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void set(T value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }
}
