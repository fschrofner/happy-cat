package fi.schro.data

import fi.schro.ui.LightPowerState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

class ElgatoLightRepository: LightRepository {
    override fun setLightStatus(lightAddress: String, status: LightStatus) {
        TODO("Not yet implemented")
    }

    override fun getLightStatus(lightAddress: String): LightStatus {
        TODO("Not yet implemented")
    }
}

@Serializable
data class ElgatoLightStatus(
    @SerialName("lights") val lights: List<ElgatoLight>,
    @SerialName("numberOfLights") val numberOfLights: Int = lights.size
)

@Serializable
data class ElgatoLight(
    @SerialName("on") val on: Int? = null,
    @SerialName("brightness") val brightness: Int? = null,
    @SerialName("temperature") val temperature: Int? = null
){
    fun toLightStatus(){
        LightStatus(
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