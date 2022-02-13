package fi.schro.data

import fi.schro.ui.LightPowerState

interface LightRepository {
    fun setLightStatus(lightAddress: String, status: LightStatus)
    fun getLightStatus(lightAddress: String): LightStatus
}

data class LightStatus(
    val powerStatus: LightPowerState?,
    val brightness: Int?,
    val temperature: Int?
){
    override fun toString(): String {
        val stringList = mutableListOf<String>()
        powerStatus?.let { stringList.add("status: ${powerStatus.stringValue}") }
        brightness?.let { stringList.add("brightness: $brightness") }
        temperature?.let { stringList.add("temperature: $temperature") }
        return stringList.joinToString(separator = "\n")
    }
}
