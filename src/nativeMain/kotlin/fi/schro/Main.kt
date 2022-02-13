package fi.schro

import fi.schro.di.mainModule
import fi.schro.ui.HappyCatCommand
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(mainModule)
    }

    HappyCatCommand()
        .main(args)
}