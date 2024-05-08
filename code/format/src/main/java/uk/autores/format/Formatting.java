// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.text.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Static formatting methods.
 */
public final class Formatting {
    private Formatting() {}

    /**
     * Parses {@link MessageFormat} expression into component parts.
     *
     * @param seq format string
     * @return immutable list of component parts
     */
    public static List<FormatSegment> parse(CharSequence seq) {
        List<FormatSegment> list = new ArrayList<>();
        int offset = 0;
        for (int i = 0; i < seq.length(); i++) {
            char ch = seq.charAt(i);
            if (ch == '\'') {
                addRaw(list, seq, offset, i);
                FormatSegment segment = parseEscaped(seq, i);
                list.add(segment);
                rationalize(list);
                i += segment.raw().length();
                offset = i;
            } else if (ch == '{') {
                addRaw(list, seq, offset, i);
                rationalize(list);
                FormatSegment segment = parseVariable(seq, i);
                list.add(segment);
                i += segment.raw().length();
                offset = i;
            }
        }
        if (offset < seq.length()) {
            addRaw(list, seq, offset, seq.length());
            rationalize(list);
        }
        argumentTypesByIndex(list);
        return Lists.immutable(list);
    }

    private static void rationalize(List<FormatSegment> segments) {
        if (segments.size() > 1) {
            int i0 = segments.size() - 1;
            FormatSegment s0 = segments.get(i0);
            int i1 = segments.size() - 2;
            FormatSegment s1 = segments.get(i1);
            if (s0 instanceof FormatLiteral && s1 instanceof FormatLiteral) {
                segments.remove(i0);
                segments.remove(i1);
                FormatLiteral l0 = (FormatLiteral) s0;
                FormatLiteral l1 = (FormatLiteral) s1;
                FormatLiteral combined = new FormatLiteral(
                        l1.raw() + l0.raw(),
                        l1.processed() + l0.processed()
                );
                segments.add(combined);
            }
        }
    }

    private static void addRaw(List<FormatSegment> list, CharSequence seq, int start, int end) {
        if (end - start > 0) {
            String raw = seq.subSequence(start, end).toString();
            list.add(new FormatLiteral(raw, raw));
        }
    }

    private static FormatLiteral parseEscaped(CharSequence seq, int offset) {
        if (isEscapedQuote(seq, offset)) {
            return new FormatLiteral("''", "'");
        }
        StringBuilder buf = new StringBuilder();
        int end = offset;
        for (int i = offset + 1; i < seq.length(); i++, end++) {
            if (isEscapedQuote(seq, i)) {
                buf.append('\'');
                i++;
                end++;
                continue;
            }
            char ch = seq.charAt(i);
            if (ch == '\'') {
                end++;
                break;
            }
            buf.append(ch);
        }
        String raw = seq.subSequence(offset, end + 1).toString();
        return new FormatLiteral(raw, buf.toString());
    }

    private static boolean isEscapedQuote(CharSequence sequence, int offset) {
        if (sequence.length() != offset + 1) {
            char ch0 = sequence.charAt(offset);
            char ch1 = sequence.charAt(offset + 1);
            return ch0 == '\'' && ch1 == '\'';
        }
        return false;
    }

    private static FormatVariable parseVariable(CharSequence sequence, int offset) {
        int index = 0;
        int initial = offset + 1;
        int typeOffset = 0;
        for (int i = initial; i < sequence.length(); i++) {
            char ch = sequence.charAt(i);
            if (Character.isDigit(ch)) {
                int digit = ch - '0';
                index = index * 10 + digit;
            } else if (ch == '}' && i != initial) {
                return newVar(sequence, offset, i + 1, index);
            } else if (ch == ',' && i != initial) {
                typeOffset = i + 1;
                break;
            } else {
                throw new IllegalArgumentException("Unexpected char " + ch + " at " + i);
            }
        }
        FormatType type = detectType(sequence, typeOffset);
        int next = typeOffset + type.label().length();
        if (next >= sequence.length()) {
            throw new IllegalArgumentException("Expected character at index " + next);
        }
        char delim = sequence.charAt(next);
        if (delim == '}') {
            return newVar(sequence, offset, next + 1, index, type);
        }
        if (delim != ',') {
            throw new IllegalArgumentException("Unexpected char " + delim + " at " + next);
        }
        FormatStyle style = detectStyle(type, sequence, next + 1);
        String subformat;
        int end;
        if (style == FormatStyle.SUBFORMAT) {
            subformat = parseSubformat(sequence, next + 1);
            end = next + 1 + subformat.length();
        } else {
            end = next + 2 + style.label().length();
            checkExhausted(sequence, end);
            return newVar(sequence, offset, end, index, type, style);
        }
        if (end == sequence.length() || sequence.charAt(end) != '}') {
            throw new IllegalArgumentException("Expected } at index " + end);
        }
        return newVar(sequence, offset, end + 1, index, type, style, subformat);
    }

    private static void checkExhausted(CharSequence sequence, int end) {
        if (end > sequence.length()) {
            throw new IllegalArgumentException("Unexpected end of sequence");
        }
    }

    private static FormatType detectType(CharSequence sequence, int offset) {
        for (FormatType type : FormatType.values()) {
            if (type == FormatType.NONE) {
                continue;
            }
            if (matches(sequence, offset, type.label())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Expected format type at " + offset);
    }

    private static FormatStyle detectStyle(FormatType type, CharSequence sequence, int offset) {
        for (FormatStyle s : type.styles()) {
            if (s == FormatStyle.NONE || s == FormatStyle.SUBFORMAT) {
                continue;
            }
            if (matches(sequence, offset, s.label())) {
                return s;
            }
        }
        return FormatStyle.SUBFORMAT;
    }

    private static String parseSubformat(CharSequence sequence, int offset) {
        StringBuilder buf = new StringBuilder();
        int nested = 0;
        boolean quoted = false;
        for (int i = offset; i < sequence.length(); i++) {
            char ch = sequence.charAt(i);
            if (ch == '\'') {
                quoted = !quoted;
            } else if (!quoted) {
                if (ch == '{') {
                    nested++;
                } else if (ch == '}') {
                    if (nested == 0) {
                        return buf.toString();
                    } else {
                        nested--;
                    }
                }
            }
            buf.append(ch);
        }
        throw new IllegalArgumentException("Expected } before index " + sequence.length());
    }

    private static boolean matches(CharSequence source, int offset, String expected) {
        if ((source.length() - offset) < expected.length()) {
            return false;
        }
        for (int i = 0; i < expected.length(); i++) {
            if (source.charAt(i + offset) != expected.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static FormatVariable newVar(CharSequence sequence, int offset, int end, int index) {
        return newVar(sequence, offset, end, index, FormatType.NONE, FormatStyle.NONE, "");
    }

    private static FormatVariable newVar(CharSequence sequence, int offset, int end, int index, FormatType type) {
        return newVar(sequence, offset, end, index, type, FormatStyle.NONE, "");
    }

    private static FormatVariable newVar(CharSequence sequence, int offset, int end, int index, FormatType type, FormatStyle style) {
        return newVar(sequence, offset, end, index, type, style, "");
    }

    private static FormatVariable newVar(CharSequence sequence, int offset, int end, int index, FormatType type, FormatStyle style, String subformat) {
        String raw = sequence.subSequence(offset, end).toString();
        FormatVariable v = new FormatVariable(raw, index, type, style, subformat);
        validate(v);
        return v;
    }

    private static void validate(FormatVariable v) {
        if (v.style() != FormatStyle.SUBFORMAT) {
            return;
        }
        String pattern = v.subformat();
        Locale l = Locale.ENGLISH;
        switch (v.type()) {
            case NUMBER:
                new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(l));
                break;
            case DATE:
            case TIME:
                new SimpleDateFormat(pattern, l);
                break;
            case CHOICE:
                new ChoiceFormat(pattern);
                break;
            default:
                break;
        }
    }

    /**
     * The number of arguments referenced by {@link FormatVariable}s in the expression.
     *
     * @param expression expression components
     * @return argument count or zero if there are no variables
     */
    public static int argumentCount(List<? extends FormatSegment> expression) {
        int max = 0;
        for (FormatSegment segment : expression) {
            if (!(segment instanceof FormatVariable)) {
                continue;
            }
            FormatVariable v = (FormatVariable) segment;
            int n = v.index() + 1;
            max = Math.max(max, n);
        }
        return max;
    }

    /**
     * The mapped argument types as dictated by {@link FormatType#argType()}.
     *
     * @param expression expression segments
     * @return immutable list of types
     */
    public static List<Class<?>> argumentTypesByIndex(List<FormatSegment> expression) {
        Class<?>[] results = new Class<?>[argumentCount(expression)];
        for (FormatSegment segment : expression) {
            if (segment instanceof FormatVariable) {
                FormatVariable v = (FormatVariable) segment;
                int index = v.index();
                if (results[index] == null) {
                    results[index] = v.type().argType();
                } else if (results[index] != v.type().argType()) {
                    String msg = results[index] + " does not match " + v.type().argType() + " at index " + index;
                    throw new IllegalArgumentException(msg);
                }
            }
        }
        return Lists.immutable(Arrays.asList(results));
    }

    /**
     * Useful for testing and resultant string size estimation.
     *
     * @param segments expression segments
     * @return sample arguments
     * @see FormatType#argType()
     */
    public static Object[] exampleArguments(List<FormatSegment> segments) {
        Object[] args = new Object[argumentCount(segments)];
        for (FormatSegment segment : segments) {
            if (segment instanceof FormatVariable) {
                FormatVariable v = (FormatVariable) segment;
                int index = v.index();
                switch (v.type()) {
                    case NONE:
                        args[index] = "foo bar baz";
                        break;
                    case NUMBER:
                    case CHOICE:
                        args[index] = 10_000_000;
                        break;
                    case DATE:
                    case TIME:
                        ZoneId utc = ZoneId.of("UTC");
                        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.EPOCH, utc);
                        args[index] = ZonedDateTime.of(ldt, utc);
                        break;
                }
            }
        }
        return args;
    }

    /**
     * Static format implementation.
     * Approximates behaviour of {@link MessageFormat#format(String, Object...)}.
     *
     * @param expression expression segments
     * @param l locale
     * @param args arguments
     * @return formatted string
     */
    public static String format(List<FormatSegment> expression, Locale l, Object...args) {
        StringBuffer buf = new StringBuffer();
        for (FormatSegment segment : expression) {
            if (segment instanceof FormatLiteral) {
                buf.append(((FormatLiteral) segment).processed());
            }
            if (segment instanceof FormatVariable) {
                formatVar(l, buf, (FormatVariable) segment, args);
            }
        }
        return buf.toString();
    }

    private static void formatVar(Locale l, StringBuffer buf, FormatVariable variable, Object...args) {
        Object value = args[variable.index()];
        switch (variable.type()) {
            case NUMBER:
                number(l, variable).format(value, buf, new FieldPosition(0));
                break;
            case DATE:
                formatDate(buf, date(l, variable), value);
                break;
            case TIME:
                formatDate(buf, time(l, variable), value);
                break;
            case CHOICE:
                formatChoice(l, buf, variable, value, args);
                break;
            default:
                buf.append(value);
                break;
        }
    }

    private static NumberFormat number(Locale l, FormatVariable variable) {
        switch (variable.style()) {
            case INTEGER:
                return NumberFormat.getIntegerInstance(l);
            case CURRENCY:
                return NumberFormat.getCurrencyInstance(l);
            case PERCENT:
                return NumberFormat.getPercentInstance(l);
            case SUBFORMAT:
                return new DecimalFormat(variable.subformat(), DecimalFormatSymbols.getInstance(l));
        }
        return NumberFormat.getInstance(l);
    }

    private static DateFormat date(Locale l, FormatVariable variable) {
        switch (variable.style()) {
            case SHORT:
                return DateFormat.getDateInstance(DateFormat.SHORT, l);
            case MEDIUM:
                return DateFormat.getDateInstance(DateFormat.MEDIUM, l);
            case LONG:
                return DateFormat.getDateInstance(DateFormat.LONG, l);
            case FULL:
                return DateFormat.getDateInstance(DateFormat.FULL, l);
            case SUBFORMAT:
                return new SimpleDateFormat(variable.subformat(), l);
        }
        return DateFormat.getDateInstance(DateFormat.DEFAULT, l);
    }

    private static DateFormat time(Locale l, FormatVariable variable) {
        switch (variable.style()) {
            case SHORT:
                return DateFormat.getTimeInstance(DateFormat.SHORT, l);
            case MEDIUM:
                return DateFormat.getTimeInstance(DateFormat.MEDIUM, l);
            case LONG:
                return DateFormat.getTimeInstance(DateFormat.LONG, l);
            case FULL:
                return DateFormat.getTimeInstance(DateFormat.FULL, l);
            case SUBFORMAT:
                return new SimpleDateFormat(variable.subformat(), l);
        }
        return DateFormat.getTimeInstance(DateFormat.DEFAULT, l);
    }

    private static void formatDate(StringBuffer buf, DateFormat df, Object value) {
        if (value instanceof ZonedDateTime) {
            ZonedDateTime zdt = (ZonedDateTime) value;
            ZoneId zoneId = zdt.getZone();
            TimeZone zone = TimeZone.getTimeZone(zoneId);
            df.setTimeZone(zone);
            value = new Date(zdt.toEpochSecond());
        }
        df.format(value, buf, new FieldPosition(0));
    }

    private static void formatChoice(Locale l, StringBuffer buf, FormatVariable variable, Object value, Object...args) {
        ChoiceFormat format = new ChoiceFormat(variable.subformat());
        String choice = format.format(value);
        String result;
        if (choice.indexOf('{') >= 0) {
            List<FormatSegment> recursive = parse(choice);
            result = format(recursive, l, args);
        } else {
            result = choice;
        }
        buf.append(result);
    }

    /**
     * Estimates the length of a formatted expression.
     *
     * @param expression expression segments
     * @return an estimation of required buffer size
     */
    public static int estimateLength(List<FormatSegment> expression) {
        Object[] args = exampleArguments(expression);
        String result = format(expression, Locale.ENGLISH, args);
        return normalize(result.length());
    }

    private static int normalize(int n) {
        int x = 8;
        while (x < n) {
            x *= 2;
        }
        return x * 2;
    }
}
