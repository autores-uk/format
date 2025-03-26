// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.autores.format.testing.TestEquality;
import uk.autores.format.testing.TestStrings;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class FormattingTest {

    @Test
    void parse() {
        {
            List<FormatSegment> segments = Formatting.parse(" ");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals(" ", lit(segments.get(0)).processed(), segments.toString());
            assertEquals(" ", lit(segments.get(0)).raw(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("foo");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("foo", lit(segments.get(0)).processed(), segments.toString());
            assertEquals("foo", lit(segments.get(0)).raw(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("'foo'");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("foo", lit(segments.get(0)).processed(), segments.toString());
            assertEquals("'foo'", lit(segments.get(0)).raw(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("''");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("'", lit(segments.get(0)).processed(), segments.toString());
            assertEquals("''", lit(segments.get(0)).raw(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("'{''}'");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("{'}", lit(segments.get(0)).processed(), segments.toString());
            assertEquals("'{''}'", lit(segments.get(0)).raw(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("'{0}'");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("{0}", lit(segments.get(0)).processed(), segments.toString());
            assertEquals("'{0}'", lit(segments.get(0)).raw(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("{0}");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("{0}", v(segments.get(0)).raw(), segments.toString());
            assertEquals(0, v(segments.get(0)).index(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("{0,number}");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("{0,number}", v(segments.get(0)).raw(), segments.toString());
            assertEquals(0, v(segments.get(0)).index(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("{0,number,currency}");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("{0,number,currency}", v(segments.get(0)).raw(), segments.toString());
            assertEquals(0, v(segments.get(0)).index(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("{0,number,'$'#}");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("{0,number,'$'#}", v(segments.get(0)).raw(), segments.toString());
            assertEquals(0, v(segments.get(0)).index(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("{0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}");
            assertEquals(1, segments.size(), segments.toString());
            assertEquals("{0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}", v(segments.get(0)).raw(), segments.toString());
            assertEquals(0, v(segments.get(0)).index(), segments.toString());
        }
        {
            List<FormatSegment> segments = Formatting.parse("There are {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.");
            assertEquals(3, segments.size(), segments.toString());
            assertEquals("There are ", lit(segments.get(0)).raw(), segments.toString());
            assertEquals("{0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}", v(segments.get(1)).raw(), segments.toString());
            assertEquals(0, v(segments.get(1)).index(), segments.toString());
            assertEquals(".", lit(segments.get(2)).raw(), segments.toString());
        }
    }

    private FormatLiteral lit(FormatSegment segment) {
        return (FormatLiteral) segment;
    }

    private FormatVariable v(FormatSegment segment) {
        return (FormatVariable) segment;
    }

    @Test
    void arguments() {
        for (String t : TestStrings.valid()) {
            MessageFormat mf = new MessageFormat(t);
            List<FormatSegment> segments = Formatting.parse(t);

            int expected = mf.getFormatsByArgumentIndex().length;
            int actual = Formatting.argumentCount(segments);

            List<Class<?>> argTypes = Formatting.argumentTypesByIndex(segments);

            assertEquals(expected, actual, t);
            assertEquals(expected, argTypes.size(), t);

            for (int i = 0; i < argTypes.size(); i++) {
                assertNotNull(argTypes.get(i), t);
            }
        }
    }

    @Test
    void raw() {
        for (String expected : TestStrings.valid()) {
            List<FormatSegment> segments = Formatting.parse(expected);
            StringBuilder buf = new StringBuilder(expected.length());
            for (FormatSegment fs : segments) {
                buf.append(fs.raw());
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
                .map(Formatting::parse).collect(Collectors.toList());
        candidates.addAll(parsed);

        TestEquality.test(candidates.toArray());
    }


    @Test
    void format() {
        Locale l = Locale.ENGLISH;

        for (String t : TestStrings.valid()) {
            MessageFormat mf = new MessageFormat(t, l);
            List<FormatSegment> expression = Formatting.parse(t);
            Object[] args = Formatting.exampleArguments(expression);

            String expected = mf.format(args);
            String actual = Formatting.format(expression, l, args);

            assertEquals(expected, actual, t);
        }
    }

    @Test
    void estimate() {
        for (String t : TestStrings.valid()) {
            List<FormatSegment> segments = Formatting.parse(t);

            int estimate = Formatting.estimateLength(segments);
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
            List<FormatSegment> expression = Formatting.parse(expr);
            String actual = Formatting.format(expression, english, 4, epoch, "an eclipse");
            String expected = "At 12:00:00\u202fAM on Jan 1, 1970, there was an eclipse on planet 4.";
            assertEquals(expected, actual);
        }
        {
            String expr = "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.";
            List<FormatSegment> expression = Formatting.parse(expr);
            {
                String actual = Formatting.format(expression, english, 0);
                String expected = "There are no files.";
                assertEquals(expected, actual);
            }
            {
                String actual = Formatting.format(expression, english, 1);
                String expected = "There is one file.";
                assertEquals(expected, actual);
            }
            {
                String actual = Formatting.format(expression, english, 1000);
                String expected = "There are 1,000 files.";
                assertEquals(expected, actual);
            }
        }
    }

    @Test
    void illegal() {
        for (String t : TestStrings.invalid()) {
            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
                new MessageFormat(t);
            }, t);
            Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> {
                Formatting.parse(t);
            }, t);
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
            Executable e = () -> Formatting.parse(t);
            assertThrowsExactly(IllegalArgumentException.class, e);
        }
    }

    @Test
    void conjoined() {
        List<FormatSegment> expression = Formatting.parse("{0}{1}{2}");
        for (FormatSegment fs : expression) {
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
            List<FormatSegment> expression = Formatting.parse(test);

            boolean actual = Formatting.needsLocale(expression);

            assertEquals(expected, actual);
        }
    }
}
