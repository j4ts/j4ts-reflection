package java.lang.reflect;

import def.js.Function;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;

import java.util.Objects;

import static jsweet.util.Lang.$noarrow;
import static jsweet.util.Lang.object;

public final class Method {
    private final Class<?> clazz;
    private final String name;
    private final boolean isStatic;

    public Method(Class<?> declaringClass,
                  String name, boolean isStatic) {
        this.clazz = declaringClass;
        this.name = name;
        this.isStatic = isStatic;
    }

    public Class<?> getDeclaringClass() {
        return clazz;
    }

    public String getName() {
        return name;
    }


    public Object invoke(Object obj, Object... args) {
        return getFunctionObj().apply(obj, args);
    }

    public Function getFunctionObj() {
        if (isStatic) {
            return $noarrow(object(clazz).$get(name));
        } else {
            return $noarrow(object(ReflectionUtils.getPrototype(clazz)).$get(name));
        }
    }

    @Override
    public String toString() {
        return "Method{" +
                "clazz=" + clazz.getName() +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Method method = (Method) o;
        return Objects.equals(getModifiers(), method.getModifiers()) &&
                Objects.equals(getFunctionObj(), method.getFunctionObj());
    }

    @Override
    public int hashCode() {
        return getFunctionObj().hashCode() ^ getModifiers();
    }

    public int getModifiers() {
        return (isStatic ? Modifier.STATIC : 0);
    }

    public int getParameterCount() {
        return Reflections.getParameterNames(getFunctionObj()).length;
    }
}
