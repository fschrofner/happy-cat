package fi.schro.ui

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import fi.schro.data.LightRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

const val ARG_TARGET_LAMP = "TARGET_LAMP"
const val ARG_CONFIGURATION_FILE = "CONFIGURATION_FILE"

class HappyCatCommand: CliktCommand(), KoinComponent {
    private val applyCommand: ApplyCommand by inject()
    private val getCommand: GetCommand by inject()
    private val setCommand: SetCommand by inject()

    init {
        subcommands(
            applyCommand,
            getCommand,
            setCommand
        )
    }

    override fun run() = Unit
}

class SetCommand: CliktCommand(name = "set", help = "Sets the defined values to the specified light"){
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

    private val powerState: LightPowerState? by option("-p", "--powerstate", help = "The state to be set").enum<LightPowerState>()


    override fun run() {
        echo("Values set")
    }
}

class GetCommand(
    private val lightRepository: LightRepository
) : CliktCommand(name = "get", help = "Gets and prints the current setting of the specified light") {
    private val targetLamp: String by argument(ARG_TARGET_LAMP)

    override fun run() {
        val status = lightRepository.getLightStatus(targetLamp)
        echo(status)
    }
}

class ApplyCommand: CliktCommand(name = "apply", help = "Applies the given configuration to the specified light"){
    private val configurationFile: String by argument(ARG_CONFIGURATION_FILE)
    private val targetLamp: String by argument(ARG_TARGET_LAMP)

    override fun run() {
        echo("Configuration applied")
    }
}

enum class LightPowerState(val stringValue: String, val intValue: Int) {
    ON("ON", 1),
    OFF("OFF", 0);

    companion object {
        fun fromInt(intValue: Int): LightPowerState? {
            return values().firstOrNull { it.intValue == intValue }
        }
    }
}