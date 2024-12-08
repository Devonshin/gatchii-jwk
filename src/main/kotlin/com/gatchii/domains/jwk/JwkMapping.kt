package com.gatchii.domains.jwk

import com.gatchii.shared.model.BaseModel
import com.gatchii.shared.repository.UUID7Table
import com.gatchii.shared.serializer.OffsetDateTimeSerializer
import com.gatchii.shared.serializer.UUIDSerializer
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.javatime.timestampWithTimeZone
import java.time.OffsetDateTime
import java.util.*

/**
 * Package: com.gatchii.domains.jwk
 * Created: Devonshin
 * Date: 16/09/2024
 */

object JwkTable : UUID7Table(
    name = "jwks",
) {
    val keyId = varchar("key_id", length = 255)
    val privateKey = varchar("private_key", length = 255)
    val content = text("content")
    val createdAt = timestampWithTimeZone("created_at").clientDefault { OffsetDateTime.now() }
    val deletedAt = timestampWithTimeZone("deleted_at").nullable()
}

@Serializable
data class JwkModel(
    val privateKey: String,
    val keyId: String,
    val content: String,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val createdAt: OffsetDateTime? = null,
    @Serializable(with = OffsetDateTimeSerializer::class)
    val deletedAt: OffsetDateTime? = null,
    @Serializable(with = UUIDSerializer::class)
    override var id: UUID? = null,
) : BaseModel<UUID>
