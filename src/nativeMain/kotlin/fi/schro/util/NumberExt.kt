/*
 * Copyright (c) 2022 Florian Schrofner
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fi.schro.util

import kotlin.math.max
import kotlin.math.min

fun Int.clamp(start: Int, end: Int): Int {
    return min(end, max(start, this))
}