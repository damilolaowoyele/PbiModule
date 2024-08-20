package org.fhi360.PbiModule.Service.Util;

import java.lang.reflect.Field;

public class FieldValueConverter {

    public static Object convertValueToFieldType(Field field, Object value) {
        Class<?> targetType = field.getType();

        if (value == null) {
            return null;
        }

        if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return Integer.valueOf(value.toString());
        } else if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return Long.valueOf(value.toString());
        } else if (targetType.equals(Double.class) || targetType.equals(double.class)) {
            return Double.valueOf(value.toString());
        } else if (targetType.equals(Float.class) || targetType.equals(float.class)) {
            return Float.valueOf(value.toString());
        } else if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            return Boolean.valueOf(value.toString());
        } else if (targetType.equals(String.class)) {
            return value.toString();
        }

        throw new IllegalArgumentException("Unsupported type conversion for field type: " + targetType.getName());
    }
}
