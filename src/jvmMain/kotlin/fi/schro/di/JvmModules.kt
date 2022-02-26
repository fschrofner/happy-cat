/*
 * Copyright (c) 2022 Florian Schrofner
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package fi.schro.di

import fi.schro.util.FileUtil
import fi.schro.util.FileUtilImpl
import org.koin.dsl.module
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

val commandModule = module {
    single { ApplyCommand(get()) }
    single { DaemonCommand(get()) }
    single { SetCommand(get()) }
    single { GetCommand(get()) }
}

val dataModule = module {
    single<LightRepository> { ElgatoLightRepository() }
    single<ConfigurationRepository> { ConfigurationRepositoryImpl(get(), get()) }
}

val networkModule = module {
    //HttpClient has to be recreated every time it is used
    factory<HttpClient> {
        HttpClient(CIO) {
            install(JsonFeature)
        }
    }
}

val fileModule = module {
    single<FileUtil> { FileUtilImpl() }
}

val jvmModules = listOf(
    commandModule,
    dataModule,
    networkModule,
    fileModule
)