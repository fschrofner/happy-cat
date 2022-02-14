package fi.schro.data

import fi.schro.ui.LightPowerState
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

class ElgatoLightRepository(private val httpClient: HttpClient): LightRepository {
    //TODO: add light endpoint constant
    private val PATH_SEPARATOR = "/"
    private val DEFAULT_PATH = "elgato"
    private val LIGHT_PATH = "lights"
    private val ACCESSORY_INFO_PATH = "accessory-info"

    val LIGHT_ENDPOINT = listOf(DEFAULT_PATH, LIGHT_PATH)
    val ACCESSORY_INFO_ENDPOINT = listOf(DEFAULT_PATH, ACCESSORY_INFO_PATH)

    override suspend fun setLightStatus(lightAddress: String, port: Int?, status: LightStatus) {
        TODO("Not yet implemented")
    }

    override suspend fun getLightStatus(lightAddress: String, port: Int?): LightStatus {
        httpClient.use {
            val status = httpClient.get<ElgatoLightStatus>("http://" + lightAddress + ":" + (port ?: 9123) + createPath(LIGHT_ENDPOINT))
            return status.toLightStatus()
        }
    }

    private fun createPath(pathElements: List<String>): String {
        return pathElements.joinToString(separator = PATH_SEPARATOR, prefix = PATH_SEPARATOR)
    }
}

@Serializable
data class ElgatoLightStatus(
    @SerialName("lights") val lights: List<ElgatoLight>,
    @SerialName("numberOfLights") val numberOfLights: Int = lights.size
){
    fun toLightStatus(): LightStatus {
        return lights.first().toLightStatus()
    }
}

@Serializable
data class ElgatoLight(
    @SerialName("on") val on: Int? = null,
    @SerialName("brightness") val brightness: Int? = null,
    @SerialName("temperature") val temperature: Int? = null
){
    fun toLightStatus(): LightStatus {
        return LightStatus(
            powerStatus = on?.let { LightPowerState.fromInt(it) },
            brightness = brightness,
            temperature = temperature?.let { convertElgatoTemperatureToKelvin(it) }
            )
    }
}

private fun convertKelvinToElgatoTemperature(kelvinTemperature: Int): Int {
    TODO("todo")
}

private fun convertElgatoTemperatureToKelvin(elgatoTemperature: Int): Int {
    //based on: https://github.com/justinforlenza/keylight-control/blob/main/src/keylight.js
    return (((-4100*elgatoTemperature) / 201f) + 1993300/201f).roundToInt()
}