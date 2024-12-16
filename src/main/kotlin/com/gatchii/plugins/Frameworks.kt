package com.gatchii.plugins

import com.gatchii.domains.jwk.*
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {

    val appModule = module {
        /*repositories*/
        single<JwkRepository> {
            JwkRepositoryImpl(JwkTable)
        }

        /*services*/

        single<JwkService>(createdAtStart = false) {
            val config = environment.config
            val jwkConfig = config.config("jwk")
            JwkServiceImpl(get(), jwkConfig)
        }

    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
