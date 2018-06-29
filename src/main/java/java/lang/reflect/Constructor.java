package java.lang.reflect;

import def.js.Object;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import java.util.Objects;

import static jsweet.util.Lang.$noarrow;
import static jsweet.util.Lang.any;

public class Constructor<T> {
    private final Class<T> clazz;

    public Constructor(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T newInstance(Object... args) {
        Object result = Object.create(ReflectionUtils.getPrototype(clazz));
        $noarrow(any(clazz)).apply(result, args);
        return any(result);
    }

    @Override
    public String toString() {
        return "Constructor{" +
                "clazz=" + clazz.getName() +
                '}';
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Constructor<?> that = (Constructor<?>) o;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return clazz.getName().hashCode();
    }

    public Class<T> getDeclaringClass() {
        return clazz;
    }

    public int getParameterCount() {
        return Reflections.getParameterNames(any(clazz)).length;
    }
}
