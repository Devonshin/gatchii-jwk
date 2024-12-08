package com.gatchii.plugins

import com.gatchii.domains.jwk.JwkRepository
import com.gatchii.domains.jwk.JwkTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    initData(this)
}

private fun initData(app: Application) {
    val jwkRepository by app.inject<JwkRepository>()
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(
            JwkTable
        )
        SchemaUtils.createMissingTablesAndColumns(JwkTable)
    }
    //    jwkRepository.create()
}
