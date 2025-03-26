package uk.autores.format;

import java.text.ChoiceFormat;
import java.util.List;
import java.util.Locale;

final class FabricateChoice {
    private FabricateChoice() {}

    static void choose(Locale l, FormatVariable variable, StringBuffer buf, Object... args) {
        Object value = args[variable.index()];
        ChoiceFormat format = new ChoiceFormat(variable.subformat());
        String choice = format.format(value);
        String result;
        if (choice.indexOf('{') >= 0) {
            List<FormatSegment> recursive = Formatting.parse(choice);
            result = Formatting.format(recursive, l, args);
        } else {
            result = choice;
        }
        buf.append(result);
    }
}
