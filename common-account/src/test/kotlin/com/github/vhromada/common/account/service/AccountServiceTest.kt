package com.github.vhromada.common.account.service

import com.github.vhromada.common.account.mapper.AccountMapper
import com.github.vhromada.common.account.repository.AccountRepository
import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.entity.Account
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.userdetails.UsernameNotFoundException
import java.util.Optional

/**
 * A class represents test for class [AccountService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class AccountServiceTest {

    /**
     * Instance of [AccountRepository]
     */
    @Mock
    private lateinit var accountRepository: AccountRepository

    /**
     * Instance of [AccountMapper]
     */
    @Mock
    private lateinit var accountMapper: AccountMapper

    /**
     * Instance of [AccountService]
     */
    private lateinit var accountService: AccountService

    /**
     * Initializes data.
     */
    @BeforeEach
    fun setUp() {
        accountService = AccountServiceImpl(accountRepository, accountMapper)
    }

    /**
     * Test method for [AccountService.getAll].
     */
    @Test
    fun getAll() {
        val expectedAccounts = listOf(AccountUtils.newAccountDomain(id = 1))

        whenever(accountRepository.findAll()).thenReturn(expectedAccounts)

        val accounts = accountService.getAll()

        assertThat(accounts).isEqualTo(expectedAccounts)

        verify(accountRepository).findAll()
        verifyNoMoreInteractions(accountRepository)
        verifyZeroInteractions(accountMapper)
    }

    /**
     * Test method for [AccountService.get].
     */
    @Test
    fun get() {
        val expectedAccount = AccountUtils.newAccountDomain(id = 1)

        whenever(accountRepository.findById(any())).thenReturn(Optional.of(expectedAccount))

        val account = accountService.get(id = expectedAccount.id!!)

        assertThat(account).isPresent
        AccountUtils.assertAccountDeepEquals(expected = expectedAccount, actual = account.get())

        verify(accountRepository).findById(expectedAccount.id!!)
        verifyNoMoreInteractions(accountRepository)
        verifyZeroInteractions(accountMapper)
    }

    /**
     * Test method for [AccountService.update].
     */
    @Test
    fun update() {
        val account = AccountUtils.newAccountDomain(id = 1)

        accountService.update(account = account)

        verify(accountRepository).save(account)
        verifyNoMoreInteractions(accountRepository)
        verifyZeroInteractions(accountMapper)
    }

    /**
     * Test method for [AccountService.add].
     */
    @Test
    fun add() {
        val account = AccountUtils.newAccountDomain(id = 1)

        accountService.add(account = account)

        verify(accountRepository).save(account)
        verifyNoMoreInteractions(accountRepository)
        verifyZeroInteractions(accountMapper)
    }

    /**
     * Test method for [AccountService.findByUsername] with correct username.
     */
    @Test
    fun findByUsername() {
        val expectedAccount = AccountUtils.newAccountDomain(id = 1)

        whenever(accountRepository.findByUsername(username = any())).thenReturn(Optional.of(expectedAccount))

        val account = accountService.findByUsername(username = expectedAccount.username)

        assertThat(account).isPresent
        AccountUtils.assertAccountDeepEquals(expected = account.get(), actual = expectedAccount)

        verify(accountRepository).findByUsername(username = expectedAccount.username)
        verifyNoMoreInteractions(accountRepository)
        verifyZeroInteractions(accountMapper)
    }

    /**
     * Test method for [AccountService.findByUsername] with invalid username.
     */
    @Test
    fun findByUsernameByInvalidUsername() {
        val username = "test"

        whenever(accountRepository.findByUsername(username = any())).thenReturn(Optional.empty())

        val account = accountService.findByUsername(username = username)

        assertThat(account).isNotPresent

        verify(accountRepository).findByUsername(username = username)
        verifyNoMoreInteractions(accountRepository)
        verifyZeroInteractions(accountMapper)
    }

    /**
     * Test method for [AccountService.loadUserByUsername] with correct username.
     */
    @Test
    fun loadUserByUsername() {
        val expectedAccount = AccountUtils.newAccountDomain(id = 1)

        whenever(accountRepository.findByUsername(username = any())).thenReturn(Optional.of(expectedAccount))
        whenever(accountMapper.map(source = any<com.github.vhromada.common.account.domain.Account>())).thenReturn(AccountUtils.newAccount(id = 1))

        val account = accountService.loadUserByUsername(expectedAccount.username)

        assertThat(account).isInstanceOf(Account::class.java)
        AccountUtils.assertAccountDeepEquals(expected = account as Account, actual = expectedAccount)

        verify(accountRepository).findByUsername(expectedAccount.username)
        verify(accountMapper).map(source = expectedAccount)
        verifyNoMoreInteractions(accountRepository, accountMapper)
    }

    /**
     * Test method for [AccountService.loadUserByUsername] with invalid username.
     */
    @Test
    fun loadUserByUsernameByInvalidUsername() {
        val username = "test"

        whenever(accountRepository.findByUsername(any())).thenReturn(Optional.empty())

        assertThrows(UsernameNotFoundException::class.java) { accountService.loadUserByUsername(username) }

        verify(accountRepository).findByUsername(username)
        verifyNoMoreInteractions(accountRepository)
        verifyZeroInteractions(accountMapper)
    }

}
