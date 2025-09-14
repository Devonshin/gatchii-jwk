package com.gatchii.domain.jwk

/**
 * @author Devonshin
 * @date 2025-09-14
 */

import com.gatchii.config.GlobalConfig
import com.auth0.jwt.interfaces.ECDSAKeyProvider
import java.security.KeyPairGenerator
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject

class JwkServiceImplKeyTest {

    @Test
    fun `Should build ECKey from provider`() = kotlinx.coroutines.test.runTest {
        // GlobalConfig를 test로 고정하여 init 블록이 initializeJwk()를 호출하지 않게 함
        mockkObject(GlobalConfig.Companion)
        every { GlobalConfig.getConfigedValue("ktor.environment") } returns "test"

        // given
        val repo = mockk<JwkRepository>(relaxed = true)
        val service = JwkServiceImpl(repo) { task ->
            // no-op TaskLeadHandler 대체
            mockk(relaxed = true)
        }

        val kpg = KeyPairGenerator.getInstance("EC")
        kpg.initialize(256) // P-256
        val kp = kpg.generateKeyPair()
        val pub = kp.public as ECPublicKey
        val pri = kp.private as ECPrivateKey
        val kid = "test-kid"

        val provider = object : ECDSAKeyProvider {
            override fun getPrivateKey(): ECPrivateKey = pri
            override fun getPublicKeyById(keyId: String?): ECPublicKey = pub
            override fun getPrivateKeyId(): String = kid
        }

        // when
        val ecKey = service.getJwkECKey(provider)

        // then
        assertNotNull(ecKey)
        assertEquals(kid, ecKey.keyID)
        assertEquals("ES256", ecKey.algorithm.name)

        unmockkObject(GlobalConfig.Companion)
    }
}