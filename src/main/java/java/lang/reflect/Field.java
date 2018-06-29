package java.lang.reflect;

import java.util.Objects;

import static jsweet.util.Lang.object;

public class Field {
    private final Class<?> clazz;
    private final String name;
    private final boolean isStatic;
    private final Object createdInstance;

    public Field(Class<?> declaringClass,
                 String name, boolean isStatic, Object createdInstance) {
        this.clazz = declaringClass;
        this.name = name;
        this.isStatic = isStatic;
        this.createdInstance = createdInstance;
    }

    public Class<?> getDeclaringClass() {
        return clazz;
    }

    public String getName() {
        return name;
    }

    public Object get(Object obj) {
        if (isStatic) {
            return object(clazz).$get(name);
        } else {
            return object(obj).$get(name);
        }
    }

    public void set(Object obj, Object to) {
        if (isStatic) {
            object(clazz).$set(name, to);
        } else {
            object(obj).$set(name, to);
        }
    }

    @Override
    public String toString() {
        return "Field{" +
                "clazz=" + clazz.getName() +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(getModifiers(), field.getModifiers()) && Objects.equals(name, field.name) &&
                (isStatic ? Objects.equals(clazz, field.clazz) : Objects.equals(get(createdInstance), field.get(field.createdInstance)));
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public int getModifiers() {
        return (isStatic ? Modifier.STATIC : 0);
    }
}
