/*
 * Copyright (c) 2022 Florian Schrofner
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

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