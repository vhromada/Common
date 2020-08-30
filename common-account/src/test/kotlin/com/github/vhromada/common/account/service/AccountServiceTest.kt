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
     * Instance of [AccountMapper]
     */
    @Mock
    private lateinit var mapper: AccountMapper

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

    @Test
    fun loadUserByUsername() {
        val expectedAccount = AccountUtils.newAccountDomain(1)

        whenever(repository.findByUsername(any())).thenReturn(Optional.of(expectedAccount))
        whenever(mapper.map(any())).thenReturn(AccountUtils.newAccount(1))

        val account = accountService.loadUserByUsername(expectedAccount.username)

        assertThat(account).isInstanceOf(Account::class.java)
        AccountUtils.assertAccountDeepEquals(expectedAccount, account as Account)

        verify(repository).findByUsername(expectedAccount.username)
        verify(mapper).map(expectedAccount)
        verifyNoMoreInteractions(repository, mapper)
    }

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
