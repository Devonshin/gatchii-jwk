package com.gatchii.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.logging.*
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

private val logger: Logger = KtorSimpleLogger("com.gatchii.plugins.Security")

fun Application.configureSecurity() {

    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
        Security.addProvider(BouncyCastleProvider())
    }
    // Please read the jwt property from the config file if you are using EngineMain
    val env = environment.config

    install(Authentication) {
    }
    println("Security installed")
}