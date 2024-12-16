package com.gatchii.domains.jwk

import com.gatchii.shared.common.TaskLeadHandler
import com.gatchii.utils.ECKeyPairHandler
import com.github.f4b6a3.uuid.UuidCreator
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import io.ktor.server.plugins.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jose4j.jwk.PublicJsonWebKey
import shared.common.UnitTest
import java.time.OffsetDateTime
import java.util.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test


/**
 * Package: test.com.gatchii.domains.jwk Created: Devonshin Date:
 * 17/09/2024
 */

@UnitTest
class JwkServiceImplUnitTest {

    private val config = HoconApplicationConfig(
        ConfigFactory.parseString(
            """
                jwk {
                    count = 10
                }
            """
        )
    )
    private val limit = 10
    private val offsetId = UUID.fromString("0292f39f-073f-7526-b011-887aab4b1e0a")
    private val jwkRepository: JwkRepository = mockk<JwkRepository>()
    private val jwkService: JwkService = JwkServiceImpl(jwkRepository, config)

    private val jwkModels: List<JwkModel> = List(limit) {
        JwkModel(
            id = UuidCreator.getTimeOrderedEpochFast(),
            privateKey = "privateKey-$it",
            publicKey = "content-$it",
            createdAt = OffsetDateTime.now(),
            deletedAt = null
        )
    }

    //private val jwksKeySet: JwkResponse = JwkResponse(setOf(emptyMap<String, String>()))

    @BeforeTest
    fun setup() {
        println("setup..")
        mockkObject(ECKeyPairHandler)
        //mockkConstructor(JwkServiceImpl::class)
        //coEvery { anyConstructed<JwkServiceImpl>().getUsableJwks() } coAnswers { jwksKeySet }
        //coEvery { jwkRepository.findAllUsable(null) } coAnswers { jwkModels }
        //coEvery { jwkRepository.findAllUsable(any()) } coAnswers { jwkModels }
    }

    @AfterTest
    fun teardown() {
        //unmockkConstructor(JwkServiceImpl::class)
        unmockkObject(ECKeyPairHandler)
        TaskLeadHandler.cleanTasks()
    }

    @Test
    fun `generate jwks test`() = runTest {
        //given
        val generateJwkCount = 10
        //when
        val generateJwks = jwkService.generateJwks(generateJwkCount)
        //then
        assertThat(generateJwks).hasSize(generateJwkCount)
        return@runTest
    }

    @Test
    fun `generate jwk test`() = runTest {
        //given
        //when
        val generateJwk = jwkService.generateJwk()
        //then
        assertThat(generateJwk).isNotNull()
        val newPublicJwk = PublicJsonWebKey.Factory.newPublicJwk(generateJwk.publicKey)
        assertThat(newPublicJwk.keyId).isNotBlank
        assertThat(newPublicJwk.keyType).isNotBlank
        assertThat(newPublicJwk.use).isNotBlank
        assertThat(newPublicJwk.algorithm).isNotBlank
        return@runTest
    }


    @Test
    fun `list 10 jwk test`() = runTest {
        //given
        coEvery { jwkRepository.findAllUsable(null, any()) } coAnswers { jwkModels }
        //when
        val listJwk = jwkService.findJwks(null, limit)
        //then
        assertThat(listJwk.size).isEqualTo(limit)
        coVerify(exactly = 1) { jwkRepository.findAllUsable(null, any()) }
    }

    @Test
    fun `get jwk test`() = runTest {
        //given
        val jwkModel = jwkModels[0]
        val id = jwkModel.id!!
        coEvery { jwkRepository.read(id) } coAnswers { jwkModel }
        //when
        val jwk = jwkService.getJwk(id)
        //then
        assertThat(jwk).isNotNull()
        coVerify(exactly = 1) { jwkRepository.read(id) }
    }

    @Test
    fun `throw NotFoundException when get 1 not exist jwk test`() = runTest {
        //given
        val id = UuidCreator.getTimeOrderedEpochFast()
        coEvery { jwkRepository.read(id) }.throws(NotFoundException())
        //when
        var thrown = false
        try {
            jwkService.getJwk(id)
        } catch (e: NotFoundException) {
            thrown = true
        }
        //then
        assertThat(thrown).isTrue()
        coVerify(exactly = 1) { jwkRepository.read(id) }
    }

    @Test
    fun `findJwk test`() = runTest {
        //given
        val jwkModel = jwkModels[0]
        val id = jwkModel.id!!
        coEvery { jwkRepository.read(id) } coAnswers { jwkModel }
        //when
        val findJwk = jwkService.findJwk(id)
        //then
        assertThat(findJwk).isNotNull()
        coVerify(exactly = 1) { jwkRepository.read(id) }
    }

    @Test
    fun `findJwk if not exist null return test`() = runTest {
        //given
        val id = UuidCreator.getTimeOrderedEpochFast()
        coEvery { jwkRepository.read(id) } coAnswers { null }
        //when
        val findJwk = jwkService.findJwk(id)
        //then
        assertThat(findJwk).isNull()
        coVerify(exactly = 1) { jwkRepository.read(id) }
    }

    @Test
    fun `delete 1 Jwk by id test`() = runTest {
        //given
        val id = UuidCreator.getTimeOrderedEpochFast()
        coEvery { jwkRepository.delete(id) } answers {}
        coEvery {
            jwkRepository.findAllUsable(offsetId, limit)
        } answers {
            jwkModels.subList(0, 9)
        }
        //when
        jwkService.deleteJwk(id)
        //then
        val findAllJwkForPaging = jwkService.findJwks(offsetId, limit)
        assertThat(findAllJwkForPaging.size).isEqualTo(limit - 1)

        coVerify(exactly = 1) { jwkRepository.delete(id) }
        coVerify(exactly = 1) { jwkRepository.findAllUsable(offsetId, limit) }
    }


    @Test
    fun `delete 1 Jwk by domain test`() = runTest {
        //given
        coEvery { jwkRepository.delete(jwkModels[0]) } answers {}
        coEvery { jwkRepository.findAllUsable(offsetId, limit) } answers {
            jwkModels.subList(0, 9)
        }
        //when
        jwkService.deleteJwk(jwkModels[0])
        //then
        val findAllJwkForPaging = jwkService.findJwks(offsetId, limit)
        assertThat(findAllJwkForPaging.size).isEqualTo(limit - 1)

        coVerify(exactly = 1) { jwkRepository.delete(jwkModels[0]) }
        coVerify(exactly = 1) { jwkRepository.findAllUsable(offsetId, limit) }
    }


}
