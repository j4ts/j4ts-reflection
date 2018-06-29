# j4ts-reflection
A JSweet implementation for https://github.com/ronmamo/reflections reflection lib


example:
```
import org.reflections.Reflections;

public class Test {
    public static void main(String[] args) {
        System.out.println("Write all class whose implements Cloneable interface under \"java.util\" package");
        Reflections org = new Reflections("java.util");

        for (Class<?> className : org.getSubTypesOf(java.lang.Cloneable.class)) {
            System.out.println(className.getName());
        }
    }
}
```

add maven dependency to your repo, and you can use it java such as javascript side
```
<dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>0.9.11</version>
</dependency>
```
