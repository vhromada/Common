package com.github.vhromada.common.account.repository

import com.github.vhromada.common.account.AccountTestConfiguration
import com.github.vhromada.common.account.utils.AccountUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

/**
 * A class represents integration test for class [AccountRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AccountTestConfiguration::class])
@Transactional
@Rollback
class AccountRepositoryIntegrationTest {

    /**
     * Instance of [EntityManager]
     */
    @Autowired
    @Qualifier("containerManagedEntityManager")
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [AccountRepository]
     */
    @Autowired
    private lateinit var accountRepository: AccountRepository

    /**
     * Test method for get accounts.
     */
    @Test
    fun getAccounts() {
        val accounts = accountRepository.findAll()

        AccountUtils.assertAccountsDeepEquals(AccountUtils.getAccounts(), accounts)

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for get account.
     */
    @Test
    fun getAccount() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val account = accountRepository.findById(i).orElse(null)

            AccountUtils.assertAccountDeepEquals(AccountUtils.getAccount(i), account)
        }

        assertThat(accountRepository.findById(Int.MAX_VALUE)).isNotPresent

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for get account by username.
     */
    @Test
    fun findByUsername() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val expectedAccount = AccountUtils.getAccount(i)
            val account = accountRepository.findByUsername(expectedAccount.username).orElse(null)

            AccountUtils.assertAccountDeepEquals(expectedAccount, account)
        }

        assertThat(accountRepository.findByUsername("TEST")).isNotPresent

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for get account by uuid.
     */
    @Test
    fun findByUuid() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val expectedAccount = AccountUtils.getAccount(i)
            val account = accountRepository.findByUuid(expectedAccount.uuid!!).orElse(null)

            AccountUtils.assertAccountDeepEquals(expectedAccount, account)
        }

        assertThat(accountRepository.findByUuid("TEST")).isNotPresent

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

}
