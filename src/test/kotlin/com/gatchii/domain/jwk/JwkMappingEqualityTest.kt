package com.gatchii.domain.jwk

/**
 * @author Devonshin
 * @date 2025-09-14
 */

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import java.time.OffsetDateTime
import java.util.UUID

class JwkMappingEqualityTest {

    @Test
    fun `Should consider two models equal when ids are same`() {
        val id = UUID.randomUUID()
        val a = JwkModel(privateKey = "p1", publicKey = "u1", createdAt = OffsetDateTime.now(), id = id)
        val b = JwkModel(privateKey = "p2", publicKey = "u2", createdAt = OffsetDateTime.now(), id = id)
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun `Should consider two models not equal when ids differ`() {
        val a = JwkModel(privateKey = "p1", publicKey = "u1", createdAt = OffsetDateTime.now(), id = UUID.randomUUID())
        val b = JwkModel(privateKey = "p1", publicKey = "u1", createdAt = OffsetDateTime.now(), id = UUID.randomUUID())
        assertNotEquals(a, b)
    }
}