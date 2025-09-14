package com.gatchii.plugins

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
import kotlinx.serialization.json.Json

@IntegrationTest
@DisplayName("StatusPages E2E")
class StatusPagesE2ETest {

    @Test
    @DisplayName("Should return 404 ErrorResponse for unknown route")
    fun should_return_404_for_unknown_route() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing { /* intentionally empty */ }
        }
        val res = client.get("/not-exists")
        assertEquals(HttpStatusCode.NotFound, res.status)
        val body = res.bodyAsText()
        val dto = Json { ignoreUnknownKeys = true }.decodeFromString(ErrorResponse.serializer(), body)
        assertEquals(HttpStatusCode.NotFound.value, dto.code)
        assertTrue(dto.path.contains("/not-exists"))
    }

    @Test
    @DisplayName("Should map exception to 500 ErrorResponse")
    fun should_map_exception_to_500_error_response() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            routing {
                get("/boom") { throw RuntimeException("boom") }
            }
        }
        val res = client.get("/boom")
        assertEquals(HttpStatusCode.InternalServerError, res.status)
        val body = res.bodyAsText()
        val dto = Json { ignoreUnknownKeys = true }.decodeFromString(ErrorResponse.serializer(), body)
        assertEquals(HttpStatusCode.InternalServerError.value, dto.code)
        assertTrue(dto.path.contains("/boom"))
    }
}