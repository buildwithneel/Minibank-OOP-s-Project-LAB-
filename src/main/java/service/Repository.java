package service;
import model.annotation.Id;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
public class Repository<T> {
    private final HashMap<String, T> items = new HashMap<>();
    public T save(T entity) {
        if (entity == null) throw new IllegalArgumentException("Entity cannot be null");
        String id = findId(entity);
        items.put(id, entity);
        return entity;
    }
    public T findById(String id) {
        return items.get(id);
    }
    public ArrayList<T> findAll() {
        return new ArrayList<>(items.values());
    }
    public boolean delete(String id) {
        return items.remove(id) != null;
    }
    @SafeVarargs
    public final void saveAll(T... values) {
        for (T value : values) save(value);
    }
    private String findId(T entity) {
        for (Field field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value == null) throw new IllegalArgumentException("@Id value cannot be null");
                    return value.toString();
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Cannot read @Id field", e);
                }
            }
        }
        Class<?> parent = entity.getClass().getSuperclass();
        while (parent != null) {
            for (Field field : parent.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(entity);
                        if (value == null) throw new IllegalArgumentException("@Id value cannot be null");
                        return value.toString();
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Cannot read @Id field", e);
                    }
                }
            }
            parent = parent.getSuperclass();
        }
        throw new IllegalArgumentException("No field marked @Id in " + entity.getClass().getName());
    }
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) return 0;
        double total = 0;
        for (N value : values) total += value.doubleValue();
        return total / values.length;
    }
}
