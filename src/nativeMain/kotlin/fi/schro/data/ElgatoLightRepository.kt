package fi.schro.data

import fi.schro.ui.LightPowerStatus
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.math.roundToInt

class ElgatoLightRepository: LightRepository, KoinComponent {
    private val PATH_SEPARATOR = "/"
    private val DEFAULT_PATH = "elgato"
    private val LIGHT_PATH = "lights"
    private val ACCESSORY_INFO_PATH = "accessory-info"

    //needs a new http client for every request
    private val httpClient: HttpClient get() {
        return get()
    }

    val LIGHT_ENDPOINT = listOf(DEFAULT_PATH, LIGHT_PATH)
    val ACCESSORY_INFO_ENDPOINT = listOf(DEFAULT_PATH, ACCESSORY_INFO_PATH)

    override suspend fun setLightStatus(lightAddress: String, port: Int?, status: LightStatus) {
        val elgatoStatus = ElgatoLightStatus.fromLightStatus(status)

        httpClient.use {
            val response: HttpResponse = it.put(createUrl(lightAddress, port, LIGHT_ENDPOINT)){
                contentType(ContentType.Application.Json)
                body = elgatoStatus
            }
        }
    }

    override suspend fun getLightStatus(lightAddress: String, port: Int?): LightStatus {
        httpClient.use {
            val status = it.get<ElgatoLightStatus>(createUrl(lightAddress, port, LIGHT_ENDPOINT))
            return status.toLightStatus()
        }
    }

    private fun createPath(pathElements: List<String>): String {
        return pathElements.joinToString(separator = PATH_SEPARATOR, prefix = PATH_SEPARATOR)
    }

    private fun createUrl(lightAddress: String, port: Int?, endpoint: List<String>): String {
        return "http://" + lightAddress + ":" + (port ?: 9123) + createPath(endpoint)
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

    companion object {
        fun fromLightStatus(status: LightStatus): ElgatoLightStatus {
            return ElgatoLightStatus(listOf(
                ElgatoLight.fromLightStatus(status)
            ))
        }
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
            powerStatus = on?.let { LightPowerStatus.fromInt(it) },
            brightness = brightness,
            temperature = temperature?.let { convertElgatoTemperatureToKelvin(it) }
            )
    }

    companion object {
        fun fromLightStatus(status: LightStatus): ElgatoLight {
            return ElgatoLight(
                on = status.powerStatus?.intValue,
                brightness = status.brightness,
                temperature = status.temperature?.let { convertKelvinToElgatoTemperature(it) }
            )
        }
    }
}

//TODO: these calculations seem weird, maybe they could be improved
private fun convertKelvinToElgatoTemperature(kelvinTemperature: Int): Int {
    //based on: https://github.com/justinforlenza/keylight-control/blob/main/src/keylight.js
    return (((kelvinTemperature - 1993300/201f) * 201f)/-4100f).roundToInt()
}

private fun convertElgatoTemperatureToKelvin(elgatoTemperature: Int): Int {
    //based on: https://github.com/justinforlenza/keylight-control/blob/main/src/keylight.js
    return (((-4100*elgatoTemperature) / 201f) + 1993300/201f).roundToInt()
}