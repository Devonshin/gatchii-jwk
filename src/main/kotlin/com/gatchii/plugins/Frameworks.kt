package com.gatchii.plugins

import com.gatchii.domains.jwk.JwkRepositoryImpl
import com.gatchii.domains.jwk.JwkServiceImpl
import com.gatchii.domains.jwk.JwkTable
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {

    val appModule = module {

        /*repositories*/
        single { JwkRepositoryImpl(JwkTable) }

        /*services*/
        single { JwkServiceImpl(get()) }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
