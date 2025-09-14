package com.gatchii.domain.jwk

/**
 * @author Devonshin
 * @date 2025-09-14
 */

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlin.test.*
import org.junit.jupiter.api.DisplayName
import shared.common.IntegrationTest
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.ECDSAKeyProvider
import kotlinx.serialization.json.Json

@IntegrationTest
@DisplayName("JwkRoute E2E")
class JwkRouteTest {

    @Test
    @DisplayName("Should return empty keys when no active jwks")
    fun should_return_empty_keys_when_no_active_jwks() = testApplication {
        application {
            routing { jwkRoute(EmptyJwkService()) }
        }
        val res = client.get("/.well-known/jwks.json")
        assertEquals(HttpStatusCode.OK, res.status)
        val body = res.bodyAsText()
        val dto = Json { ignoreUnknownKeys = true }.decodeFromString(JwkResponse.serializer(), body)
        assertTrue(dto.keys.isEmpty())
    }

    @Test
    @DisplayName("Should return 200 and JSON when GET /.well-known/jwks.json")
    fun should_return_200_and_json_when_get_well_known_jwks() = testApplication {
        application {
            routing {
                // 테스트에서는 DI를 우회하기 위해 Fake 서비스를 직접 주입합니다.
                jwkRoute(FakeJwkService())
            }
        }

        val res = client.get("/.well-known/jwks.json")
        assertEquals(HttpStatusCode.OK, res.status, "HTTP 200을 기대합니다")

        val ct = res.headers[HttpHeaders.ContentType] ?: ""
        assertTrue(ct.lowercase().startsWith("application/json"), "Content-Type은 application/json 이어야 합니다")

        val body = res.bodyAsText()
        assertTrue(body.isNotBlank(), "본문은 비어있지 않아야 합니다")

        // DTO 디코딩 및 스키마 검증: keys 배열이 존재하며 최소 1개 항목을 포함하고 표준 필드를 가진다
        val json = Json { ignoreUnknownKeys = true }
        val dto = json.decodeFromString(JwkResponse.serializer(), body)
        assertNotNull(dto, "DTO 디코딩이 성공해야 합니다")
        assertTrue(dto.keys.isNotEmpty(), "keys 배열은 비어있지 않아야 합니다")
        val first = dto.keys.first()
        // 표준 JWKS 필드 존재 여부(최소 한 개) 확인
        assertTrue(first.containsKey("kty"), "kty 필드가 있어야 합니다")
    }
}

private class EmptyJwkService : JwkService {
    override suspend fun initializeJwk() {}
    override suspend fun getRandomJwk(): JwkModel = throw NotImplementedError("not needed")
    override suspend fun findJwk(id: java.util.UUID): JwkModel = throw NotImplementedError("not needed")
    override suspend fun convertAlgorithm(provider: ECDSAKeyProvider): Algorithm = throw NotImplementedError("not needed")
    override suspend fun getProvider(jwk: JwkModel): ECDSAKeyProvider = throw NotImplementedError("not needed")
    override suspend fun findAllJwk(): List<Map<String, String>> = emptyList()
    override suspend fun deleteJwk(id: java.util.UUID) {}
    override suspend fun deleteJwks(jwks: List<JwkModel>) {}
    override suspend fun createJwk(): JwkModel = throw NotImplementedError("not needed")
    override suspend fun taskProcessing() {}
    override suspend fun createJwks(size: Int): List<JwkModel> = emptyList()
    override suspend fun findAllUsableJwk(): List<JwkModel> = emptyList()
}

private class FakeJwkService : JwkService {
    override suspend fun initializeJwk() { /* not needed */ }
    override suspend fun getRandomJwk(): JwkModel = throw NotImplementedError("not needed")
    override suspend fun findJwk(id: java.util.UUID): JwkModel = throw NotImplementedError("not needed")
    override suspend fun convertAlgorithm(provider: ECDSAKeyProvider): Algorithm = throw NotImplementedError("not needed")
    override suspend fun getProvider(jwk: JwkModel): ECDSAKeyProvider = throw NotImplementedError("not needed")
    override suspend fun findAllJwk(): List<Map<String, String>> = listOf(
        mapOf(
            "kty" to "EC",
            "kid" to "test-key",
            "use" to "sig",
            "alg" to "ES256"
        )
    )
    override suspend fun deleteJwk(id: java.util.UUID) { /* not needed */ }
    override suspend fun deleteJwks(jwks: List<JwkModel>) { /* not needed */ }
    override suspend fun createJwk(): JwkModel = throw NotImplementedError("not needed")
    override suspend fun taskProcessing() { /* not needed */ }
    override suspend fun createJwks(size: Int): List<JwkModel> = throw NotImplementedError("not needed")
    override suspend fun findAllUsableJwk(): List<JwkModel> = throw NotImplementedError("not needed")
}