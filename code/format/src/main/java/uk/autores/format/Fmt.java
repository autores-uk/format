// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.util.Locale;

@FunctionalInterface
interface Fmt {
    void format(Locale l, FormatVariable variable, StringBuffer buf, Object... args);
}
