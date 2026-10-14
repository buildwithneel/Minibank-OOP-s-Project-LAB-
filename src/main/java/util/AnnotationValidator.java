package util;
import model.annotation.MaxLength;
import model.annotation.Positive;
import java.lang.reflect.Field;
import java.util.ArrayList;
public final class AnnotationValidator {
    private AnnotationValidator() {}
    public static String[] validate(Object obj) {
        ArrayList<String> errors = new ArrayList<>();
        if (obj == null) return new String[]{"Object cannot be null"};
        Class<?> type = obj.getClass();
        while (type != null) {
            for (Field field : type.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (field.isAnnotationPresent(Positive.class) && value instanceof Number number && number.longValue() <= 0) {
                        errors.add(field.getName() + " " + field.getAnnotation(Positive.class).message());
                    }
                    if (field.isAnnotationPresent(MaxLength.class) && value instanceof String string && string.length() > field.getAnnotation(MaxLength.class).value()) {
                        errors.add(field.getName() + " length must be at most " + field.getAnnotation(MaxLength.class).value());
                    }
                } catch (IllegalAccessException e) {
                    errors.add("Could not validate field " + field.getName());
                }
            }
            type = type.getSuperclass();
        }
        return errors.toArray(new String[0]);
    }
}
