package fi.schro.data

import fi.schro.ui.LightPowerStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface LightRepository {
    suspend fun setLightStatus(lightAddress: String, port: Int? = null, status: LightStatus)
    suspend fun getLightStatus(lightAddress: String, port: Int? = null): LightStatus
}

@Serializable
data class LightStatus(
    @SerialName("power") val powerStatus: LightPowerStatus?,
    @SerialName("brightness") val brightness: Int?,
    @SerialName("temperature") val temperature: Int?
){
    override fun toString(): String {
        val stringList = mutableListOf<String>()
        powerStatus?.let { stringList.add("power: ${powerStatus.stringValue}") }
        brightness?.let { stringList.add("brightness: $brightness") }
        temperature?.let { stringList.add("temperature: $temperature") }
        return stringList.joinToString(separator = "\n")
    }

    fun getNecessaryChanges(otherStatus: LightStatus): LightStatus? {
        val statusUpdates = LightStatus(
            powerStatus = if(powerStatus != otherStatus.powerStatus) otherStatus.powerStatus else null,
            brightness = if(brightness != otherStatus.brightness) otherStatus.brightness else null,
            temperature = if(temperature != otherStatus.temperature) otherStatus.temperature else null
        )

        return if(statusUpdates.powerStatus != null || statusUpdates.brightness != null || statusUpdates.temperature != null){
            statusUpdates
        } else null
    }
}
