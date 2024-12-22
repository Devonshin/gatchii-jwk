package com.gatchii.shared.repository

import com.gatchii.plugins.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.util.IsolationLevel
import org.jetbrains.exposed.sql.Database
import javax.xml.crypto.Data

/**
 * Package: com.gatchii.shared.repository
 * Created: Devonshin
 * Date: 17/09/2024
 */

class DatabaseFactoryImpl(databaseConfig: DatabaseConfig) : DatabaseFactory {
    private val config: DatabaseConfig = databaseConfig
    private lateinit var database: Database
    override fun connect() {
        database = Database.connect(dataSource())
    }

    override fun close() {
        if (::database.isInitialized) {
            database.connector().close()
        }
    }

    override fun dataSource(): HikariDataSource {
        val hConfig = HikariConfig()
        hConfig.jdbcUrl = config.url
        hConfig.username = config.user
        hConfig.password = config.password
        hConfig.setDriverClassName(config.driverClass)
        hConfig.maximumPoolSize = config.maxPoolSize
        hConfig.isAutoCommit = true
        hConfig.transactionIsolation = IsolationLevel.TRANSACTION_REPEATABLE_READ.name
        hConfig.validate()
        return HikariDataSource(hConfig)
    }
}