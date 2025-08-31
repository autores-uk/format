// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.autores.format.testing.TestEquality;
import uk.autores.format.testing.TestStrings;

import java.text.FieldPosition;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FormatExpressionTest {

    @Test
    void parse() {
        {
            FormatExpression expr = FormatExpression.parse(" ");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals(" ", lit(expr.get(0)).processed(), expr.toString());
            assertEquals(" ", lit(expr.get(0)).toString(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("foo");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("foo", lit(expr.get(0)).processed(), expr.toString());
            assertEquals("foo", lit(expr.get(0)).toString(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("'foo'");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("foo", lit(expr.get(0)).processed(), expr.toString());
            assertEquals("'foo'", lit(expr.get(0)).toString(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("''");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("'", lit(expr.get(0)).processed(), expr.toString());
            assertEquals("''", lit(expr.get(0)).toString(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("'{''}'");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("{'}", lit(expr.get(0)).processed(), expr.toString());
            assertEquals("'{''}'", lit(expr.get(0)).toString(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("'{0}'");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("{0}", lit(expr.get(0)).processed(), expr.toString());
            assertEquals("'{0}'", lit(expr.get(0)).toString(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("{0}");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("{0}", v(expr.get(0)).toString(), expr.toString());
            assertEquals(0, v(expr.get(0)).index(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("{0,number}");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("{0,number}", v(expr.get(0)).toString(), expr.toString());
            assertEquals(0, v(expr.get(0)).index(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("{0,number,currency}");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("{0,number,currency}", v(expr.get(0)).toString(), expr.toString());
            assertEquals(0, v(expr.get(0)).index(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("{0,number,'$'#}");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("{0,number,'$'#}", v(expr.get(0)).toString(), expr.toString());
            assertEquals(0, v(expr.get(0)).index(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("{0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}");
            assertEquals(1, expr.size(), expr.toString());
            assertEquals("{0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}", v(expr.get(0)).toString(), expr.toString());
            assertEquals(0, v(expr.get(0)).index(), expr.toString());
        }
        {
            FormatExpression expr = FormatExpression.parse("There are {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.");
            assertEquals(3, expr.size(), expr.toString());
            assertEquals("There are ", lit(expr.get(0)).toString(), expr.toString());
            assertEquals("{0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}", v(expr.get(1)).toString(), expr.toString());
            assertEquals(0, v(expr.get(1)).index(), expr.toString());
            assertEquals(".", lit(expr.get(2)).toString(), expr.toString());
        }
    }

    private FormatLiteral lit(Formatter segment) {
        return (FormatLiteral) segment;
    }

    private FormatVariable v(Formatter segment) {
        return (FormatVariable) segment;
    }

    @Test
    void arguments() {
        for (String t : TestStrings.valid()) {
            MessageFormat mf = new MessageFormat(t);
            FormatExpression expr = FormatExpression.parse(t);

            int expected = mf.getFormatsByArgumentIndex().length;
            int actual = expr.argCount();

            Class<?>[] argTypes = expr.argTypes();

            assertEquals(expected, actual, t);
            assertEquals(expected, argTypes.length, t);

            for (Class<?> argType : argTypes) {
                assertNotNull(argType, t);
            }
        }
    }

    @Test
    void objects() {
        class X {
            @Override
            public String toString() {
                return "X";
            }
        }

        String expr = "{0}";
        String expected = MessageFormat.format(expr, new X());
        String actual = FormatExpression.parse(expr).format(Locale.getDefault(), new X());
        assertEquals(expected, actual);
    }

    @Test
    void raw() {
        for (String expected : TestStrings.valid()) {
            FormatExpression expr = FormatExpression.parse(expected);
            StringBuilder buf = new StringBuilder(expected.length());
            for (Formatter fs : expr) {
                buf.append(fs.toString());
            }
            String actual = buf.toString();
            assertEquals(expected, actual);
        }
    }

    @Test
    void equality() {
        List<Object> candidates = new ArrayList<>();
        candidates.add(new Object());
        candidates.add("foobar");
        candidates.add(Collections.singleton("foo"));
        candidates.add(Arrays.asList("foo", "bar"));
        List<Object> parsed = Stream.of(TestStrings.valid())
                .map(FormatExpression::parse).collect(Collectors.toList());
        candidates.addAll(parsed);

        TestEquality.test(candidates.toArray());
    }

    @Test
    void format() {
        Locale l = Locale.ENGLISH;

        for (String t : TestStrings.valid()) {
            MessageFormat mf = new MessageFormat(t, l);
            FormatExpression expression = FormatExpression.parse(t);
            Object[] args = expression.argExamples();

            String expected = mf.format(args);
            String actual = expression.format(l, args);

            assertEquals(expected, actual, t);
        }
    }

    @Test
    void estimate() {
        for (String t : TestStrings.valid()) {
            FormatExpression expr = FormatExpression.parse(t);

            int estimate = expr.estimateLen(Locale.US);
            assertTrue(estimate >= 0, t);
        }
    }

    @Test
    void complex() {
        ZoneId utc = ZoneId.of("UTC");
        LocalDateTime ltd = LocalDateTime.ofInstant(Instant.EPOCH, utc);
        ZonedDateTime epoch = ZonedDateTime.of(ltd, utc);
        Locale english = Locale.ENGLISH;
        {
            String expr = "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.";
            FormatExpression expression = FormatExpression.parse(expr);
            String actual = expression.format( english, 4, epoch, "an eclipse");
            String expected = "At 12:00:00\u202fAM on Jan 1, 1970, there was an eclipse on planet 4.";
            assertEquals(expected, actual);
        }
        {
            String expr = "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.";
            FormatExpression expression = FormatExpression.parse(expr);
            {
                String actual = expression.format( english, 0);
                String expected = "There are no files.";
                assertEquals(expected, actual);
            }
            {
                String actual = expression.format( english, 1);
                String expected = "There is one file.";
                assertEquals(expected, actual);
            }
            {
                String actual = expression.format( english, 1000);
                String expected = "There are 1,000 files.";
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void illegal() {
        for (String t : TestStrings.invalid()) {
            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> new MessageFormat(t), t);
            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> FormatExpression.parse(t), t);
        }
    }

    @Test
    void noMixedTypes() {
        String[] mixed = {
                "{0} {0,number}",
                "{0} {0,date}",
                "{0,number} {0,time}",
                "{0,choice} {0,date}",
        };

        for(String t : mixed) {
            Executable e = () -> FormatExpression.parse(t);
            assertThrowsExactly(IllegalArgumentException.class, e);
        }
    }

    @Test
    void conjoined() {
        FormatExpression expression = FormatExpression.parse("{0}{1}{2}");
        for (Formatter fs : expression) {
            assertInstanceOf(FormatVariable.class, fs);
        }
    }

    @Test
    void needsLocale() {
        Map<String, Boolean> tests = new LinkedHashMap<>();
        tests.put("", false);
        tests.put("foo 'bar'", false);
        tests.put("{0}{0}{0}{0}", false);
        tests.put("{0}{0}{0}{1,number}", true);
        tests.put("{0}{0}{0}{1,date}{1,time}", true);

        for (Map.Entry<String, Boolean> t : tests.entrySet()) {
            boolean expected = t.getValue();
            String test = t.getKey();
            FormatExpression expression = FormatExpression.parse(test);

            boolean actual = expression.needsLocale();

            assertEquals(expected, actual);
        }
    }

    @Test
    void compatible() {
        {
            var left = FormatExpression.parse("{0}");
            var right = FormatExpression.parse("foo {0} bar");
            assertTrue(left.compatible(left));
            assertTrue(left.compatible(right));
            assertTrue(right.compatible(right));
        }
        {
            var left = FormatExpression.parse("{0,number}");
            var right = FormatExpression.parse("foo {0} bar");
            assertFalse(left.compatible(right));
        }
        {
            var left = FormatExpression.parse("{0}");
            var right = FormatExpression.parse("{0} {1}");
            assertFalse(left.compatible(right));
        }
        {
            var left = FormatExpression.parse("{0,date}");
            var right = FormatExpression.parse("foo {0,dtf_date} bar {0,dtf_time}");
            assertTrue(left.compatible(right));
        }
        {
            var left = FormatExpression.parse("{0,dtf_date} {2,number} {3}");
            var right = FormatExpression.parse("{3} {2,number,currency} {0,dtf_date}");
            assertTrue(left.compatible(right));
        }
        {
            var left = FormatExpression.parse("{1}");
            var right = FormatExpression.parse("{0}{1}");
            assertFalse(left.compatible(right));
        }
    }

    @Test
    void rawNumbers() {
        assertSame(Integer.MAX_VALUE);
    }

    @Test
    void rawDates() {
        assertSame(new Date(0));
    }

    private void assertSame(Object... args) {
        var buf = new StringBuffer();
        new MessageFormat("{0}", Locale.ENGLISH).format(args, buf, new FieldPosition(0));
        var expected = buf.toString();
        buf = new StringBuffer();
        FormatExpression.parse("{0}").formatTo(Locale.ENGLISH, buf, args);
        var actual = buf.toString();
        assertEquals(expected, actual);
    }
}
