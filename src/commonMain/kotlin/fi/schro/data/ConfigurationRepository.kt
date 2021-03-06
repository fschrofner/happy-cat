/*
 * Copyright (c) 2022 Florian Schrofner
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fi.schro.data

import com.github.ajalt.clikt.output.TermUi.echo
import fi.schro.util.*
import kotlinx.datetime.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface ConfigurationRepository {
    suspend fun applyConfiguration(configurationFilePath: String, lightAddress: String, port: Int? = null)
}

class ConfigurationRepositoryImpl(
    private val lightRepository: LightRepository,
    private val fileUtil: FileUtil
): ConfigurationRepository {
    private val timezone = TimeZone.currentSystemDefault()

    override suspend fun applyConfiguration(configurationFilePath: String, lightAddress: String, port: Int?) {
        val configString = fileUtil.readAllText(configurationFilePath)
        val configuration = Json.decodeFromString<Configuration>(configString)
        val statusToApply = determineCurrentStatus(configuration)

        statusToApply?.let { newStatus ->
            applyLightStatus(lightAddress, port, newStatus)
        }
    }

    private fun determineCurrentStatus(configuration: Configuration): LightStatus? {
        val currentTime = Clock.System.now().toLocalDateTime(timezone)
        val currentDate = TimeUtil.getCurrentDate()

        return configuration.config.firstOrNull { timedConfiguration ->
            val start = currentDate.atTime(timedConfiguration.start)
            val end = currentDate.atTime(timedConfiguration.end)

            //timespan crosses midnight
            if(start > end){
                currentTime in currentDate.atTime(0,0) .. end
                        || currentTime in start .. currentDate.plus(1, DateTimeUnit.DAY).atTime(0,0)
            } else currentTime in start..end
        }?.status
    }

    private suspend fun applyLightStatus(lightAddress: String, port: Int?, status: LightStatus){
        val currentStatus = lightRepository.getLightStatus(lightAddress)
        currentStatus.getNecessaryChanges(status)?.let { statusUpdate ->
            echo("applying status changes:")
            echo(statusUpdate)
            lightRepository.setLightStatus(lightAddress, port, statusUpdate)
        }
    }
}

@Serializable
data class Configuration(
    @SerialName("config") val config: List<TimedStatusConfiguration>
)

@Serializable
data class TimedStatusConfiguration(
    @SerialName("start") @Serializable(with = LocalTimeSerializer::class) val start: LocalTime,
    @SerialName("end") @Serializable(with = LocalTimeSerializer::class) val end: LocalTime,
    @SerialName("status") val status: LightStatus
)