package fi.schro.ui

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import fi.schro.data.ConfigurationRepository
import fi.schro.data.LightRepository
import fi.schro.data.LightStatus
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

const val ARG_TARGET_LAMP = "TARGET_LAMP"
const val ARG_CONFIGURATION_FILE = "CONFIGURATION_FILE"

class HappyCatCommand: CliktCommand(name = "hc", help = "A commandline utility to control your elgato keylight"), KoinComponent {
    private val applyCommand: ApplyCommand by inject()
    private val daemonCommand: DaemonCommand by inject()
    private val getCommand: GetCommand by inject()
    private val setCommand: SetCommand by inject()

    init {
        subcommands(
            applyCommand,
            daemonCommand,
            getCommand,
            setCommand
        )
    }

    override fun run() = Unit
}

class SetCommand(
    private val lightRepository: LightRepository
): CliktCommand(name = "set", help = "Sets the defined values to the specified light"){
    private val targetLamp: String by argument(ARG_TARGET_LAMP)
    private val brightness: Int? by option(
        "-b",
        "--brightness",
        help = "The brightness to be set in percent of the maximum"
    ).int().check("Value must be between 0 and 100") {
        it in 0..100
    }
    private val temperature: Int? by option("-t", "--temperature", help = "The temperature to be set in Kelvin").int()
        .check("Value must be between 1,000 and 10,000") {
            it in 1000..10000
        }

    private val powerStatus: LightPowerStatus? by option("-p", "--power", help = "The power status to be set").enum<LightPowerStatus>()


    override fun run() {
        val status = LightStatus(
            powerStatus = powerStatus,
            brightness = brightness,
            temperature = temperature
        )

        runBlocking {
            lightRepository.setLightStatus(lightAddress = targetLamp, status = status)
        }
    }
}

class GetCommand(
    private val lightRepository: LightRepository
) : CliktCommand(name = "get", help = "Gets and prints the current setting of the specified light") {
    private val targetLamp: String by argument(ARG_TARGET_LAMP)

    override fun run() {
        runBlocking {
            val status = lightRepository.getLightStatus(targetLamp)
            echo(status)
        }
    }
}

class ApplyCommand(
    private val configurationRepository: ConfigurationRepository
): CliktCommand(name = "apply", help = "Applies the currently valid configuration inside the configuration file to the specified light"){
    private val configurationFile: String by argument(ARG_CONFIGURATION_FILE)
    private val targetLamp: String by argument(ARG_TARGET_LAMP)

    override fun run() {
        runBlocking {
            configurationRepository.applyConfiguration(configurationFile, targetLamp)
        }
    }
}

class DaemonCommand(
    private val configurationRepository: ConfigurationRepository
): CliktCommand(name = "daemon", help = "Starts a daemon which applies the currently valid configuration inside the configuration file every minute"){
    private val configurationFile: String by argument(ARG_CONFIGURATION_FILE)
    private val targetLamp: String by argument(ARG_TARGET_LAMP)

    override fun run() {
        runBlocking {
            while(isActive){
                //continue daemon even if applying configuration failed once
                try {
                    configurationRepository.applyConfiguration(configurationFile, targetLamp)
                } catch (exception: Exception){
                    echo(exception)
                }
                delay(1000 * 60)
            }
        }
    }
}

enum class LightPowerStatus(val stringValue: String, val intValue: Int) {
    ON("ON", 1),
    OFF("OFF", 0);

    companion object {
        fun fromInt(intValue: Int): LightPowerStatus? {
            return values().firstOrNull { it.intValue == intValue }
        }
    }
}