// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.util.Locale;

/**
 * Represents parts of format expressions that are not {@link FormatVariable}s.
 */
public final class FormatLiteral extends Formatter {
    private static final FormatLiteral[] INTERNED = {
            new FormatLiteral(" ", " "),
            new FormatLiteral(".", "."),
            new FormatLiteral("?", "?"),
            new FormatLiteral("''", "'"),
    };

    private final String raw;
    private final String processed;

    private FormatLiteral(String raw, String processed) {
        this.raw = raw;
        this.processed = processed;
    }

    static FormatLiteral from(String raw, String processed) {
        for (FormatLiteral in : INTERNED) {
            if (in.raw.equals(raw)) {
                return in;
            }
        }
        return new FormatLiteral(raw, processed);
    }

    @Override
    public void formatTo(Locale l, StringBuffer buf, Object... args) {
        buf.append(processed);
    }

    @Override
    public String toString() {
        return raw;
    }

    /**
     * Processes escape sequences.
     *
     * @return escape processed string
     */
    public String processed() {
        return processed;
    }
}
