package com.gatchii.domains.jwk

import com.gatchii.shared.repository.DatabaseFactoryForTest
import shared.repository.dummyJwkQueryList
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import shared.common.UnitTest
import java.time.OffsetDateTime
import java.util.*

/**
 * Package: com.gatchii.domains.jwk
 * Created: Devonshin
 * Date: 17/09/2024
 */
@UnitTest
class JwkRepositoryImplUnitTest {

    companion object {
        private val databaseFactory: DatabaseFactoryForTest = DatabaseFactoryForTest()
        private lateinit var jwkRepository: JwkRepository
        private var jwkCount = 10 // dummyJwkData count is 10

        @BeforeAll
        @JvmStatic
        internal fun init() {
            println("init..")
            databaseFactory.connect()
            jwkRepository = JwkRepositoryImpl(JwkTable)

            transaction {
                println("transaction..")
                SchemaUtils.drop(JwkTable)
                SchemaUtils.create(JwkTable)
                SchemaUtils.createMissingTablesAndColumns(JwkTable)
                addLogger(StdOutSqlLogger)
                execInBatch(dummyJwkQueryList)
            }
        }

        @AfterAll
        @JvmStatic
        internal  fun destroy() {
            databaseFactory.close()
        }
    }

    @Test
    fun `jwk create test`() = runTest {
        println("jwk create test..")
        //given
        val randomUUID = UUID.randomUUID()
        val jwkModel = JwkModel(
            privateKey = "ac-$randomUUID",
            keyId = "keyId-$randomUUID",
            content = "content-$randomUUID",
            createdAt = OffsetDateTime.now()
        )
        //when
        val created = jwkRepository.create(jwkModel)
        val read = jwkRepository.read(created.id!!)
        jwkCount++

        //then
        assertThat(created.id != null)
        assertThat(created.id == read?.id)
    }

    @Test
    fun `jwk batch create test`() = runTest {
        //given
        val list = List(10) {
            val randomUUID = UUID.randomUUID()
            jwkCount++
            JwkModel(
                privateKey = "ac-$randomUUID",
                keyId = "keyId-$randomUUID",
                content = "content-$randomUUID",
                createdAt = OffsetDateTime.now()
            )
        }
        //when
        val created = jwkRepository.batchCreate(list)
        //then
        assertThat(created.size).isEqualTo(list.size)
    }

    @Test
    fun `jkw read test`() = runTest {
        //given
        val id = UUID.fromString("0192f49f-073f-7526-b011-887aab4b1e0a")
        //when
        val jwkModel = jwkRepository.read(id)
        //then
        assertThat(jwkModel).isNotNull
        assert(jwkModel?.id == id)
    }

    @Test
    fun `jwk findAll test`() = runTest {
        //given
        //when
        val findAll = jwkRepository.findAll()
        //then
        assertThat(findAll.size).isEqualTo(21)
    }

    @Test
    fun `jwk update test`() = runTest {
        //given
        val randomUUID = UUID.randomUUID()
        val jwkModel = JwkModel(
            id = UUID.fromString("0192f49f-073f-7526-b011-887aab4b1e0a"),
            privateKey = "update-ac-$randomUUID",
            keyId = "update-keyId-$randomUUID",
            content = "update-content-$randomUUID",
            createdAt = OffsetDateTime.now()
        )
        //when
        val oldJwkModel = jwkRepository.read(jwkModel.id!!)
        println("oldJwkModel = $oldJwkModel")
        jwkRepository.update(jwkModel)
        val newJwkModel = jwkRepository.read(jwkModel.id!!)
        //then
        assertThat(newJwkModel?.privateKey).isEqualTo(jwkModel.privateKey)
    }

    @Test
    fun `jwk delete by id test`() = runTest {
        //given
        val id = UUID.fromString("0192f39f-073f-7526-b011-887aab4b1e0a")
        //when
        jwkRepository.delete(id)
        jwkCount--
        //then
        val read = jwkRepository.read(id)
        assertThat(read).isNull()
    }

    @Test
    fun `jwk delete by domain test`() = runTest {
        //given
        val jwkModel = JwkModel(
            id = UUID.fromString("0192f39f-073f-7526-b011-887aab4b1e0a"),
            privateKey = "update ac",
            keyId = "keyId",
            content = "content",
            createdAt = OffsetDateTime.now()
        )
        //when
        jwkRepository.delete(jwkModel)
        jwkCount--
        //then
        val read = jwkRepository.read(jwkModel.id!!)
        assertThat(read).isNull()
    }

    @Test
    @Order(Int.MAX_VALUE)
    fun `jwk findAllUsable limit 10 test`() = runTest {
        //given
        val limit = 10
        //when
        val findAllUsable = jwkRepository.findAllUsable(null, limit)
        //then
        assertThat(findAllUsable.size).isEqualTo(10)
    }

    @Test
    @Order(Int.MAX_VALUE)
    fun `jwk findAllUsable limit 10 with uuid test`() = runTest {
        //given
        val limit = 10
        val offsetId = UUID.fromString("0192f49f-073f-7526-b011-887aab4b1e0a")
        //when
        val findAllUsable = jwkRepository.findAllUsable(offsetId, limit)
        //then
        assertThat(findAllUsable.size).isEqualTo(9)
    }


}