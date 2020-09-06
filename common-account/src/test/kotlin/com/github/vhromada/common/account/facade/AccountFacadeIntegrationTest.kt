package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.AccountTestConfiguration
import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.result.Status
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.persistence.EntityManager

/**
 * A class represents integration test for class [AccountFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AccountTestConfiguration::class])
class AccountFacadeIntegrationTest {

    /**
     * Instance of [EntityManager]
     */
    @Autowired
    @Qualifier("containerManagedEntityManager")
    private lateinit var entityManager: EntityManager

    /**
     * Instance of (@link AccountFacade}
     */
    @Autowired
    private lateinit var facade: AccountFacade

    /**
     * Test method for [AccountFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val result = facade.add(newAccountNullUuid(null))

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        val expectedAccount = AccountUtils.newAccountDomain(AccountUtils.ACCOUNTS_COUNT + 1)
        val repositoryData = AccountUtils.getAccount(entityManager, AccountUtils.ACCOUNTS_COUNT + 1)
        AccountUtils.assertAccountDeepEquals(expectedAccount, repositoryData)
        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT + 1)
    }

    /**
     * Test method for [AccountFacade.add] with account with not null ID.
     */
    @Test
    fun addNotNullId() {
        val result = facade.add(newAccountNullUuid(Int.MAX_VALUE))

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_ID_NOT_NULL", "ID must be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account with not null UUID.
     */
    @Test
    fun addNotNullUuid() {
        val result = facade.add(newAccount(null))

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_UUID_NOT_NULL", "UUID must be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account with null username.
     */
    @Test
    fun addNullUsername() {
        val account = newAccountNullUuid(null)
                .copy(username = null)

        val result = facade.add(account)

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_USERNAME_NULL", "Username mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account with null password.
     */
    @Test
    fun addNullPassword() {
        val account = newAccountNullUuid(null)
                .copy(password = null)

        val result = facade.add(account)

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_PASSWORD_NULL", "Password mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account with not existing role.
     */
    @Test
    fun addRoleNotExisting() {
        val account = newAccountNullUuid(null)
                .copy(roles = listOf("ROLE_TEST"))

        val result = facade.add(account)

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ROLE_NOT_EXIST", "Role doesn't exist.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val result = facade.update(newAccount(1))

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        AccountUtils.assertAccountDeepEquals(AccountUtils.newAccountDomain(1), AccountUtils.getAccount(entityManager, 1))
        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with null ID.
     */
    @Test
    fun updateNullId() {
        val result = facade.update(newAccount(null))

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_ID_NULL", "ID mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with null UUID.
     */
    @Test
    fun updateNullUuid() {
        val result = facade.update(newAccountNullUuid(1))

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_UUID_NULL", "UUID mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with null username.
     */
    @Test
    fun updateNullUsername() {
        val account = newAccount(1)
                .copy(username = null)

        val result = facade.update(account)

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_USERNAME_NULL", "Username mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with null password.
     */
    @Test
    fun updateNullPassword() {
        val account = newAccount(1)
                .copy(password = null)

        val result = facade.update(account)

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_PASSWORD_NULL", "Password mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with not existing account.
     */
    @Test
    fun updateAccountNotExisting() {
        val result = facade.update(newAccount(Int.MAX_VALUE))

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_NOT_EXIST", "Account doesn't exist.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with not existing role.
     */
    @Test
    fun updateRoleNotExisting() {
        val account = newAccount(1)
                .copy(roles = listOf("ROLE_TEST"))

        val result = facade.update(account)

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ROLE_NOT_EXIST", "Role doesn't exist.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Returns instance of [Account].
     *
     * @param id ID
     * @return instance of [Account]
     */
    private fun newAccount(id: Int?): Account {
        return AccountUtils.newAccount(id)
    }

    /**
     * Returns instance of [Account] with null UUID.
     *
     * @param id ID
     * @return instance of [Account]
     */
    private fun newAccountNullUuid(id: Int?): Account {
        return newAccount(id)
                .copy(uuid = null)
    }

}
