package cz.vhromada.common.account.utils

import cz.vhromada.common.account.domain.Account
import cz.vhromada.common.account.domain.Role
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import javax.persistence.EntityManager

/**
 * A class represents utility class for accounts.
 *
 * @author Vladimir Hromada
 */
object AccountUtils {

    /**
     * Count of accounts
     */
    const val ACCOUNTS_COUNT = 3

    /**
     * Account name
     */
    private const val ACCOUNT = "Account "

    /**
     * Returns accounts.
     *
     * @return accounts
     */
    fun getAccounts(): List<Account> {
        val accounts = mutableListOf<Account>()
        for (i in 0 until ACCOUNTS_COUNT) {
            accounts.add(getAccount(i + 1))
        }

        return accounts
    }

    /**
     * Returns account for index.
     *
     * @param index index
     * @return account for index
     */
    fun getAccount(index: Int): Account {
        val roles = mutableListOf<Role>()
        when (index) {
            1 -> roles.add(RoleUtils.getRole(1))
            2 -> roles.add(RoleUtils.getRole(2))
            3 -> {
                roles.add(RoleUtils.getRole(1))
                roles.add(RoleUtils.getRole(2))
            }
            else -> throw IllegalArgumentException("Bad index")
        }

        return Account(
                id = index,
                username = "$ACCOUNT$index username",
                password = "$ACCOUNT$index password",
                roles = roles)
    }

    /**
     * Returns account.
     *
     * @param id ID
     * @return account
     */
    fun newAccountDomain(id: Int): Account {
        return Account(
                id = id,
                username = "username",
                password = "password",
                roles = listOf(RoleUtils.getRole(1)))
    }


    /**
     * Returns account.
     *
     * @param id ID
     * @return account
     */
    fun newAccount(id: Int): cz.vhromada.common.entity.Account {
        return cz.vhromada.common.entity.Account(
                id = id,
                username = "username",
                password = "password",
                roles = listOf(RoleUtils.getRole(1).name))
    }

    /**
     * Returns count of accounts.
     *
     * @param entityManager entity manager
     * @return count of accounts
     */
    @Suppress("CheckStyle")
    fun getAccountsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(u.id) FROM Account u", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Asserts accounts deep equals.
     *
     * @param expected expected accounts
     * @param actual   actual accounts
     */
    fun assertAccountsDeepEquals(expected: List<Account?>?, actual: List<Account?>?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertThat(expected!!.size).isEqualTo(actual!!.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertAccountDeepEquals(expected[i], actual[i])
            }
        }
    }

    /**
     * Asserts account deep equals.
     *
     * @param expected expected account
     * @param actual   actual account
     */
    fun assertAccountDeepEquals(expected: Account?, actual: Account?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertSoftly {
            it.assertThat(actual!!.id).isEqualTo(expected!!.id)
            it.assertThat(actual.username).isEqualTo(expected.username)
            it.assertThat(actual.password).isEqualTo(expected.password)
            RoleUtils.assertRolesDeepEquals(expected.roles, actual.roles)
        }
    }

    /**
     * Asserts account deep equals.
     *
     * @param expected expected account
     * @param actual   actual account
     */
    fun assertAccountDeepEquals(expected: Account?, actual: cz.vhromada.common.entity.Account?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertSoftly {
            it.assertThat(actual!!.id).isEqualTo(expected!!.id)
            it.assertThat(actual.username).isEqualTo(expected.username)
            it.assertThat(actual.password).isEqualTo(expected.password)
            RoleUtils.assertRolesListDeepEquals(expected.roles, actual.roles)
        }
    }

}