package com.gatchii.plugins

import com.gatchii.domains.jwk.jkwRoute
import com.gatchii.domains.main.mainRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            if (cause is AuthorizationException) {
                call.respond(
                    ErrorResponse(
                        message = cause.message,
                        code = "403",
                        status = HttpStatusCode.Forbidden.value
                    )
                )
            } else {
                call.respond(
                    ErrorResponse(
                        message = cause.message,
                        code = "500",
                        status = HttpStatusCode.InternalServerError.value
                    )
                )
            }
        }
        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                ErrorResponse(
                    message = "Not Found",
                    code = "404",
                    status = HttpStatusCode.NotFound.value
                )
            )
        }
        //statusFile(HttpStatusCode.Unauthorized, HttpStatusCode.PaymentRequired, filePattern = "error#.html")
    }
    routing {
        route("") {
            mainRoute()
            jkwRoute()
        }
    }
}

class AuthorizationException(override val message: String?) : Throwable()


@Serializable
data class ErrorResponse(
    val message: String?,
    val status: Int,
    val code: String? = null
)

