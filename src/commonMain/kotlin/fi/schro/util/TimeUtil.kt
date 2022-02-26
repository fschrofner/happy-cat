/*
 * Copyright (c) 2022 Florian Schrofner
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fi.schro.util

import kotlinx.datetime.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TimeUtil {
    fun getCurrentDate(): LocalDate {
        val currentTime = Clock.System.now()
        return currentTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
}

fun LocalDate.atTime(time: LocalTime): LocalDateTime{
    return LocalDateTime(year, month, dayOfMonth, time.hour, time.minute, 0, 0)
}

@Serializable
data class LocalTime(val hour: Int, val minute: Int)

object LocalTimeSerializer: KSerializer<LocalTime>{
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Time", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalTime {
        val stringValue = decoder.decodeString()
        val values = stringValue.split(":").map { it.toInt() }
        return LocalTime(values[0], values[1])
    }

    override fun serialize(encoder: Encoder, value: LocalTime) {
        encoder.encodeString("${value.hour}:${value.minute}")
    }
}