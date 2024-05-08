// Copyright 2024 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import org.junit.jupiter.api.Test;
import uk.autores.format.testing.TestStrings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FormatGenerationTest {

    @Test
    void exercise() {
        for (String t : TestStrings.valid()) {
            List<FormatSegment> expression = Formatting.parse(t);
            List<?> args = FormatGeneration.args(expression);
            List<?> expressions = FormatGeneration.expressions(expression);
            assertNotNull(args);
            assertNotNull(expressions);
        }
    }
}
