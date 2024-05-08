// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format.testing;

public final class TestStrings {

    private TestStrings() {}

    public static String[] valid() {
        return new String[]{
                // simple
                "",
                "'",
                "''",
                "''foo''",
                "foo bar baz",
                "C''est la vie",
                "foo 'bar'",
                // args
                "{0}",
                "{1}",
                "{10}",
                "{0,number}",
                "{0,date}",
                "{0,time}",
                // quoted out args
                "'{0,number}",
                "'{0,number}'",
                "'{''}'",
                // core styles
                "{0,number,integer}",
                "{0,number,currency}",
                "{0,number,percent}",
                "{0,date,short}",
                "{0,date,medium}",
                "{0,date,long}",
                "{0,date,full}",
                "{0,time,short}",
                "{0,time,medium}",
                "{0,time,long}",
                "{0,time,full}",
                // subformats
                "{0,number,#,##0.##}",
                "{0,date,EE}",
                "{0,time,hh}",
                "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}.",
                // complex
                "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.",
                "{0} to {10}",
        };
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
