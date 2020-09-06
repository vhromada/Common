package com.github.vhromada.common.account.service

import com.github.vhromada.common.account.repository.AccountRepository
import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.mapper.Mapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
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
    private lateinit var repository: AccountRepository

    /**
     * Instance of [Mapper]
     */
    @Mock
    private lateinit var mapper: Mapper<com.github.vhromada.common.account.domain.Account, Account>

    /**
     * Instance of [AccountService]
     */
    private lateinit var accountService: AccountService

    /**
     * Initializes data.
     */
    @BeforeEach
    fun setUp() {
        accountService = AccountServiceImpl(repository, mapper)
    }

    /**
     * Test method for [AccountService.get].
     */
    @Test
    fun get() {
        val expectedAccount = AccountUtils.newAccountDomain(1)

        whenever(repository.findById(any())).thenReturn(Optional.of(expectedAccount))

        val account = accountService.get(expectedAccount.id!!)

        assertThat(account).isPresent
        AccountUtils.assertAccountDeepEquals(expectedAccount, account.get())

        verify(repository).findById(expectedAccount.id!!)
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [AccountService.update].
     */
    @Test
    fun update() {
        val account = AccountUtils.newAccountDomain(1)

        accountService.update(account)

        verify(repository).save(account)
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [AccountService.add].
     */
    @Test
    fun add() {
        val account = AccountUtils.newAccountDomain(1)

        accountService.add(account)

        verify(repository).save(account)
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(mapper)
    }

    /**
     * Test method for [AccountService.loadUserByUsername] with correct username.
     */
    @Test
    fun loadUserByUsername() {
        val expectedAccount = AccountUtils.newAccountDomain(1)

        whenever(repository.findByUsername(any())).thenReturn(Optional.of(expectedAccount))
        whenever(mapper.map(any<com.github.vhromada.common.account.domain.Account>())).thenReturn(AccountUtils.newAccount(1))

        val account = accountService.loadUserByUsername(expectedAccount.username)

        assertThat(account).isInstanceOf(Account::class.java)
        AccountUtils.assertAccountDeepEquals(expectedAccount, account as Account)

        verify(repository).findByUsername(expectedAccount.username)
        verify(mapper).map(expectedAccount)
        verifyNoMoreInteractions(repository, mapper)
    }

    /**
     * Test method for [AccountService.loadUserByUsername] with invalid username.
     */
    @Test
    fun loadUserByUsernameByInvalidUsername() {
        val username = "test"

        whenever(repository.findByUsername(any())).thenReturn(Optional.empty())

        Assertions.assertThrows(UsernameNotFoundException::class.java) { accountService.loadUserByUsername(username) }

        verify(repository).findByUsername(username)
        verifyNoMoreInteractions(repository)
        verifyZeroInteractions(mapper)
    }

}
