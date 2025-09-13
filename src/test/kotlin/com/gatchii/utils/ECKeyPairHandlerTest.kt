package com.gatchii.utils

import com.gatchii.common.utils.ECKeyPairHandler
import com.gatchii.domain.jwk.JwkModel
import com.github.f4b6a3.uuid.UuidCreator
import io.ktor.util.encodeBase64
import org.assertj.core.api.Assertions
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jwk.PublicJsonWebKey
import org.jose4j.jwk.Use
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Security
import java.time.OffsetDateTime
import kotlin.jvm.java

/**
 * Package: com.gatchii.unit.utils
 * Created: Devonshin
 * Date: 03/11/2024
 */

@UnitTest
class ECKeyPairHandlerTest {
    companion object {
        init {
            // Add Bouncy Castle Provider for tests
            Security.addProvider(BouncyCastleProvider())
        }
    }

    @Test
    fun `ECDSA enerateKeyPair test`() {
        val keyPair: KeyPair = ECKeyPairHandler.generateKeyPair()
        assertNotNull(keyPair.private)
        assertNotNull(keyPair.public)
    }

    @Test
    fun `ECDSA signAndVerify test`() {
        val message = "This is a test message"
        val keyPair: KeyPair = ECKeyPairHandler.generateKeyPair()

        val signature = ECKeyPairHandler.sign(message, keyPair.private)
        assertNotNull(signature)

        val isVerified = ECKeyPairHandler.verify(message, keyPair.public, signature)
        assertTrue(isVerified, "Signature should be verified.")
    }

    @Test
    fun `ECDSA InvalidSignature test`() {
        val message = "This is a test message"
        val otherMessage = "This is a different message"
        val keyPair: KeyPair = ECKeyPairHandler.generateKeyPair()

        val signature = ECKeyPairHandler.sign(message, keyPair.private)

        val isVerified = ECKeyPairHandler.verify(otherMessage, keyPair.public, signature)
        assertFalse(isVerified, "Signature should not be verified.")
    }

    @Test
    fun `test if convertPrivateKey generates PrivateKey from PEM format`() {
        // given
        val keyPair: KeyPair = ECKeyPairHandler.generateKeyPair()
        val privateKeyPem = keyPair.private.encoded.encodeBase64()

        // when
        val privateKey: PrivateKey = ECKeyPairHandler.convertPrivateKey(privateKeyPem)

        //then
        Assertions.assertThat(privateKey)
            .isEqualTo(keyPair.private)
            .withFailMessage("PrivateKey should not be null for valid PEM format")
    }

    @Test
    fun `test if convertPrivateKey throws exception for invalid PEM format`() {
        // given
        val invalidPemString = "This is an invalid PEM format"
        // when - then
        assertThrows(IllegalArgumentException::class.java) {
            ECKeyPairHandler.convertPrivateKey(invalidPemString)
        }
    }


    @Test
    fun `test if convertPublicKey generates PublicKey from PEM format`() {
        // given
        val keyPair: KeyPair = ECKeyPairHandler.generateKeyPair()
        val publicKeyPem = keyPair.public.encoded.encodeBase64()

        // when
        val privateKey: PublicKey = ECKeyPairHandler.convertPublicKey(publicKeyPem)

        //then
        Assertions.assertThat(privateKey)
            .isEqualTo(keyPair.public)
            .withFailMessage("PublicKey should not be null for valid PEM format")
    }

    @Test
    fun `test if convertPublicKey throws exception for invalid PEM format`() {
        // given
        val invalidPemString = "This is an invalid PEM format"

        // when - then
        assertThrows(IllegalArgumentException::class.java) {
            ECKeyPairHandler.convertPublicKey(invalidPemString)
        }
    }

    @Test
    fun `test ecdsa format`() {
        val keyPair = ECKeyPairHandler.generateKeyPair()
        val newJwk = PublicJsonWebKey.Factory.newPublicJwk(keyPair.public)
        newJwk.privateKey = keyPair.private;
        newJwk.use = Use.SIGNATURE;
        newJwk.algorithm = ECKeyPairHandler.SIGN_ALGORITHM
        newJwk.keyId = UuidCreator.getTimeOrderedEpoch().toString()
        val jwkModel = JwkModel(
            privateKey = keyPair.private.encoded.encodeBase64(),
            publicKey = newJwk.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY),
            createdAt = OffsetDateTime.now()
        )

        println(jwkModel)
    }

}