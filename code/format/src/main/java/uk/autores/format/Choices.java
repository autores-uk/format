// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.text.ChoiceFormat;
import java.util.Locale;

final class Choices {
    private Choices() {}

    static void format(Locale l, FormatVariable variable, StringBuffer buf, Object... args) {
        Object value = args[variable.index()];
        variable.requireNonNull(value);

        ChoiceFormat format = new ChoiceFormat(variable.subformat());
        String choice = format.format(value);
        if (choice.indexOf('{') >= 0) {
            FormatExpression recursive = FormatExpression.parse(choice);
            recursive.formatTo(l, buf, args);
        } else {
            buf.append(choice);
        }
    }
}
