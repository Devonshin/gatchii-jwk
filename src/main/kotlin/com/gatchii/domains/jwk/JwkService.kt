package com.gatchii.domains.jwk

import java.util.*

/**
 * Package: com.gatchii.domains.jwk
 * Created: Devonshin
 * Date: 16/09/2024
 */

interface JwkService {
    suspend fun generateJwks(number: Int = 1): List<JwkModel>
    suspend fun findAllJwkForPaging(offsetId: UUID?, limit: Int = 10): List<JwkModel>
    suspend fun getJwk(id: UUID): JwkModel
    suspend fun findJwk(id: UUID): JwkModel?
    suspend fun deleteJwk(id: UUID)
    suspend fun deleteJwk(domain: JwkModel)
    suspend fun getUsableJwks(): List<JwkModel>
    fun getNextJwk(): JwkModel
    suspend fun saveJwks(jwks: List<JwkModel>): List<JwkModel>
    suspend fun saveJwk(jwk: JwkModel): JwkModel
}