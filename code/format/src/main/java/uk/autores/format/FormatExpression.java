// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * <p>
 *     Type that approximates the behaviour of {@link java.text.MessageFormat}.
 * </p>
 * Features:
 * <ul>
 *     <li>Immutable/thread safe</li>
 *     <li>Supports JDK23 expressions at lower JDK versions</li>
 *     <li>Exposes more parsed expression metadata</li>
 * </ul>
 */
public final class FormatExpression extends Formatter implements Iterable<Formatter> {
    private final Formatter[] expr;
    private final int vars;

    FormatExpression(Formatter[] expr, int vars) {
        this.expr = expr;
        this.vars = vars;
    }

    @Override
    public void formatTo(Locale l, StringBuffer buf, Object... args) {
        for (Formatter f : expr) {
            f.formatTo(l, buf, args);
        }
    }

    @Override
    public String toString() {
        return Chars.concat(expr);
    }

    /**
     * Constituent {@link FormatLiteral}s and {@link FormatVariable}s.
     *
     * @return component parts, in order
     */
    @Override
    public Iterator<Formatter> iterator() {
        return asList(expr).iterator();
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
     * @return argument count or zero if there are no variables
     */
    public int argCount() {
        return vars;
    }

    /**
     * <p>
     *     The types for any {@link FormatVariable}s.
     * </p>
     * <p>
     *     The {@link FmtType#argType()}s map to the {@link FormatVariable#index()}.
     *     {@link Void} is returned for any missing arguments.
     * </p>
     *
     * @return types by index
     */
    public Class<?>[] argTypes() {
        Class<?>[] results = new Class<?>[vars];
        Arrays.fill(results, Void.class);
        for (Formatter segment : expr) {
            if (segment instanceof FormatVariable v) {
                results[v.index()] = v.type().argType();
            }
        }
        return results;
    }

    /**
     * Useful for testing and string size estimation.
     *
     * @return sample arguments
     * @see FmtType#argType()
     */
    public Object[] argExamples() {
        Object[] args = new Object[vars];
        for (Formatter f: expr) {
            if (f instanceof FormatVariable variable) {
                Examples.set(args, variable);
            }
        }
        return args;
    }

    /**
     * Estimates the length of the evaluated expression.
     *
     * @param l locale
     * @return suggested string buffer size
     */
    public int estimateLen(Locale l) {
        Object[] args = argExamples();
        StringBuffer buf = new StringBuffer();
        int len = 0;
        for (Formatter segment : expr) {
            segment.formatTo(l, buf, args);
            len += buf.length();
            buf.delete(0, buf.length());
        }
        return normalize(len);
    }

    private int normalize(int n) {
        int x = 8;
        while (x < n) {
            x *= 2;
        }
        return x * 2;
    }

    /**
     * Tests an expression to determine if a {@link Locale} is required to format an expression.
     * True if any {@link Formatter} is a {@link FormatVariable} AND
     * any {@link FormatVariable#type()} is NOT {@link FmtType#NONE}.
     *
     * @return true if a locale is required
     */
    public boolean needsLocale() {
        for (Formatter segment : expr) {
            if (segment instanceof FormatVariable fv && fv.type() != FmtType.NONE) {
                return true;
            }
        }
        return false;
    }

    int size() {
        return expr.length;
    }

    Formatter get(int i) {
        return expr[i];
    }

    /**
     * Parses a {@link java.text.MessageFormat} expression.
     *
     * @param seq source text
     * @return parsed expression
     */
    public static FormatExpression parse(CharSequence seq) {
        List<Formatter> list = new ArrayList<>();
        int offset = 0;
        for (int i = 0; i < seq.length(); i++) {
            char ch = seq.charAt(i);
            if (ch == '\'') {
                addRaw(list, seq, offset, i);
                FormatLiteral segment = parseEscaped(seq, i);
                list.add(segment);
                rationalize(list);
                i += segment.toString().length();
                offset = i;
            } else if (ch == '{') {
                addRaw(list, seq, offset, i);
                rationalize(list);
                FormatVariable segment = parseVariable(seq, i);
                list.add(segment);
                i += segment.toString().length();
                offset = i;
                i--;
            }
        }
        if (offset < seq.length()) {
            addRaw(list, seq, offset, seq.length());
            rationalize(list);
        }
        Formatter[] expr = list.toArray(new Formatter[0]);
        int vars = argCount(expr);
        validateTypes(expr, vars);
        return new FormatExpression(expr, vars);
    }

    private static void rationalize(List<Formatter> expr) {
        if (expr.size() > 1) {
            int i0 = expr.size() - 1;
            Formatter s0 = expr.get(i0);
            int i1 = expr.size() - 2;
            Formatter s1 = expr.get(i1);
            if (s0 instanceof FormatLiteral l0 && s1 instanceof FormatLiteral l1) {
                expr.remove(i0);
                expr.remove(i1);
                FormatLiteral combined = new FormatLiteral(
                        l1.toString() + l0,
                        l1.processed() + l0.processed()
                );
                expr.add(combined);
            }
        }
    }

    private static void addRaw(List<Formatter> list, CharSequence seq, int start, int end) {
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
        Arrays.sort(NAMED_FMT_TYPES, FormatExpression::longestFirst);
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

    private static void validateTypes(Formatter[] expr, int vars) {
        Object[] args = new Object[vars];
        Arrays.fill(args, Void.class);
        for (Formatter segment : expr) {
            if (segment instanceof FormatVariable v) {
                int index = v.index();
                if (args[index] == Void.class) {
                    args[index] = v.type().argType();
                } else if (args[index] != v.type().argType()) {
                    String msg = args[index] + " does not match " + v.type().argType() + " at index " + index;
                    throw new IllegalArgumentException(msg);
                }
            }
        }
    }

    static int argCount(Formatter[] s) {
        int max = 0;
        for (Formatter segment : s) {
            if (segment instanceof FormatVariable v) {
                int n = v.index() + 1;
                max = Math.max(max, n);
            }
        }
        return max;
    }
}
