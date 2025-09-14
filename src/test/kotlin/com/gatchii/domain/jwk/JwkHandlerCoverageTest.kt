package com.gatchii.domain.jwk

/**
 * @author Devonshin
 * @date 2025-09-14
 */

import com.gatchii.common.exception.InvalidUsableJwkStatusException
import com.gatchii.common.utils.DateUtil
import com.typesafe.config.ConfigFactory
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import java.time.OffsetDateTime
import java.util.UUID

class JwkHandlerCoverageTest {

    @Test
    fun `Should return empty when getRandomActiveJwk on empty active list`() {
        // given
        JwkHandler.setConfig(ConfigFactory.parseString("maxCapacity=2"))
        JwkHandler.clearAll()

        // when
        val opt = JwkHandler.getRandomActiveJwk()

        // then
        assertTrue(opt.isEmpty)
        // reset config
        JwkHandler.setConfig(ConfigFactory.load("application-test.conf").getConfig("jwk"))
    }

    @Test
    fun `Should move old inactive jwks to discard when expired`() {
        // given
        JwkHandler.setConfig(ConfigFactory.parseString("maxCapacity=2,expireTimeSec=1"))
        JwkHandler.clearAll()

        val oldCreatedAt = OffsetDateTime.now().minusSeconds(10)
        val inactive = JwkModel(
            privateKey = "priv",
            publicKey = "pub",
            status = JwkStatus.INACTIVE,
            createdAt = oldCreatedAt,
            id = UUID.randomUUID()
        )
        JwkHandler.addInactiveJwk(inactive)

        // 현재 시간을 모킹하여 만료 처리 유도
        mockkObject(DateUtil)
        every { DateUtil.getCurrentDate() } returns OffsetDateTime.now()

        // when
        val toDelete = JwkHandler.getRemovalJwks()

        // then
        assertEquals(1, toDelete.size)
        assertTrue(JwkHandler.getDiscardJwks().any { it.id == inactive.id && it.status == JwkStatus.DELETED })
        assertTrue(JwkHandler.getInactiveJwks().none { it.id == inactive.id })

        // cleanup
        unmockkObject(DateUtil)
        JwkHandler.setConfig(ConfigFactory.load("application-test.conf").getConfig("jwk"))
    }

    @Test
    fun `Should throw when addJwk called with invalid status`() {
        // given
        JwkHandler.setConfig(ConfigFactory.parseString("maxCapacity=2"))
        JwkHandler.clearAll()

        val deleted = JwkModel(
            privateKey = "priv",
            publicKey = "pub",
            status = JwkStatus.DELETED,
            createdAt = OffsetDateTime.now(),
            id = UUID.randomUUID()
        )

        // expect
        assertFailsWith<InvalidUsableJwkStatusException> {
            JwkHandler.addJwk(deleted)
        }
        // reset
        JwkHandler.setConfig(ConfigFactory.load("application-test.conf").getConfig("jwk"))
    }
}