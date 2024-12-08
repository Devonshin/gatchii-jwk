package com.gatchii.domains.jwk

import io.ktor.server.routing.*

/**
 * Package: com.gatchii.domains.jwk
 * Created: Devonshin
 * Date: 23/09/2024
 */

fun Route.jkwRoute() {
    route(".well-known/jwks.json") {

    }
}