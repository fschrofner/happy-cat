package fi.schro.data

import fi.schro.util.FileUtil
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import platform.posix.fopen

interface ConfigurationRepository {
    suspend fun applyConfiguration(configurationFilePath: String, lightAddress: String, port: Int? = null)
}

class ConfigurationRepositoryImpl(
    private val lightRepository: LightRepository
): ConfigurationRepository {
    override suspend fun applyConfiguration(configurationFilePath: String, lightAddress: String, port: Int?) {
        val configString = FileUtil.readAllText(configurationFilePath)
        TODO("Not yet implemented")
    }

    private suspend fun applyLightStatus(lightAddress: String, port: Int?, status: LightStatus){
        val currentStatus = lightRepository.getLightStatus(lightAddress)
        currentStatus.getNecessaryChanges(status)?.let { statusUpdate ->
            lightRepository.setLightStatus(lightAddress, port, statusUpdate)
        }
    }
}