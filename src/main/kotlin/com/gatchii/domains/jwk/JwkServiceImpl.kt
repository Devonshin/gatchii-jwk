package com.gatchii.domains.jwk


import com.gatchii.shared.common.OnetimeTaskLeadHandler
import com.gatchii.shared.common.TaskLeadHandler
import com.gatchii.utils.ECKeyPairHandler
import com.github.f4b6a3.uuid.UuidCreator
import io.ktor.server.plugins.*
import io.ktor.util.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jwk.PublicJsonWebKey
import org.jose4j.jwk.Use
import java.time.OffsetDateTime
import java.util.*

/**
 * Package: com.gatchii.domains.jwk
 * Created: Devonshin
 * Date: 16/09/2024
 */

class JwkServiceImpl(
    private val jwkRepository: JwkRepository,
) : JwkService {

    private val logger = KtorSimpleLogger("com.gatchii.domains.jwk.JwkServiceImpl")

    companion object {
        private val preparedJwks = mutableListOf<JwkModel>()
        private var idx = 0
        private const val JWK_COUNT = 20
    }

    init {
        logger.info("JwkServiceImpl init. ")
        TaskLeadHandler.addTasks(OnetimeTaskLeadHandler("jwk") {
            runBlocking {
                val usableJwks = getUsableJwks()
                for (usableJwk in usableJwks) {
                    preparedJwks.add(usableJwk)
                }
                if (usableJwks.size < JWK_COUNT) {
                    for (generatedJwk in generateJwks(JWK_COUNT - usableJwks.size)) {
                        val saveJwk = saveJwk(generatedJwk)
                        preparedJwks.add(saveJwk)
                    }
                }
                logger.debug("generate usableJwks end = {}", usableJwks.size)
            }
        })
    }

    override fun getNextJwk(): JwkModel {
        if (idx >= JWK_COUNT) {
            idx = 0
        }
        return preparedJwks[idx++]
    }

    override suspend fun generateJwks(number: Int): List<JwkModel> {
        logger.info("generateJwk $number")
        val newJwks = mutableListOf<JwkModel>()
        val now = OffsetDateTime.now()
        repeat(number) {
            val keyPair = ECKeyPairHandler.generateKeyPair()
            val newPublicJwk = PublicJsonWebKey.Factory.newPublicJwk(keyPair.public)
            newPublicJwk.privateKey = keyPair.private;
            newPublicJwk.use = Use.SIGNATURE;
            newPublicJwk.algorithm = ECKeyPairHandler.KEY_ALGORITHM
            newJwks.add(
                JwkModel(
                    keyId = UuidCreator.getTimeOrderedEpoch().toString(),
                    privateKey = keyPair.private.encoded.encodeBase64(),
                    content = newPublicJwk.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY),
                    createdAt = now
                )
            )
        }
        return newJwks
    }

    override suspend fun saveJwk(jwk: JwkModel): JwkModel {
        return jwkRepository.create(jwk)
    }

    override suspend fun saveJwks(jwks: List<JwkModel>): List<JwkModel> {
        return jwkRepository.batchCreate(jwks)
    }

    override suspend fun findAllJwkForPaging(offsetId: UUID?, limit: Int): List<JwkModel> {
        return jwkRepository.findAllUsable(offsetId, limit)
    }

    override suspend fun getJwk(id: UUID): JwkModel {
        return jwkRepository.read(id) ?: throw NotFoundException()
    }

    override suspend fun findJwk(id: UUID): JwkModel? {
        return jwkRepository.read(id)
    }

    override suspend fun deleteJwk(id: UUID) {
        jwkRepository.delete(id)
    }

    override suspend fun deleteJwk(domain: JwkModel) {
        jwkRepository.delete(domain)
    }

    override suspend fun getUsableJwks(): List<JwkModel> {
        return jwkRepository.findAllUsable(null)
    }


}