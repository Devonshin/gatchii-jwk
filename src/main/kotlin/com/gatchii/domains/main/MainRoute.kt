package com.gatchii.domains.main

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Package: com.gatchii.domains.main
 * Created: Devonshin
 * Date: 10/09/2024
 */

fun Route.mainRoute() {
    get(Regex("/?")) {
        println("Hello, this is main-server ! ")
        call.respond("Hello, this is jwk-server ! ")
    }
}