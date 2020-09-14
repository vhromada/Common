package com.github.vhromada.common.account.utils

import com.github.vhromada.common.account.domain.Account
import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.account.entity.Credentials
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
        val uuid: String
        val roles = mutableListOf<Role>()
        when (index) {
            1 -> {
                uuid = "08f12e2f-f842-436f-ac0d-b4d1026d74be"
                roles.add(RoleUtils.getRole(1))
            }
            2 -> {
                uuid = "1436b587-401e-4183-9982-9e7eaea9d33a"
                roles.add(RoleUtils.getRole(2))
            }
            3 -> {
                uuid = "be63de12-96b7-46fc-943d-a1af577c0e5d"
                roles.add(RoleUtils.getRole(1))
                roles.add(RoleUtils.getRole(2))
            }
            else -> throw IllegalArgumentException("Bad index")
        }

        return Account(
                id = index,
                uuid = uuid,
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
    fun newAccountDomain(id: Int?): Account {
        return Account(
                id = id,
                uuid = "c01cb46d-0acf-402b-9d76-d12a75b98f8a",
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
    fun newAccount(id: Int?): com.github.vhromada.common.entity.Account {
        return com.github.vhromada.common.entity.Account(
                id = id,
                uuid = "c01cb46d-0acf-402b-9d76-d12a75b98f8a",
                username = "username",
                password = "password",
                roles = listOf(RoleUtils.getRole(1).name))
    }

    /**
     * Returns credentials.
     *
     * @return credentials
     */
    fun newCredentials(): Credentials {
        return Credentials(
                username = "username",
                password = "password")
    }

    /**
     * Returns count of accounts.
     *
     * @param entityManager entity manager
     * @return count of accounts
     */
    @Suppress("CheckStyle")
    fun getAccountsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(a.id) FROM Account a", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns account.
     *
     * @param entityManager entity manager
     * @param id            game ID
     * @return account
     */
    fun getAccount(entityManager: EntityManager, id: Int): Account? {
        return entityManager.find(Account::class.java, id)
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
            it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            it.assertThat(actual.username).isEqualTo(expected.username)
            it.assertThat(actual.password).isEqualTo(expected.password)
            RoleUtils.assertRolesDeepEquals(expected.roles, actual.roles)
        }
    }

    /**
     * Asserts accounts deep equals.
     *
     * @param expected expected accounts
     * @param actual   actual accounts
     */
    fun assertAccountListDeepEquals(expected: List<com.github.vhromada.common.entity.Account?>?, actual: List<Account?>?) {
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
    fun assertAccountDeepEquals(expected: com.github.vhromada.common.entity.Account?, actual: Account?) {
        assertSoftly {
            it.assertThat(expected).isNotNull
            it.assertThat(actual).isNotNull
        }
        assertSoftly {
            it.assertThat(actual!!.id).isEqualTo(expected!!.id)
            it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            it.assertThat(actual.username).isEqualTo(expected.username)
            it.assertThat(actual.password).isEqualTo(expected.password)
            RoleUtils.assertRolesListDeepEquals(expected.roles, actual.roles)
        }
    }

}
