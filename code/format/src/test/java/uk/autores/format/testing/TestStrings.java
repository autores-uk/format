// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format.testing;

import uk.autores.format.FmtStyle;
import uk.autores.format.FmtType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TestStrings {

    private TestStrings() {}

    public static String[] valid() {
        String[] exprs = new String[]{
                // simple
                "",
                "'",
                "''",
                "''foo''",
                "foo bar baz",
                "C''est la vie",
                "foo 'bar'",
                "'foo' 'bar' 'baz'",
                // args
                "{0}",
                "{1}",
                "{10}",
                // quoted out args
                "'{0,number}",
                "'{0,number}'",
                "'{''}'",
                // subformats
                "{0,number,#,##0.##}",
                "{0,number,''#,##0.##''}",
                "{0,date,EE}",
                "{0,time,hh}",
                "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.",
                // complex
                "{0} to {10}",
                "{0} {0}",
                "{0,number} {0,choice,0#foo|0<bar}{0,number,currency}",
                "{0,date}{0,time}",
                "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.",
                "{0}{0}{0}",
                // JDK23 expressions
                "{0,dtf_date,yy}",
                "{0,dtf_time,hh}",
                "{0,dtf_datetime,yy}",
        };

        List<String> expressions = new ArrayList<>(Arrays.asList(exprs));
        for (FmtType type : FmtType.values()) {
            if (type == FmtType.NONE || type == FmtType.CHOICE) {
                continue;
            }
            expressions.addAll(construct(type));
        }
        return expressions.toArray(new String[]{});
    }

    private static List<String> construct(FmtType type) {
        List<String> expressions = new ArrayList<>();
        for (FmtStyle style : type.styles()) {
            String expr = "{0," + type.label();
            if (style != FmtStyle.NONE && style != FmtStyle.SUBFORMAT) {
                expr += "," + style.label();
            }
            expr += "}";
            expressions.add(expr);
        }
        return expressions;
    }

    public static String[] invalid() {
        return new String[]{
                "{n}",
                "{0",
                "{0x",
                "{0,",
                "{0,number",
                "{0,number!",
                "{0,number,currency",
                "{0,foobar}",
                "{0,number,'}",
                "{}",
                "{,",
                "{0,number,##",
        };
    }
}
