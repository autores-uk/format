package uk.autores.format;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ReflectionTest {

    public static final String OK = "OK";

    public static String ok() {
        return OK;
    }

    public static void throwUp(Exception e) throws Exception {
        throw e;
    }

    @Test
    void type() {
        {
            Class<?> me = Reflection.type(ReflectionTest.class.getName(), "");
            assertSame(ReflectionTest.class, me);
        }
        {
            String msg = "error abc";
            try {
                Reflection.type("DoesNotExist", msg);
                fail();
            } catch (Exception e) {
                assertTrue(e instanceof RuntimeException);
                assertEquals(msg, e.getMessage());
            }
        }
    }

    @Test
    void field() {
        Class<?> me = Reflection.type(ReflectionTest.class.getName(), "");
        Object actual = Reflection.field(me, "OK");
        assertEquals(OK, actual);
    }

    @Test
    void invoke() {
        Class<?> me = Reflection.type(ReflectionTest.class.getName(), "");
        {
            Method m = Reflection.meth(me, "ok");
            Object actual = Reflection.invoke(null, m);
            assertEquals(OK, actual);
        }
        {
            Method m = Reflection.meth(me, "throwUp", Exception.class);
            try {
                Reflection.invoke(null, m, new TestException());
                fail();
            } catch (Exception e) {
                assertTrue(e.getCause() instanceof InvocationTargetException);
            }
        }
        {
            Method m = Reflection.meth(me, "throwUp", Exception.class);
            try {
                Reflection.invoke(null, m, new TestRuntimeException());
                fail();
            } catch (Exception e) {
                assertTrue(e instanceof TestRuntimeException);
            }
        }
    }

    public static class TestException extends Exception {
    }

    public static class TestRuntimeException extends RuntimeException {
    }
}