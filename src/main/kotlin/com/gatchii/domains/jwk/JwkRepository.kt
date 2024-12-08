package com.gatchii.domains.jwk

import com.gatchii.domains.jwk.JwkTable.content
import com.gatchii.domains.jwk.JwkTable.createdAt
import com.gatchii.domains.jwk.JwkTable.deletedAt
import com.gatchii.domains.jwk.JwkTable.id
import com.gatchii.domains.jwk.JwkTable.keyId
import com.gatchii.domains.jwk.JwkTable.privateKey
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
            it[this.keyId] = domain.keyId
            it[this.content] = domain.content
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
            this[keyId] = it.keyId
            this[content] = it.content
            if (it.createdAt != null) {
                this[createdAt] = it.createdAt
            }
            this[deletedAt] = it.deletedAt
        }

    override fun toDomain(row: ResultRow): JwkModel {
        return JwkModel(
            id = row[id].value,
            keyId = row[keyId],
            privateKey = row[privateKey],
            content = row[content],
            createdAt = row[createdAt],
            deletedAt = row[deletedAt]
        )
    }

    override fun updateRow(domain: JwkModel): JwkTable.(UpdateStatement) -> Unit = {
        domain.deletedAt?.let { throw NotSupportMethodException("JwkModel can't be update. $domain") }
        it[privateKey] = domain.privateKey
        it[content] = domain.content
        it[keyId] = domain.keyId
    }
}