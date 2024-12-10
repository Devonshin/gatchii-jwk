package com.gatchii.domains.jwk

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/** Package: com.gatchii.domains.jwk Created: Devonshin Date: 23/09/2024 */

fun Route.jkwRoute() {

    val jwkService: JwkService by inject<JwkService>()
    get("/.well-known/jwks.json") {
        call.respond(HttpStatusCode.OK, jwkService.getUsableJwks())
    }

}