package uk.autores.format;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class Reflect {
    private static final ClassLoader CL = Reflect.class.getClassLoader();

    private Reflect() {}

    static Class<?> type(String c, String msg) {
        return load(() -> CL.loadClass(c), msg);
    }

    static Method meth(Class<?> c, String m, Class<?>... params) {
        return load(() -> c.getMethod(m, params), "");
    }

    static Object field(Class<?> c, String f) {
        return load(() -> c.getField(f).get(null), "");
    }

    static Object invoke(Object o, Method m, Object... args) {
        try {
            return m.invoke(o, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw r(e);
        }
    }

    private static RuntimeException r(Exception e) {
        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        }
        return new InvocationException(e);
    }

    private static <R> R load(L<R> loader, String msg) {
        try {
            return loader.load();
        } catch (Exception e) {
            throw new UnsupportedOperationException(msg, e);
        }
    }

    private interface L<R> {
        R load() throws Exception;
    }

    public static class InvocationException extends RuntimeException {
        private InvocationException(Throwable t) {
            super(t);
        }
    }
}
