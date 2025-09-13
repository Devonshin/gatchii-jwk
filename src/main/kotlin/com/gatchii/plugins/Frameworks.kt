package com.gatchii.plugins

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.gatchii.common.tasks.RoutineScheduleExpression
import com.gatchii.common.tasks.RoutineTaskHandler
import com.gatchii.domain.jwk.*
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.util.concurrent.TimeUnit

fun Application.configureFrameworks() {

    val appModule = module {

        val config = environment.config.config("jwk")
        single<JwkProvider> {
            JwkProviderBuilder(config.property("issuer").getString())
                .cached(10, 24, TimeUnit.HOURS)
                .rateLimited(10, 1, TimeUnit.MINUTES)
                .build()
        }

        /*repositories*/
        single<JwkRepository> { JwkRepositoryImpl(JwkTable) }

        /*services*/
        single<JwkService> {
            JwkServiceImpl(get()) { task: () -> Unit ->
                RoutineTaskHandler(
                    taskName = "jwkTask",
                    scheduleExpression = RoutineScheduleExpression(),
                    task = task
                )
            }
        }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
        println("Framework installed")
    }
}
