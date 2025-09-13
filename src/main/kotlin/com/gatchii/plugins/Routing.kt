package com.gatchii.plugins

import com.gatchii.domain.jwk.jwkRoute
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.get

fun Application.configureRouting() {
    install(AutoHeadResponse)

    routing {
        get("/favicon.ico") { call.respondText("") }
        route("/") {
            jwkRoute(get())
        }
    }
}
