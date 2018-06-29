# j4ts-reflection
A JSweet implementation for https://github.com/ronmamo/reflections reflection lib


example:
```
import org.reflections.Reflections;

public class Test {
    public static void main( String[] args ) throws InvocationTargetException, IllegalAccessException {

        System.out.println("Write all class path under \"java.util\" package");
        Reflections org = new Reflections("java.util");

        for (Class<?> className : org.getSubTypesOf(java.lang.Cloneable.class)) {
            System.out.println(className.getName());
        }

        ArrayList<String> strings = new ArrayList<>(Arrays.asList("1", "2", "3"));

        System.out.println();
        System.out.println("Get all method of ArrayList class: ");
        Method savedMethod = null;
        for (Method method : ReflectionUtils.getAllMethods(ArrayList.class)) {
            System.out.println("ArrayList." + method.getName());

            if (Objects.equals(method.getName(), "size")) {
                Object size = method.invoke(strings);

                System.out.println("Array size is: " + size);

                if (savedMethod != null)
                    break;
            } else if (Objects.equals(method.getName(), "subList")) {
                savedMethod = method;
            }
        }

        System.out.println("subList() parameter count: " + savedMethod.getParameterCount());
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
