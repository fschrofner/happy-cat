package fi.schro.util

import kotlin.math.max
import kotlin.math.min

fun Int.clamp(start: Int, end: Int): Int {
    return min(end, max(start, this))
}