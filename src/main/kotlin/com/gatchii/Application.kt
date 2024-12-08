package com.gatchii

import com.gatchii.plugins.configureDatabases
import com.gatchii.plugins.configureFrameworks
import com.gatchii.plugins.configureRouting
import com.gatchii.plugins.configureSecurity
import com.gatchii.shared.common.TaskLeadHandler
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun onApplicationLoaded() {
    TaskLeadHandler.runTasks()
}

fun Application.module() {
    configureDatabases()
    configureFrameworks()
    configureSecurity()
    //    configureHTTP()
    //    configureMonitoring()
    //    configureSerialization()
    configureRouting()
    onApplicationLoaded()
}
