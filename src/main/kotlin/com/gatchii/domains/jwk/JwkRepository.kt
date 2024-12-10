package com.gatchii.domains.jwk

import com.gatchii.domains.jwk.JwkTable.createdAt
import com.gatchii.domains.jwk.JwkTable.deletedAt
import com.gatchii.domains.jwk.JwkTable.id
import com.gatchii.domains.jwk.JwkTable.privateKey
import com.gatchii.domains.jwk.JwkTable.publicKey
import com.gatchii.shared.exception.NotSupportMethodException
import com.gatchii.shared.repository.ExposedCrudRepository
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import java.util.*

/**
 * Package: com.gatchii.domains.jwk
 * Created: Devonshin
 * Date: 16/09/2024
 */

interface JwkRepository: ExposedCrudRepository<JwkTable, JwkModel, UUID> {

    fun findAllUsable(offsetId: UUID?, limit: Int = 10): List<JwkModel>

    override fun toRow(domain: JwkModel): JwkTable.(InsertStatement<EntityID<UUID>>) -> Unit =
        {
            if (domain.id != null) it[id] = domain.id!!
            it[this.privateKey] = domain.privateKey
            it[this.publicKey] = domain.publicKey
            if (domain.createdAt != null) {
                it[this.createdAt] = domain.createdAt
            }
            it[this.deletedAt] = domain.deletedAt?.let {
                domain.deletedAt
            }
        }

    override fun toBatchRow(): BatchInsertStatement.(JwkModel) -> Unit =
        {
            if (it.id != null) this[id] = it.id!!
            this[privateKey] = it.privateKey
            this[publicKey] = it.publicKey
            if (it.createdAt != null) {
                this[createdAt] = it.createdAt
            }
            this[deletedAt] = it.deletedAt
        }

    override fun toDomain(row: ResultRow): JwkModel {
        return JwkModel(
            id = row[id].value,
            privateKey = row[privateKey],
            publicKey = row[publicKey],
            createdAt = row[createdAt],
            deletedAt = row[deletedAt]
        )
    }

    override fun updateRow(domain: JwkModel): JwkTable.(UpdateStatement) -> Unit = {
        domain.deletedAt?.let { throw NotSupportMethodException("JwkModel can't be update. $domain") }
        it[privateKey] = domain.privateKey
        it[publicKey] = domain.publicKey
    }
}