package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.facade.impl.AccountFacadeImpl
import com.github.vhromada.common.account.mapper.AccountMapper
import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.service.AccountService
import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.account.utils.RoleUtils
import com.github.vhromada.common.account.validator.AccountValidator
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.provider.AccountProvider
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
private val INVALID_DATA_RESULT = Result.error<Unit>(key = "DATA_INVALID", message = "Data must be valid.")

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
    private lateinit var accountService: AccountService

    /**
     * Instance of [RoleRepository]
     */
    @Mock
    private lateinit var roleRepository: RoleRepository

    /**
     * Instance of [AccountMapper]
     */
    @Mock
    private lateinit var accountMapper: AccountMapper

    /**
     * Instance of [AccountValidator]
     */
    @Mock
    private lateinit var accountValidator: AccountValidator

    /**
     * Instance of [PasswordEncoder]
     */
    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    /**
     * Instance of [AccountProvider]
     */
    @Mock
    private lateinit var accountProvider: AccountProvider

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
        facade = AccountFacadeImpl(
            accountService = accountService,
            roleRepository = roleRepository,
            accountMapper = accountMapper,
            accountValidator = accountValidator,
            passwordEncoder = passwordEncoder,
            accountProvider = accountProvider,
            uuidProvider = uuidProvider
        )
    }

    /**
     * Test method for [AccountFacade.getAll].
     */
    @Test
    fun getAll() {
        val domainList = listOf(AccountUtils.newAccountDomain(id = 1), AccountUtils.newAccountDomain(id = 2))
        val entityList = listOf(AccountUtils.newAccount(id = 1), AccountUtils.newAccount(id = 2))

        whenever(accountService.getAll()).thenReturn(domainList)
        whenever(accountMapper.map(source = any<List<com.github.vhromada.common.account.domain.Account>>())).thenReturn(entityList)

        val result = facade.getAll()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entityList)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).getAll()
        verify(accountMapper).map(source = domainList)
        verifyNoMoreInteractions(accountService, accountMapper)
        verifyZeroInteractions(roleRepository, accountValidator, passwordEncoder, accountProvider, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.get] with existing data.
     */
    @Test
    fun getExistingData() {
        val domain = AccountUtils.newAccountDomain(id = 1)
        val entity = AccountUtils.newAccount(id = 1)

        whenever(accountService.get(id = any())).thenReturn(Optional.of(domain))
        whenever(accountMapper.map(source = any<com.github.vhromada.common.account.domain.Account>())).thenReturn(entity)

        val result = facade.get(id = 1)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entity)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).get(id = 1)
        verify(accountMapper).map(source = domain)
        verifyNoMoreInteractions(accountService, accountMapper)
        verifyZeroInteractions(roleRepository, accountValidator, passwordEncoder, accountProvider, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.get] with not existing data.
     */
    @Test
    fun getNotExistingData() {
        whenever(accountService.get(id = any())).thenReturn(Optional.empty())

        val result = facade.get(id = Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).get(id = Int.MAX_VALUE)
        verifyNoMoreInteractions(accountService)
        verifyZeroInteractions(roleRepository, accountMapper, accountValidator, passwordEncoder, accountProvider, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.add] with account.
     */
    @Test
    fun add() {
        val account = AccountUtils.newAccount(id = null)
        val accountDomain = AccountUtils.newAccountDomain(id = null)
        val role = RoleUtils.getRole(index = 1)
        val argumentCaptor = argumentCaptorAccount()

        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(role))
        whenever(accountMapper.mapBack(source = any<Account>())).thenReturn(accountDomain)
        whenever(accountValidator.validateNew(account = any())).thenReturn(Result())
        whenever(passwordEncoder.encode(any())).thenReturn(account.password)
        whenever(uuidProvider.getUuid()).thenReturn(account.uuid)

        val result = facade.add(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).add(account = argumentCaptor.capture())
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verify(accountMapper).mapBack(source = account)
        verify(accountValidator).validateNew(account = account)
        verify(passwordEncoder).encode(accountDomain.password)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(accountService, roleRepository, accountMapper, accountValidator, passwordEncoder, uuidProvider)
        verifyZeroInteractions(accountProvider)

        val argument = argumentCaptor.lastValue
        AccountUtils.assertAccountDeepEquals(expected = accountDomain, actual = argument)
    }

    /**
     * Test method for [AccountFacade.add] with invalid account.
     */
    @Test
    fun addInvalidAccount() {
        val account = AccountUtils.newAccount(id = Int.MAX_VALUE)

        whenever(accountValidator.validateNew(account = any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.add(account = account)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(accountValidator).validateNew(account = account)
        verifyNoMoreInteractions(accountValidator)
        verifyZeroInteractions(accountService, roleRepository, accountMapper, passwordEncoder, accountProvider, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.add] with credentials.
     */
    @Test
    fun addCredentials() {
        val credentials = AccountUtils.newCredentials()
        val account = AccountUtils.newAccount(id = null)
        val accountDomain = AccountUtils.newAccountDomain(id = null)
        val role = RoleUtils.getRole(index = 1)
        val argumentCaptor = argumentCaptorAccount()

        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(role))
        whenever(accountMapper.mapBack(source = any<Account>())).thenReturn(accountDomain)
        whenever(accountMapper.mapCredentials(source = any())).thenReturn(account)
        whenever(accountValidator.validateNew(account = any())).thenReturn(Result())
        whenever(passwordEncoder.encode(any())).thenReturn(credentials.password)
        whenever(uuidProvider.getUuid()).thenReturn(account.uuid)

        val result = facade.add(credentials = credentials)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).add(account = argumentCaptor.capture())
        verify(accountMapper).mapBack(source = account)
        verify(accountMapper).mapCredentials(source = credentials)
        verify(accountValidator).validateNew(account = account)
        verify(passwordEncoder).encode(accountDomain.password)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(accountService, roleRepository, accountMapper, accountValidator, passwordEncoder, uuidProvider)
        verifyZeroInteractions(accountProvider)

        val argument = argumentCaptor.lastValue
        AccountUtils.assertAccountDeepEquals(expected = accountDomain, actual = argument)
    }

    /**
     * Test method for [AccountFacade.add] with invalid credentials.
     */
    @Test
    fun addInvalidCredentials() {
        val credentials = AccountUtils.newCredentials()
        val account = AccountUtils.newAccount(id = null)

        whenever(accountMapper.mapCredentials(source = any())).thenReturn(account)
        whenever(accountValidator.validateNew(account = any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.add(credentials = credentials)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(accountMapper).mapCredentials(source = credentials)
        verify(accountValidator).validateNew(account = account)
        verifyNoMoreInteractions(accountService, accountValidator)
        verifyZeroInteractions(accountProvider)
    }

    /**
     * Test method for [AccountFacade.update] with account.
     */
    @Test
    fun update() {
        val account = AccountUtils.newAccount(id = 1)
        val accountDomain = AccountUtils.newAccountDomain(id = 1)
        val role = RoleUtils.getRole(index = 1)
        val password = "password"

        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(role))
        whenever(accountMapper.mapBack(source = any<Account>())).thenReturn(accountDomain)
        whenever(accountValidator.validateUpdate(account = any())).thenReturn(Result())
        whenever(passwordEncoder.encode(any())).thenReturn(password)

        val result = facade.update(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).update(account = accountDomain)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verify(accountMapper).mapBack(source = account)
        verify(accountValidator).validateUpdate(account = account)
        verify(passwordEncoder).encode(accountDomain.password)
        verifyNoMoreInteractions(accountService, roleRepository, accountMapper, accountValidator, passwordEncoder)
        verifyZeroInteractions(accountProvider, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.update] with invalid account.
     */
    @Test
    fun updateInvalidAccount() {
        val account = AccountUtils.newAccount(id = Int.MAX_VALUE)

        whenever(accountValidator.validateUpdate(account = any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.update(account = account)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(accountValidator).validateUpdate(account = account)
        verifyNoMoreInteractions(accountValidator)
        verifyZeroInteractions(accountService, roleRepository, accountMapper, passwordEncoder, accountProvider, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.update] with credentials.
     */
    @Test
    fun updateCredentials() {
        val credentials = AccountUtils.newCredentials()
        val account = AccountUtils.newAccount(id = 1)
        val accountDomain = AccountUtils.newAccountDomain(id = 1)
        val role = RoleUtils.getRole(index = 1)
        val password = "password"

        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(role))
        whenever(accountMapper.mapBack(source = any<Account>())).thenReturn(accountDomain)
        whenever(accountValidator.validateUpdate(account = any())).thenReturn(Result())
        whenever(accountProvider.getAccount()).thenReturn(account)
        whenever(passwordEncoder.encode(any())).thenReturn(password)

        val result = facade.update(credentials = credentials)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).update(account = accountDomain)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verify(accountMapper).mapBack(source = account)
        verify(accountValidator).validateUpdate(account = account)
        verify(accountProvider).getAccount()
        verify(passwordEncoder).encode(accountDomain.password)
        verifyNoMoreInteractions(accountService, roleRepository, accountMapper, accountValidator, accountProvider, passwordEncoder)
        verifyZeroInteractions(uuidProvider)
    }

    /**
     * Test method for [AccountFacade.update] with invalid credentials.
     */
    @Test
    fun updateInvalidCredentials() {
        val account = AccountUtils.newAccount(id = Int.MAX_VALUE)

        whenever(accountValidator.validateUpdate(account = any())).thenReturn(INVALID_DATA_RESULT)
        whenever(accountProvider.getAccount()).thenReturn(account)

        val result = facade.update(credentials = AccountUtils.newCredentials())

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(accountValidator).validateUpdate(account = account)
        verify(accountProvider).getAccount()
        verifyNoMoreInteractions(accountValidator, accountProvider)
        verifyZeroInteractions(accountService, roleRepository, accountMapper, passwordEncoder, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.findByUsername] with correct username.
     */
    @Test
    fun findByUsername() {
        val expectedAccount = AccountUtils.newAccountDomain(id = 1)

        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.of(expectedAccount))
        whenever(accountMapper.map(source = any<com.github.vhromada.common.account.domain.Account>())).thenReturn(AccountUtils.newAccount(id = 1))

        val account = facade.findByUsername(username = expectedAccount.username)

        assertThat(account).isPresent
        AccountUtils.assertAccountDeepEquals(expected = account.get(), actual = expectedAccount)

        verify(accountService).findByUsername(username = expectedAccount.username)
        verify(accountMapper).map(source = expectedAccount)
        verifyNoMoreInteractions(accountService, accountMapper)
        verifyZeroInteractions(roleRepository, accountValidator, accountProvider, passwordEncoder, uuidProvider)
    }

    /**
     * Test method for [AccountFacade.findByUsername] with invalid username.
     */
    @Test
    fun findByUsernameByInvalidUsername() {
        val username = "test"

        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.empty())

        val account = facade.findByUsername(username = username)

        assertThat(account).isNotPresent

        verify(accountService).findByUsername(username = username)
        verifyNoMoreInteractions(accountService)
        verifyZeroInteractions(roleRepository, accountMapper, accountValidator, accountProvider, passwordEncoder, uuidProvider)
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
