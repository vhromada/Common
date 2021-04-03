package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.AccountTestConfiguration
import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.account.utils.RoleUtils
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.result.Status
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
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
     * Test method for [AccountFacade.getAll].
     */
    @Test
    fun getAll() {
        val result = facade.getAll()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }
        AccountUtils.assertAccountListDeepEquals(expected = result.data!!, actual = AccountUtils.getAccounts())

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val result = facade.get(id = i)

            assertSoftly {
                it.assertThat(result.status).isEqualTo(Status.OK)
                it.assertThat(result.events()).isEmpty()
            }
            AccountUtils.assertAccountDeepEquals(expected = result.data!!, actual = AccountUtils.getAccount(index = i))
        }

        val result = facade.get(id = Int.MAX_VALUE)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isNull()
            it.assertThat(result.events()).isEmpty()
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account.
     */
    @Test
    @DirtiesContext
    fun add() {
        val result = facade.add(account = newAccountNullUuid(id = null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        val expectedAccount = AccountUtils.newAccountDomain(id = AccountUtils.ACCOUNTS_COUNT + 1)
        val repositoryData = AccountUtils.getAccount(entityManager = entityManager, id = AccountUtils.ACCOUNTS_COUNT + 1)
        AccountUtils.assertAccountDeepEquals(expected = expectedAccount, actual = repositoryData)
        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT + 1)
    }

    /**
     * Test method for [AccountFacade.add] with account with not null ID.
     */
    @Test
    fun addNotNullId() {
        val result = facade.add(account = newAccountNullUuid(id = Int.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_ID_NOT_NULL", message = "ID must be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account with not null UUID.
     */
    @Test
    fun addNotNullUuid() {
        val result = facade.add(account = newAccount(id = null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_UUID_NOT_NULL", message = "UUID must be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account with null username.
     */
    @Test
    fun addNullUsername() {
        val account = newAccountNullUuid(id = null)
            .copy(username = null)

        val result = facade.add(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_NULL", message = "Username mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account with null password.
     */
    @Test
    fun addNullPassword() {
        val account = newAccountNullUuid(id = null)
            .copy(password = null)

        val result = facade.add(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_PASSWORD_NULL", message = "Password mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account with not existing role.
     */
    @Test
    fun addRoleNotExisting() {
        val account = newAccountNullUuid(id = null)
            .copy(roles = listOf("ROLE_TEST"))

        val result = facade.add(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ROLE_NOT_EXIST", message = "Role doesn't exist.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with account with existing username.
     */
    @Test
    fun addExistingUsername() {
        val account = newAccountNullUuid(id = null)
            .copy(username = AccountUtils.getAccount(index = 1).username)

        val result = facade.add(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_ALREADY_EXIST", message = "Username already exists.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with credentials.
     */
    @Test
    @DirtiesContext
    fun addCredentials() {
        val result = facade.add(credentials = AccountUtils.newCredentials())

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        val expectedAccount = AccountUtils.newAccountDomain(id = AccountUtils.ACCOUNTS_COUNT + 1)
            .copy(roles = listOf(RoleUtils.getRole(index = 2)))
        val repositoryData = AccountUtils.getAccount(entityManager = entityManager, id = AccountUtils.ACCOUNTS_COUNT + 1)
        AccountUtils.assertAccountDeepEquals(expected = expectedAccount, actual = repositoryData)
        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT + 1)
    }

    /**
     * Test method for [AccountFacade.add] with credentials with username.
     */
    @Test
    fun addCredentialsNullUsername() {
        val account = AccountUtils.newCredentials()
            .copy(username = null)

        val result = facade.add(credentials = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_NULL", message = "Username mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with credentials with password.
     */
    @Test
    fun addCredentialsNullPassword() {
        val account = AccountUtils.newCredentials()
            .copy(password = null)

        val result = facade.add(credentials = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_PASSWORD_NULL", message = "Password mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.add] with credentials with existing username.
     */
    @Test
    fun addCredentialsExistingUsername() {
        val account = AccountUtils.newCredentials()
            .copy(username = AccountUtils.getAccount(index = 2).username)

        val result = facade.add(credentials = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_ALREADY_EXIST", message = "Username already exists.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account.
     */
    @Test
    @DirtiesContext
    fun update() {
        val result = facade.update(account = newAccount(id = 1))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        AccountUtils.assertAccountDeepEquals(expected = AccountUtils.newAccountDomain(id = 1), actual = AccountUtils.getAccount(entityManager = entityManager, id = 1))
        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with null ID.
     */
    @Test
    fun updateNullId() {
        val result = facade.update(account = newAccount(id = null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_ID_NULL", message = "ID mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with null UUID.
     */
    @Test
    fun updateNullUuid() {
        val result = facade.update(account = newAccountNullUuid(id = 1))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_UUID_NULL", message = "UUID mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with null username.
     */
    @Test
    fun updateNullUsername() {
        val account = newAccount(id = 1)
            .copy(username = null)

        val result = facade.update(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_NULL", message = "Username mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with null password.
     */
    @Test
    fun updateNullPassword() {
        val account = newAccount(id = 1)
            .copy(password = null)

        val result = facade.update(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_PASSWORD_NULL", message = "Password mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with not existing account.
     */
    @Test
    fun updateAccountNotExisting() {
        val result = facade.update(account = newAccount(id = Int.MAX_VALUE))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_NOT_EXIST", message = "Account doesn't exist.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with not existing role.
     */
    @Test
    fun updateRoleNotExisting() {
        val account = newAccount(id = 1)
            .copy(roles = listOf("ROLE_TEST"))

        val result = facade.update(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ROLE_NOT_EXIST", message = "Role doesn't exist.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with account with existing username.
     */
    @Test
    fun updateExistingUsername() {
        val account = newAccount(id = 1)
            .copy(username = AccountUtils.getAccount(index = 2).username)

        val result = facade.update(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_ALREADY_EXIST", message = "Username already exists.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with credentials.
     */
    @Test
    @DirtiesContext
    fun updateCredentials() {
        val result = facade.update(credentials = AccountUtils.newCredentials())

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        AccountUtils.assertAccountDeepEquals(expected = AccountUtils.newAccountDomain(id = 1), AccountUtils.getAccount(entityManager = entityManager, id = 1))
        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with credentials with username.
     */
    @Test
    fun updateCredentialsNullUsername() {
        val account = AccountUtils.newCredentials()
            .copy(username = null)

        val result = facade.update(credentials = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_NULL", message = "Username mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with credentials with password.
     */
    @Test
    fun updateCredentialsNullPassword() {
        val account = AccountUtils.newCredentials()
            .copy(password = null)

        val result = facade.add(credentials = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_PASSWORD_NULL", message = "Password mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.update] with credentials with existing username.
     */
    @Test
    fun updateCredentialsExistingUsername() {
        val account = AccountUtils.newCredentials()
            .copy(username = AccountUtils.getAccount(index = 2).username)

        val result = facade.update(credentials = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_ALREADY_EXIST", message = "Username already exists.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Test method for [AccountFacade.findByUsername].
     */
    @Test
    fun findByUsername() {
        val expectedAccount = AccountUtils.getAccount(index = 1)

        val account = facade.findByUsername(username = expectedAccount.username)

        assertThat(account).isPresent
        AccountUtils.assertAccountDeepEquals(expected = account.get(), actual = expectedAccount)

        assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
    }

    /**
     * Returns instance of [Account].
     *
     * @param id ID
     * @return instance of [Account]
     */
    private fun newAccount(id: Int?): Account {
        return AccountUtils.newAccount(id = id)
    }

    /**
     * Returns instance of [Account] with null UUID.
     *
     * @param id ID
     * @return instance of [Account]
     */
    private fun newAccountNullUuid(id: Int?): Account {
        return newAccount(id = id)
            .copy(uuid = null)
    }

}
