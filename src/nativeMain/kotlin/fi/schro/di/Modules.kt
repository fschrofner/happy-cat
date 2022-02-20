package fi.schro.di

import fi.schro.data.ConfigurationRepository
import fi.schro.data.ConfigurationRepositoryImpl
import fi.schro.data.ElgatoLightRepository
import fi.schro.data.LightRepository
import fi.schro.ui.ApplyCommand
import fi.schro.ui.DaemonCommand
import fi.schro.ui.GetCommand
import fi.schro.ui.SetCommand
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import org.koin.dsl.module

val commandModule = module {
    single{ ApplyCommand(get()) }
    single{ DaemonCommand(get()) }
    single{ SetCommand(get()) }
    single{ GetCommand(get()) }
}

val dataModule = module {
    single<LightRepository> { ElgatoLightRepository() }
    single<ConfigurationRepository> { ConfigurationRepositoryImpl(get()) }
}

val networkModule = module {
    //HttpClient has to be recreated every time it is used
    factory<HttpClient> { HttpClient(CIO){
        install(JsonFeature)
    }}
}

val mainModule = listOf(
    commandModule,
    dataModule,
    networkModule
)