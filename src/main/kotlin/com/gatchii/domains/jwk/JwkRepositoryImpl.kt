package com.gatchii.domains.jwk

import com.gatchii.domains.jwk.JwkTable.deletedAt
import com.gatchii.domains.jwk.JwkTable.id
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import java.util.*

/**
 * Package: com.gatchii.domains.jwk
 * Created: Devonshin
 * Date: 16/09/2024
 */

class JwkRepositoryImpl(override val table: JwkTable) : JwkRepository {
    override fun findAllUsable(offsetId: UUID?, limit: Int): List<JwkModel> = dbQuery {
        table.selectAll()
            .where { deletedAt.isNull() }
            .apply { if (offsetId != null) andWhere { id less offsetId } }
            .limit(limit)
            .orderBy(id to SortOrder.DESC)
            .map { toDomain(it) }
    }


}