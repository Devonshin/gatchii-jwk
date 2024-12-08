package com.gatchii.domains.login

import com.gatchii.domains.jwk.JwkService
import com.gatchii.domains.jwt.JwtService
import com.gatchii.domains.jwt.RefreshTokenService
import com.gatchii.shared.exception.NotFoundUserException
import com.gatchii.utils.BCryptPasswordEncoder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import shared.common.UnitTest
import java.time.OffsetDateTime
import java.util.*

/**
 * Package: com.gatchii.domains.login
 * Created: Devonshin
 * Date: 24/09/2024
 */

@UnitTest
class LoginServiceImplUnitTest {

    private lateinit var loginService: LoginServiceImpl
    private lateinit var loginRepository: LoginRepository
    private lateinit var jwkService: JwkService
    private lateinit var jwtService: JwtService
    private lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder
    private lateinit var refreshTockenService: RefreshTokenService

    @BeforeEach
    fun setUp() {
        loginRepository = mockk<LoginRepository>()
        jwkService = mockk<JwkService>()
        jwtService = mockk<JwtService>()
        bCryptPasswordEncoder = mockk<BCryptPasswordEncoder>()
        refreshTockenService = mockk<RefreshTokenService>()
        loginService = LoginServiceImpl(loginRepository, bCryptPasswordEncoder, jwkService, jwtService, refreshTockenService)
    }

    @Test
    fun `loginSuccessAction test`() = runTest {
        //given
        val loginModel = LoginModel(
            prefixId = "oporteat",
            suffixId = "0u",
            password = "regione",
            status = LoginStatus.ACTIVE,
            lastLoginAt = OffsetDateTime.now(),
            deletedAt = null,
            id = null
        )

        //when
        //then
        val jwtModel = loginService.loginSuccessAction(loginModel)

    }


    @Test
    fun `attemptAuthenticate if return null then throw NotFoundUser test`() = runTest {
        //given
        val loginReq = LoginUserRequest(suffixId = "0u", prefixId = "dicam", password = "solet")
        coEvery {
            loginRepository.findUser(any(), any())
        } returns null
        //when
        //then
        assertThrows<NotFoundUserException> {
            loginService.attemptAuthenticate(loginReq)
        }
        coVerify(exactly = 1) { loginRepository.findUser(any(), any()) }
    }

    @Test
    fun `attemptAuthentication test `() = runTest {
        //given
        val loginReq = LoginUserRequest(suffixId = "0u", prefixId = "dicam", password = "solet")
        coEvery {
            bCryptPasswordEncoder.matches(any(), any())
        } returns true
        coEvery {
            loginRepository.findUser(any(), any())
        } answers {
            LoginModel(
                suffixId = "0u",
                prefixId = "dicam",
                password = "solet",
                status = LoginStatus.ACTIVE,
                lastLoginAt = OffsetDateTime.now(),
                deletedAt = null,
                id = UUID.randomUUID()
            )
        }
        //when
        val attemptAuthenticate = loginService.attemptAuthenticate(loginReq)
        //then
        assertThat(attemptAuthenticate.prefixId).isEqualTo(loginReq.prefixId )
        assertThat(attemptAuthenticate.status).isEqualTo(LoginStatus.ACTIVE)
    }

    @Test
    fun `attemptAuthentication fail test`() = runTest {
        //given
        val loginReq = LoginUserRequest(suffixId = "0u", prefixId = "dicam", password = "solet")
        coEvery {
            bCryptPasswordEncoder.matches(any(), any())
        } returns false
        coEvery {
            loginRepository.findUser(any(), any())
        } answers {
            LoginModel(
                suffixId = "0u",
                prefixId = "dicam",
                password = "solet",
                status = LoginStatus.ACTIVE,
                lastLoginAt = OffsetDateTime.now(),
                deletedAt = null,
                id = UUID.randomUUID()
            )
        }
        //when
        //then
        assertThrows<NotFoundUserException> {
            loginService.attemptAuthenticate(loginReq)
        }
    }


}
