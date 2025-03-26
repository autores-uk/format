// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.util.Locale;

/**
 * Represents non-variable parts of format expressions.
 */
public final class FormatLiteral extends FormatSegment {
    private final String raw;
    private final String processed;

    FormatLiteral(String raw, String processed) {
        this.raw = raw;
        this.processed = processed;
    }

    @Override
    String raw() {
        return raw;
    }

    @Override
    void formatTo(Locale l, StringBuffer buf, Object... args) {
        buf.append(processed);
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
