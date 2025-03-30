// Copyright 2024-2025 https://github.com/autores-uk/format/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.format;

import java.util.Collections;
import java.util.List;

final class Immutable {

    private Immutable() {}

    static <E> List<E> list(List<E> mutable) {
        if (mutable.isEmpty()) {
            return Collections.emptyList();
        }
        if (mutable.size() == 1) {
            return Collections.singletonList(mutable.get(0));
        }
        return Collections.unmodifiableList(mutable);
    }
}
