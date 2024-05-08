// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class FormatGeneration {
    private static final String[] ESCS = generateEscapes();

    private FormatGeneration() {}

    private static String[] generateEscapes() {
        // backslash is last escape char
        int end = '\\' + 1;
        String[] arr = new String[end];
        for (int i = 0; i < end; i++) {
            arr[i] = String.valueOf((char) i);
        }
        arr['\t'] = "\\t";
        arr['\b'] = "\\b";
        arr['\n'] = "\\n";
        arr['\r'] = "\\r";
        arr['\f'] = "\\f";
        arr['"'] = "\\\"";
        arr['\\'] = "\\\\";
        return arr;
    }

    public static List<String> args(List<FormatSegment> expression) {
        int count = Formatting.argumentCount(expression);
        String[] args = new String[count];
        for (int i = 0; i < args.length; i++) {
            args[i] = Void.class.getName() + " arg" + i;
        }
        for (FormatSegment segment : expression) {
            if (segment instanceof FormatVariable) {
                FormatVariable v = (FormatVariable) segment;
                int i = v.index();
                Class<?> c = v.type().argType();
                args[i] = c.getName() + " arg" + i;
            }
        }
        return Arrays.asList(args);
    }

    public static List<String> expressions(List<FormatSegment> expression) {
        List<String> result = new ArrayList<>(expression.size() * 2);
        int est = Formatting.estimateLength(expression);
        result.add("java.lang.StringBuffer buf = new java.lang.StringBuffer(" + est + ");");
        for (FormatSegment segment : expression) {
            if (segment instanceof FormatLiteral) {
                FormatLiteral lit = (FormatLiteral) segment;
                String escaped = escape(lit.processed());
                String append = "buf.append(" + escaped + ");";
                result.add(append);
            } else if (segment instanceof FormatVariable) {
                add(result, (FormatVariable) segment);
            }
        }
        return result;
    }

    private static String escape(String s) {
        StringBuilder buf = new StringBuilder(s.length() + 2);
        buf.append("\"");
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch < ESCS.length) {
                buf.append(ESCS[i]);
            } else {
                buf.append(ch);
            }
        }
        buf.append("\"");
        return buf.toString();
    }

    private static void add(List<String> expressions, FormatVariable v) {
        switch (v.type()) {
            case NONE:
                none(expressions, v);
                break;
            case NUMBER:
                number(expressions, v);
                break;
            case DATE:
                date(expressions, v);
                break;
            case TIME:
                time(expressions, v);
                break;
            case CHOICE:
                choice(expressions, v);
                break;
        }
    }

    private static void none(List<String> expressions, FormatVariable v) {
        String expr = "buf.append(arg" + v.index() + ");";
        expressions.add(expr);
    }

    private static void number(List<String> expressions, FormatVariable v) {
        String inst;
        switch (v.style()) {
            case INTEGER:
                inst = "java.text.NumberFormat.getIntegerInstance(l)";
                break;
            case CURRENCY:
                inst = "java.text.NumberFormat.getCurrencyInstance(l)";
                break;
            case PERCENT:
                inst = "java.text.NumberFormat.getPercentInstance(l)";
                break;
            case SUBFORMAT:
                String esc = escape(v.subformat());
                String symbols = "java.text.DecimalFormatSymbols.getInstance(l)";
                inst = "new java.text.DecimalFormat(" + esc + ", " + symbols + ")";
                break;
            default:
                inst = "java.text.NumberFormat.getInstance(l)";
        }
        String format = inst + ".format(arg" + v.index() + ")";
        String append = "buf.append(" + format + ")";
        expressions.add(append);
    }

    private static void date(List<String> expressions, FormatVariable v) {
        expressions.add("{");
        String inst;
        switch (v.style()) {
            case SHORT:
                inst = "java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, l)";
                break;
            case LONG:
                inst = "java.text.DateFormat.getDateInstance(java.text.DateFormat.LONG, l)";
                break;
            case FULL:
                inst = "java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, l)";
                break;
            case SUBFORMAT:
                String esc = escape(v.subformat());
                inst = "new java.text.SimpleDateFormat(" + esc + ", l)";
                break;
            case MEDIUM:
            default:
                inst = "java.text.DateFormat.getDateInstance(java.text.DateFormat.DEFAULT, l)";
        }
        expressions.add("java.time.ZoneId zoneId = arg" + v.index() + ".getZone();");
        expressions.add("java.util.TimeZone zone = java.util.TimeZone.getTimeZone(zoneId);");
        expressions.add("java.text.DateFormat format = " + inst + ";");
        expressions.add("format.setTimeZone(zone);");
        expressions.add("buf.append(format.format(arg" + v.index() + "));");
        expressions.add("}");
    }

    private static void time(List<String> expressions, FormatVariable v) {
        expressions.add("{");
        String inst;
        switch (v.style()) {
            case SHORT:
                inst = "java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, l)";
                break;
            case LONG:
                inst = "java.text.DateFormat.getTimeInstance(java.text.DateFormat.LONG, l)";
                break;
            case FULL:
                inst = "java.text.DateFormat.getTimeInstance(java.text.DateFormat.FULL, l)";
                break;
            case SUBFORMAT:
                String esc = escape(v.subformat());
                inst = "new java.text.SimpleDateFormat(" + esc + ", l)";
                break;
            case MEDIUM:
            default:
                inst = "java.text.DateFormat.getTimeInstance(java.text.DateFormat.DEFAULT, l)";
        }
        expressions.add("java.time.ZoneId zoneId = arg" + v.index() + ".getZone();");
        expressions.add("java.util.TimeZone zone = java.util.TimeZone.getTimeZone(zoneId);");
        expressions.add("java.text.DateFormat format = " + inst + ";");
        expressions.add("format.setTimeZone(zone);");
        expressions.add("buf.append(format.format(arg" + v.index() + "));");
        expressions.add("}");
    }

    private static void choice(List<String> expressions, FormatVariable v) {
        expressions.add("{");

        String pattern = escape(v.subformat());
        expressions.add("java.text.ChoiceFormat format = new java.text.ChoiceFormat(" + pattern + ");");
        expressions.add("java.lang.String result = format.format(arg" + v.index() + ");");
        expressions.add("if (result.indexOf('{') >= 0) {");
        expressions.add("// TODO");
        expressions.add("}");
        expressions.add("buf.append(result);");

        expressions.add("}");
    }
}
