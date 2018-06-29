package org.reflections;

import def.js.Array;
import def.js.Function;
import def.js.Object;
import def.js.RegExp;
import jsweet.util.Lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

import static def.js.Globals.eval;
import static jsweet.util.Lang.*;

public class Reflections {
    String prefix;
    Map<String, Set<Class<?>>> interfaces = new HashMap<>();
    Map<Class<?>, Set<Class<?>>> classes = new HashMap<>();


    public Reflections() {
        prefix = "";

        scan();
    }

    public Reflections(String from) {
        prefix = from;
        scan();
    }

    private void scan() {
        Stack<Object> stack = new Stack<>();
        if (prefix.isEmpty()) {
            stack.push(eval("java"));
            stack.push(eval("org"));
            stack.push(eval("com"));
            stack.push(eval("sun"));
            stack.push(eval("javax"));
            stack.push(eval("jdk"));
            stack.push(eval("javafx"));
        } else {
            stack.push(eval(prefix));
        }

        while (!stack.empty()) {
            Object pop = stack.pop();
            if (pop == null || !Objects.equals(typeof(pop), "object")) {
                continue;
            }

            for (Class<?> aClass : ReflectionUtils.getAllInnerClasses(any(pop))) {
                if (!classes.containsKey(aClass)) {
                    classes.put(aClass, new HashSet<>());
                }

                Set<Class<?>> allInnerClasses = ReflectionUtils.getAllInnerClasses(aClass);
                allInnerClasses.add(aClass);

                for (Class<?> allInnerClass : allInnerClasses) {
                    for (Class<?> aClass1 : ReflectionUtils.getAllExtendedType(allInnerClass)) {
                        if (!classes.containsKey(aClass1)) {
                            classes.put(aClass1, new HashSet<>());
                        }
                        classes.get(aClass1).add(allInnerClass);
                    }
                    for (String s : ReflectionUtils.getAllInterfaces(allInnerClass)) {
                        if (!interfaces.containsKey(s)) {
                            interfaces.put(s, new HashSet<>());
                        }
                        interfaces.get(s).add(allInnerClass);
                    }
                }
            }

            Object.getOwnPropertyNames(pop)
                    .map(str -> pop.$get(str))
                    .filter(obj -> Objects.equals(typeof(obj), "object"))
                    .forEach(obj -> stack.push(object(obj)));
        }
    }


    public static Array<String> getParameterNames(Function functionObj) {
        return array(functionObj.toJSString()
                .match(new RegExp("function\\s[^(]*?\\(([^)]*)\\)")).$get(1)
                .split(",")).map(s -> s.trim());
    }

    public List<String> getMethodParamNames(Method method) {
        return Arrays.asList(array(getParameterNames(method.getFunctionObj())));
    }

    public List<String> getConstructorParamNames(Constructor constructor) {
        return Arrays.asList(array(getParameterNames(any(constructor.getDeclaringClass()))));

    }

    public Set<String> getAllTypes() {
        HashSet<String> strings = new HashSet<>(interfaces.keySet());
        classes.keySet().forEach(clazz -> strings.add(clazz.getName()));
        return strings;
    }

    public <T> Set<Class<? extends T>> getSubTypesOf(Class<T> type) {
        if (interfaces.containsKey(Lang.<String>any(type)))
            return any(interfaces.get(Lang.<String>any(type)));
        if (classes.containsKey(type))
            return any(classes.get(type));
        return Collections.emptySet();
    }
}
