// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.text.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * Static formatting methods.
 */
public final class Formatting {
    private Formatting() {}

    /**
     * Parses {@link MessageFormat} expression into component parts.
     * The expression
     * <code>"At {1,time} on {1,date}, there was {2} on planet {0,number,integer}."</code>
     * will be parsed to:
     * <ul>
     *     <li>{@link FormatLiteral} <code>"At "</code></li>
     *     <li>{@link FormatVariable} <code>"{1,time}"</code> argument index 1 {@link FmtType#TIME}</li>
     *     <li>{@link FormatLiteral} <code>" on "</code></li>
     *     <li>{@link FormatVariable} <code>"{1,date}"</code>  argument index 1 {@link FmtType#TIME}</li>
     *     <li>{@link FormatLiteral} <code>", there was "</code></li>
     *     <li>{@link FormatVariable} <code>"{2}"</code>  argument index 2 {@link FmtType#NONE}</li>
     *     <li>{@link FormatLiteral} <code>" on planet "</code></li>
     *     <li>{@link FormatVariable} <code>"{0,number,integer}"</code>  argument index 0 {@link FmtType#NUMBER} {@link FmtStyle#INTEGER}</li>
     *     <li>{@link FormatLiteral} <code>"."</code></li>
     * </ul>
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
                FormatLiteral segment = parseEscaped(seq, i);
                list.add(segment);
                rationalize(list);
                i += segment.raw().length();
                offset = i;
            } else if (ch == '{') {
                addRaw(list, seq, offset, i);
                rationalize(list);
                FormatVariable segment = parseVariable(seq, i);
                list.add(segment);
                i += segment.raw().length();
                offset = i;
                i--;
            }
        }
        if (offset < seq.length()) {
            addRaw(list, seq, offset, seq.length());
            rationalize(list);
        }
        argumentTypesByIndex(list);
        return Immutable.list(list);
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
        FmtType type = detectType(sequence, typeOffset);
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
        FmtStyle style = detectStyle(type, sequence, next + 1);
        String subformat;
        int end;
        if (style == FmtStyle.SUBFORMAT) {
            subformat = parseSubformat(sequence, next + 1);
            end = next + 1 + subformat.length();
        } else {
            end = next + 2 + style.label().length();
            checkExhausted(sequence, end);
            return newVar(sequence, offset, end, index, type, style);
        }
//        if (end == sequence.length() || sequence.charAt(end) != '}') {
//            throw new IllegalArgumentException("Expected } at index " + end);
//        }
        return newVar(sequence, offset, end + 1, index, type, style, subformat);
    }

    private static void checkExhausted(CharSequence sequence, int end) {
        if (end > sequence.length()) {
            throw new IllegalArgumentException("Unexpected end of sequence");
        }
    }

    private static final FmtType[] NAMED_FMT_TYPES;
    static {
        List<FmtType> list = new ArrayList<>(asList(FmtType.values()));
        list.remove(FmtType.NONE);
        NAMED_FMT_TYPES = list.toArray(new FmtType[0]);
        Arrays.sort(NAMED_FMT_TYPES, Formatting::longestFirst);
    }

    private static int longestFirst(FmtType a, FmtType b) {
        return b.label().length() - a.label().length();
    }

    private static FmtType detectType(CharSequence sequence, int offset) {
        for (FmtType type : NAMED_FMT_TYPES) {
            if (matches(sequence, offset, type.label())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Expected format type at " + offset);
    }

    private static FmtStyle detectStyle(FmtType type, CharSequence sequence, int offset) {
        for (FmtStyle s : type.styles()) {
            if (s == FmtStyle.NONE || s == FmtStyle.SUBFORMAT) {
                continue;
            }
            if (matches(sequence, offset, s.label())) {
                return s;
            }
        }
        return FmtStyle.SUBFORMAT;
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
        return newVar(sequence, offset, end, index, FmtType.NONE, FmtStyle.NONE, "");
    }

    private static FormatVariable newVar(CharSequence sequence, int offset, int end, int index, FmtType type) {
        return newVar(sequence, offset, end, index, type, FmtStyle.NONE, "");
    }

    private static FormatVariable newVar(CharSequence sequence, int offset, int end, int index, FmtType type, FmtStyle style) {
        return newVar(sequence, offset, end, index, type, style, "");
    }

    private static FormatVariable newVar(CharSequence sequence, int offset, int end, int index, FmtType type, FmtStyle style, String subformat) {
        String raw = sequence.subSequence(offset, end).toString();
        FormatVariable v = new FormatVariable(raw, index, type, style, subformat);
        validate(v);
        return v;
    }

    private static void validate(FormatVariable v) {
        if (v.style() != FmtStyle.SUBFORMAT) {
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
            case DTF_DATE:
            case DTF_TIME:
            case DTF_DATETIME:
                DateTimeFormatter.ofPattern(pattern, l);
                break;
            default:
                break;
        }
    }

    /**
     * <p>
     *     The number of arguments referenced by {@link FormatVariable}s in the expression.
     * </p>
     * <p>
     *      The expression <code>"{3}"</code> returns a count of <code>4</code> because an
     *      argument array of four elements is required to resolve <code>arg[3]</code>
     *      despite arguments zero through three not being present.
     * </p>
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
     * The mapped argument types as dictated by {@link FmtType#argType()}.
     * Any missing argument indices are mapped to {@link Void}.
     *
     * @param expression expression segments
     * @return immutable list of types
     */
    public static List<Class<?>> argumentTypesByIndex(List<FormatSegment> expression) {
        Class<?>[] results = new Class<?>[argumentCount(expression)];
        Arrays.fill(results, Void.class);
        for (FormatSegment segment : expression) {
            if (segment instanceof FormatVariable) {
                FormatVariable v = (FormatVariable) segment;
                int index = v.index();
                if (results[index] == Void.class) {
                    results[index] = v.type().argType();
                } else if (results[index] != v.type().argType()) {
                    String msg = results[index] + " does not match " + v.type().argType() + " at index " + index;
                    throw new IllegalArgumentException(msg);
                }
            }
        }
        return Immutable.list(asList(results));
    }

    /**
     * Useful for testing and resultant string size estimation.
     *
     * @param segments expression segments
     * @return sample arguments
     * @see FmtType#argType()
     */
    public static Object[] exampleArguments(List<FormatSegment> segments) {
        Object[] args = new Object[argumentCount(segments)];
        for (FormatSegment segment : segments) {
            if (segment instanceof FormatVariable) {
                FormatVariable v = (FormatVariable) segment;
                setExample(v, args);
            }
        }
        return args;
    }

    private static void setExample(FormatVariable v, Object[] args) {
        int index = v.index();
        switch (v.type()) {
            case NONE:
                args[index] = "De finibus bonorum et malorum";
                break;
            case NUMBER:
            case CHOICE:
                args[index] = 10_000_000;
                break;
            case DATE:
            case TIME:
                args[index] = new Date(0);
                break;
            case LIST:
                args[index] = asList("Pugh", "Pugh", "Barney McGrew", "Cuthbert", "Dibble", "Grub");
                break;
            default:
                args[index] = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC"));
                break;
        }
    }

    /**
     * <p>
     *     Static format implementation.
     *     Approximates behaviour of {@link MessageFormat#format(String, Object...)}.
     * </p>
     * <p>
     *     <strong>Use of {@link FmtType#LIST} expressions requires JDK 22 or above.</strong>
     * </p>
     *
     * @param expression expression segments
     * @param l locale
     * @param args arguments
     * @return formatted string
     */
    public static String format(List<FormatSegment> expression, Locale l, Object...args) {
        StringBuffer buf = new StringBuffer();
        for (FormatSegment segment : expression) {
            segment.formatTo(l, buf, args);
        }
        return buf.toString();
    }

    /**
     * Estimates the length of a formatted expression.
     *
     * @param l the locale
     * @param expression expression segments
     * @return an estimation of required buffer size
     */
    public static int estimateLength(Locale l, List<FormatSegment> expression) {
        Object[] args = exampleArguments(expression);
        String result = format(expression, l, args);
        return normalize(result.length());
    }

    private static int normalize(int n) {
        int x = 8;
        while (x < n) {
            x *= 2;
        }
        return x * 2;
    }

    /**
     * Tests an expression to determine if a {@link Locale} is required to format an expression.
     * Returns true if any {@link FormatSegment} is a {@link FormatVariable} and
     * its {@link FormatVariable#type()} is NOT {@link FmtType#NONE}.
     *
     * @param expression parsed expression
     * @return true if a locale is required
     */
    public static boolean needsLocale(List<FormatSegment> expression) {
        for (FormatSegment segment : expression) {
            if (segment instanceof FormatVariable) {
                FormatVariable fv = (FormatVariable) segment;
                if (fv.type() != FmtType.NONE) {
                    return true;
                }
            }
        }
        return false;
    }
}
