package com.gatchii.domains.jwk

import com.gatchii.shared.common.OnetimeTaskLeadHandler
import com.gatchii.shared.common.TaskLeadHandler
import com.gatchii.utils.ECKeyPairHandler
import com.github.f4b6a3.uuid.UuidCreator
import io.ktor.server.config.*
import io.ktor.server.plugins.*
import io.ktor.util.*
import io.ktor.util.logging.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jwk.PublicJsonWebKey.Factory.newPublicJwk
import org.jose4j.jwk.Use
import java.time.OffsetDateTime
import java.util.*

/** Package: com.gatchii.domains.jwk Created: Devonshin Date: 16/09/2024 */

class JwkServiceImpl(
    private val jwkRepository: JwkRepository,
    jwkConfig: ApplicationConfig,
) : JwkService {

    private val logger = KtorSimpleLogger("com.gatchii.domains.jwk.JwkServiceImpl")
    private val jwkCount = jwkConfig.propertyOrNull("count")?.getString()?.toInt() ?: 20

    companion object {
        private val preparedJwks = mutableListOf<JwkModel>()
        private var idx = 0
    }

    init {
        logger.info("JwkServiceImpl init. ")
        TaskLeadHandler.addTasks(OnetimeTaskLeadHandler("jwk") {
            runBlocking {
                val usableJwks = jwkRepository.findAllUsable(null, jwkCount)
                for (usableJwk in usableJwks) {
                    preparedJwks.add(usableJwk)
                }
                if (usableJwks.size < jwkCount) {
                    for (generatedJwk in generateJwks(jwkCount - usableJwks.size)) {
                        val saveJwk = saveJwk(generatedJwk)
                        preparedJwks.add(saveJwk)
                    }
                }
                logger.debug("Prepared usable Jwks count = {}", preparedJwks.size)
            }
        })
    }

    override fun getNextJwk(): JwkModel {
        if (idx >= jwkCount) {
            idx = 0
        }
        return preparedJwks[idx++]
    }

    override suspend fun generateJwks(number: Int): List<JwkModel> {
        logger.debug("generateJwk $number")
        val newJwks = mutableListOf<JwkModel>()
        repeat(number) {
            newJwks.add(generateJwk())
        }
        return newJwks
    }

    override suspend fun generateJwk(): JwkModel {
        val now = OffsetDateTime.now()
        val keyPair = ECKeyPairHandler.generateKeyPair()
        val newPublicJwk = newPublicJwk(keyPair.public)
        newPublicJwk.privateKey = keyPair.private
        newPublicJwk.use = Use.SIGNATURE
        newPublicJwk.algorithm = ECKeyPairHandler.KEY_ALGORITHM
        newPublicJwk.keyId = UuidCreator.getTimeOrderedEpoch().toString()
        return JwkModel(
            id = UUID.fromString(newPublicJwk.keyId),
            privateKey = keyPair.private.encoded.encodeBase64(),
            publicKey = newPublicJwk.toJson(JsonWebKey.OutputControlLevel.PUBLIC_ONLY),
            createdAt = now
        )
    }

    //todo 캐싱
    override suspend fun saveJwk(jwk: JwkModel): JwkModel {
        return jwkRepository.create(jwk)
    }

    //todo 캐싱
    override suspend fun saveJwks(jwks: List<JwkModel>): List<JwkModel> {
        return jwkRepository.batchCreate(jwks)
    }

    //todo 캐싱
    override suspend fun findJwks(offsetId: UUID?, limit: Int): List<JwkModel> {
        return jwkRepository.findAllUsable(offsetId, limit)
    }

    //todo 캐싱
    override suspend fun getJwk(id: UUID): JwkModel {
        return jwkRepository.read(id) ?: throw NotFoundException()
    }

    //todo 캐싱
    override suspend fun findJwk(id: UUID): JwkModel? {
        return jwkRepository.read(id)
    }

    //todo 캐싱
    override suspend fun deleteJwk(id: UUID) {
        jwkRepository.delete(id)
    }

    //todo 캐싱
    override suspend fun deleteJwk(domain: JwkModel) {
        jwkRepository.delete(domain)
    }

    //todo 캐싱
    override suspend fun getUsableJwks(): JwkResponse {
        val list = mutableSetOf<Map<String, String>>()
        preparedJwks
            .forEach {
                list.add(Json.decodeFromString<Map<String, String>>(it.publicKey))
            }
        return JwkResponse(list)
    }


}