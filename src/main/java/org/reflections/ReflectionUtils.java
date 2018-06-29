package org.reflections;

import def.js.Object;
import def.js.RegExp;
import def.js.String;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static def.dom.Globals.console;
import static def.js.Globals.eval;
import static jsweet.util.Lang.*;

public class ReflectionUtils {
    private ReflectionUtils() {

    }

    public static Class<?> forName(String typeName) {
        Object object = object(eval(typeName));
        return object == null ? null : any(object.constructor);
    }

    public static Set<Constructor<?>> getConstructors(Class<?> type) {
        return Collections.singleton(new Constructor<>(type));
    }

    public static Set<Constructor<?>> getAllConstructors(Class<?> type) {
        Set<Constructor<?>> result = getConstructors(type);

        Class<?> superType = type;
        while ((superType = getExtendedType(superType)) != null) {
            result.addAll(getConstructors(superType));
        }

        return result;
    }

    public static Set<Field> getFields(Class<?> type) {
        Set<Field> result = getStaticFields(type);
        result.addAll(getNonStaticFields(type));

        Class<?> superType = type;
        while ((superType = getExtendedType(superType)) != null) {
            result.removeAll(getNonStaticFields(superType));
        }

        return result;
    }

    private static Set<Field> getStaticFields(Class<?> type) {
        return toSet(array(Object.getOwnPropertyNames(type)
                .filter(key -> !Objects.equals(typeof(object(type).$get(key)), "function"))
                .map(str -> new Field(type, string(str), true, null))));
    }

    private static Set<Field> getNonStaticFields(Class<?> type) {
        try {
            Object createdInstance = $new(type);
            return toSet(array(Object.getOwnPropertyNames(createdInstance)
                    .filter(key -> !Objects.equals(typeof(createdInstance.$get(key)), "function"))
                    .map(str -> new Field(type, string(str), false, createdInstance))));
        } catch (Throwable throwable) {
            throw new ReflectionsException("can not create class instance: " + type.getName() + ", because no default constructor", throwable);
        }
    }


    public static Set<Field> getAllFields(Class<?> type) {
        Set<Field> result = getStaticFields(type);
        result.addAll(getNonStaticFields(type));

        Class<?> superType = getExtendedType(type);
        if (superType != null) {
            result.addAll(getAllFields(superType));
        }
        return result;
    }

    public static Set<Method> getMethods(Class<?> type) {
        Set<Method> result = getStaticMethods(type);
        result.addAll(getNonStaticMethods(type));

        Class<?> superType = type;
        while ((superType = getExtendedType(superType)) != null) {
            result.removeAll(getNonStaticMethods(superType));
        }

        return result;
    }

    static Set<Method> getStaticMethods(Class<?> type) {
        return toSet(array(Object.getOwnPropertyNames(type)
                .filter(str -> Objects.equals(typeof(object(type).$get(str)), "function") && !isAClass(object(type).$get(str)))
                .map(str -> new Method(type, string(str), true))));
    }

    private static Set<Method> getNonStaticMethods(Class<?> type) {
        Object prototype = getPrototype(type);
        return toSet(array(Object.getOwnPropertyNames(prototype)
                .filter(str -> Objects.equals(typeof(object(prototype).$get(str)), "function") && !isAClass(object(prototype).$get(str)))
                .map(str -> new Method(type, string(str), false))));
    }

    public static Set<Method> getAllMethods(Class<?> type) {
        Set<Method> result = getStaticMethods(type);
        result.addAll(getNonStaticMethods(type));

        Class<?> superType = getExtendedType(type);
        if (superType != null) {
            result.addAll(getAllMethods(superType));
        }

        return result;
    }

    public static Class<?> getExtendedType(Class<?> type) {
        Object parent = Object.getPrototypeOf(getPrototype(type));

        return parent == null ? null : any(parent.constructor);
    }

    public static Set<Class<?>> getAllExtendedType(Class<?> type) {
        Class<?> superClass = getExtendedType(type);

        if (superClass == null)
            return new HashSet<>();

        Set<Class<?>> allSuperTypes = getAllExtendedType(superClass);
        allSuperTypes.add(superClass);

        return allSuperTypes;
    }

    public static Set<Class<?>> getAllSuperTypes(Class<?> type) {
        Class<?> superClass = getExtendedType(type);

        if (superClass == null)
            return any(getInterfaces(type));

        Set<Class<?>> allSuperTypes = any(getInterfaces(type));
        allSuperTypes.add(superClass);
        allSuperTypes.addAll(getAllSuperTypes(superClass));

        return allSuperTypes;
    }


    public static Set<Class<?>> getInnerClasses(Class<?> type) {
        Class<?>[] functions = array(Object.getOwnPropertyNames(type)
                .map(str -> object(type).$get(str))
                .filter(clazz -> Objects.equals(typeof(clazz), "function") && isAClass(object(clazz)))
                .map(clazz -> (Class<?>) clazz)
                .filter(clazz -> allowedToMapKey(clazz)));

        for (Class fun : functions) {
            if (object(fun).$get("__class") == null && object(type).$get("__class") != null) {
                console.debug("add __class tag to " + fun.getName());
                object(fun).$set("__class", string(type.getName()).replace(new RegExp("\\.[^.]*$"), "." + fun.getName()));
                console.debug("so now: " + fun.getName());
            }
        }

        return toSet(functions);
    }


    public static Set<Class<?>> getAllInnerClasses(Class<?> type) {
        Set<Class<?>> result = new HashSet<>();
        Queue<Class<?>> queue = new LinkedList<>();
        queue.add(type);

        while (queue.size() > 0) {
            Class<?> pop = queue.poll();

            for (Class<?> aClass : getInnerClasses(pop)) {
                if (!result.contains(aClass) && !Objects.equals(aClass, type)) {
                    queue.add(aClass);
                    result.add(aClass);
                }
            }
        }

        return result;
    }

    public static Set<java.lang.String> getInterfaces(Class<?> type) {
        java.lang.String[] interfaces = any(object(type).$get("__interfaces"));
        return interfaces == null ? new HashSet<>() : toSet(interfaces);
    }

    public static Set<java.lang.String> getAllInterfaces(Class<?> type) {
        Set<java.lang.String> result = getInterfaces(type);

        Class<?> superType = type;
        while ((superType = getExtendedType(superType)) != null) {
            result.addAll(getInterfaces(superType));
        }

        return result;
    }

    public static <T, U> U getPrototype(Class<T> clazz) {
        return eval("clazz.prototype");
    }

    private static <T> Set<T> toSet(T[] arr) {
        Set<T> result = new HashSet<>();
        for (T elem : arr)
            result.add(elem);
        return result;
    }


    private static boolean allowedToMapKey(Class<?> clazz) {
        for (Method m : ReflectionUtils.getStaticMethods(clazz)) {
            if (Objects.equals(m.getName(), "hashCode")) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAClass(Object constructor) {
        return constructor.$get("__class") != null || constructor.$get("__interfaces") != null;
    }
}
