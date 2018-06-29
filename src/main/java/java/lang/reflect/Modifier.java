package java.lang.reflect;

public class Modifier {
    public static final int STATIC = 8;

    public static final int INTERFACE = 512;

    public static boolean isStatic(int mod) {
        return (mod & STATIC) != 0;
    }

    public static boolean isInterface(int mod) {
        return (mod & INTERFACE) != 0;
    }
}
