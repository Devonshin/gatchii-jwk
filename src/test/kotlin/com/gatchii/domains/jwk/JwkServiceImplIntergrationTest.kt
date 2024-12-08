package com.gatchii.domains.jwk

import com.gatchii.shared.repository.DatabaseFactoryForTest
import shared.common.IntegrationTest


/**
 * Package: test.com.gatchii.domains.jwk
 * Created: Devonshin
 * Date: 17/09/2024
 */

@IntegrationTest
class JwkServiceImplIntergrationTest {

    private lateinit var databaseFactory: DatabaseFactoryForTest
    private val jwkRepository: JwkRepository = JwkRepositoryImpl(JwkTable)

}