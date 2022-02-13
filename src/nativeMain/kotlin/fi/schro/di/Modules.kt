package fi.schro.di

import fi.schro.data.ElgatoLightRepository
import fi.schro.data.LightRepository
import fi.schro.ui.ApplyCommand
import fi.schro.ui.GetCommand
import fi.schro.ui.SetCommand
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.koin.dsl.module

val commandModule = module {
    single{ ApplyCommand() }
    single{ SetCommand() }
    single{ GetCommand(get()) }
}

val dataModule = module {
    single<LightRepository> { ElgatoLightRepository() }
}

val networkModule = module {
    single<HttpClient> { HttpClient(CIO) }
}

val mainModule = listOf(
    commandModule,
    dataModule,
    networkModule
)