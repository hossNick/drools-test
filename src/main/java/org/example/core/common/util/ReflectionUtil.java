package org.example.core.common.util;



import org.example.core.model.DynamicFact;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectionUtil {





    public static String makeGetterMethod(String filedName) {
        return "get" + StringUtil.firstLetterToUpperCase(filedName);
    }

    private static String makeSetterMethod(String filedName) {
        return "set" + StringUtil.firstLetterToUpperCase(filedName);
    }

    public static Map<String, Object> getNotNullSimpleFieldMap(DynamicFact entity) {
        Map<String, Object> fieldMap = new HashMap<>();
        if (entity == null)
            return fieldMap;

        for (Method method : entity.getClass().getMethods()) {
            if (!DynamicFact.class.isAssignableFrom(method.getReturnType())
                    && !Collection.class.isAssignableFrom(method.getReturnType())
                    && (isBasicClass(method.getReturnType()) || method.getReturnType().isEnum())
                    && method.getName().startsWith("get")
                    && method.getParameterTypes().length == 0) {

                try {
                    Object value = method.invoke(entity);
                    if (value != null) {
                        String propertyName = method.getName().substring("get".length());
                        propertyName = StringUtil.firstLetterToLowerCase(propertyName);
                        fieldMap.put(propertyName, value);
                    }
                } catch (Throwable e) {
                }
            }
        }

        return fieldMap;
    }

    static Class<?>[] basicClasses = new Class[]{
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class,
            Character.class,
            String.class,
            Boolean.class,
            Date.class,
            UUID.class
    };

    public static boolean isBasicClass(Class<?> clazz) {
        for (Class<?> basicClass : basicClasses)
            if (basicClass.equals(clazz))
                return true;
        return false;
    }


    public static Object invokeGetter(Object obj, String name) {
        Object result;
        Class<?> c = obj.getClass();
        Class<?>[] parameterTypes = new Class[]{};
        Method concatMethod;
        Object[] arguments = new Object[]{};
        try {
            if (name.indexOf(".") > 0) {
                String left = name.substring(0, name.indexOf("."));
                String right = name.substring(name.indexOf(".") + 1);
                Object object = invokeGetter(obj, left);
                if (object == null)
                    return null;
                result = invokeGetter(object, right);
                return result;
            }
            String getMethod = makeGetterMethod(name);
            concatMethod = c.getMethod(getMethod, parameterTypes);
            result = concatMethod.invoke(obj, arguments);
        } catch (Throwable e) {
            return null;
        }

        return result;
    }

    public static void invokeSetter(Object obj, String name, Object value) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        Method method = Arrays.stream(methods).filter(m -> m.getName().equals(makeSetterMethod(name))).findFirst().orElse(null);
        if (method == null)
            throw new NullPointerException("Method with name " + name + " Not Found in " + obj + " of type " + clazz);

        try {
            method.invoke(obj, value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasProperty(Class<?> c, String name) {
        Method method = getGetter(c, name);
        return method != null;
    }

    public static Method getGetter(Class<?> clazz, String fieldName) {
        String capitalizedFieldName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        String getterName = "get" + capitalizedFieldName;
        String booleanGetterName = "is" + capitalizedFieldName;

        try {
            return clazz.getMethod(getterName);
        } catch (NoSuchMethodException e) {
            try {
                return clazz.getMethod(booleanGetterName);
            } catch (NoSuchMethodException ignored) {
            }
        }

        return null;
    }

    public static <T extends DynamicFact> Field getAttributeEnum(T entity, String propertyName) {
        List<Field> fields = getAllFields(entity.getClass());

        for (Field field : fields)
            if (field.isEnumConstant() && field.getName().equals(propertyName))
                return field;

        return null;
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(List.of(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + fieldName + " not found");
    }

    public static List<Method> getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        while (clazz != null) {
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    public static Object invokeMethod(Object object, String methodName, Object... params) throws ReflectiveOperationException {
        Class<?> clazz = object.getClass();
        List<Method> methods = getAllMethods(clazz);
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterCount() == params.length) {
                method.setAccessible(true);
                return method.invoke(object, params);
            }
        }
        throw new NoSuchMethodException("Method " + methodName + " not found in " + clazz);
    }

    public static void setFieldValue(Object object, String fieldName, Object value) throws ReflectiveOperationException {
        Field field = getField(object.getClass(), fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }

    public static Object getFieldValue(Object object, String fieldName) throws ReflectiveOperationException {
        Field field = getField(object.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(object);
    }

    public static <T> T createInstance(Class<T> clazz) throws ReflectiveOperationException {
        Constructor<T> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    public static boolean implementsInterface(Class<?> clazz, Class<?> interfaceType) {
        return Arrays.asList(clazz.getInterfaces()).contains(interfaceType);
    }

    public static List<Annotation> getAnnotations(Class<?> clazz) {
        return Arrays.asList(clazz.getAnnotations());
    }

    public static List<Class<?>> findClassesInPackage(String packageName) {
        throw new UnsupportedOperationException("Classpath scanning requires additional dependencies (e.g., Reflections).");
    }

    public static boolean isFieldAnnotated(Field field, Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }
}
