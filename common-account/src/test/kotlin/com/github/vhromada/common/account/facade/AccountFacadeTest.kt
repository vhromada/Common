package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.service.AccountService
import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.account.utils.RoleUtils
import com.github.vhromada.common.account.validator.AccountValidator
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.provider.UuidProvider
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Status
import com.nhaarman.mockitokotlin2.KArgumentCaptor
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional

/**
 * Result for invalid data
 */
private val INVALID_DATA_RESULT = Result.error<Unit>("DATA_INVALID", "Data must be valid.")

/**
 * A class represents test for class [AccountFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class AccountFacadeTest {

    /**
     * Instance of [AccountService]
     */
    @Mock
    private lateinit var service: AccountService

    /**
     * Instance of [RoleRepository]
     */
    @Mock
    private lateinit var repository: RoleRepository

    /**
     * Instance of [Mapper]
     */
    @Mock
    private lateinit var mapper: Mapper<com.github.vhromada.common.account.domain.Account, Account>

    /**
     * Instance of [AccountValidator]
     */
    @Mock
    private lateinit var validator: AccountValidator

    /**
     * Instance of [PasswordEncoder]
     */
    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [AccountFacade]
     */
    private lateinit var facade: AccountFacade

    /**
     * Initializes facade for accounts.
     */
    @BeforeEach
    fun setUp() {
        facade = AccountFacadeImpl(service, repository, mapper, validator, passwordEncoder, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.add].
     */
    @Test
    fun add() {
        val account = AccountUtils.newAccount(null)
        val accountDomain = AccountUtils.newAccountDomain(null)
        val role = RoleUtils.getRole(1)
        val argumentCaptor = argumentCaptorAccount()

        whenever(repository.findByName(any())).thenReturn(Optional.of(role))
        whenever(mapper.mapBack(any<Account>())).thenReturn(accountDomain)
        whenever(validator.validateNew(any())).thenReturn(Result())
        whenever(passwordEncoder.encode(any())).thenReturn(account.password)
        whenever(uuidProvider.getUuid()).thenReturn(account.uuid)

        val result = facade.add(account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).add(argumentCaptor.capture())
        account.roles!!.forEach { verify(repository).findByName(it) }
        verify(mapper).mapBack(account)
        verify(validator).validateNew(account)
        verify(passwordEncoder).encode(accountDomain.password)
        verifyNoMoreInteractions(service, repository, mapper, validator, passwordEncoder, uuidProvider)

        val argument = argumentCaptor.lastValue
        AccountUtils.assertAccountDeepEquals(accountDomain, argument)
    }

    /**
     * Test method for [AccountFacade.add] with invalid account.
     */
    @Test
    fun addInvalidAccount() {
        val account = AccountUtils.newAccount(Int.MAX_VALUE)

        whenever(validator.validateNew(any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.add(account)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(validator).validateNew(account)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(service, repository, mapper, passwordEncoder, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.update].
     */
    @Test
    fun update() {
        val account = AccountUtils.newAccount(1)
        val accountDomain = AccountUtils.newAccountDomain(1)
        val role = RoleUtils.getRole(1)
        val password = "password"

        whenever(repository.findByName(any())).thenReturn(Optional.of(role))
        whenever(mapper.mapBack(any<Account>())).thenReturn(accountDomain)
        whenever(validator.validateExist(any())).thenReturn(Result())
        whenever(passwordEncoder.encode(any())).thenReturn(password)

        val result = facade.update(account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(service).update(accountDomain)
        account.roles!!.forEach { verify(repository).findByName(it) }
        verify(mapper).mapBack(account)
        verify(validator).validateExist(account)
        verify(passwordEncoder).encode(accountDomain.password)
        verifyNoMoreInteractions(service, repository, mapper, validator, passwordEncoder)
        verifyZeroInteractions(uuidProvider)
    }

    /**
     * Test method for [AccountFacade.update] with invalid account.
     */
    @Test
    fun updateInvalidAccount() {
        val account = AccountUtils.newAccount(Int.MAX_VALUE)

        whenever(validator.validateExist(any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.update(account)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(validator).validateExist(account)
        verifyNoMoreInteractions(validator)
        verifyZeroInteractions(service, repository, mapper, passwordEncoder, uuidProvider)
    }

    /**
     * Returns argument captor for account.
     *
     * @return argument captor for account
     */
    private fun argumentCaptorAccount(): KArgumentCaptor<com.github.vhromada.common.account.domain.Account> {
        return argumentCaptor()
    }

}
