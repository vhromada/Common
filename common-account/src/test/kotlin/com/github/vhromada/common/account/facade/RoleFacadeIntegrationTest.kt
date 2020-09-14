package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.AccountTestConfiguration
import com.github.vhromada.common.account.entity.UpdateRoles
import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.account.utils.RoleUtils
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
 * A class represents integration test for class [RoleFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [AccountTestConfiguration::class])
class RoleFacadeIntegrationTest {

    /**
     * Instance of [EntityManager]
     */
    @Autowired
    @Qualifier("containerManagedEntityManager")
    private lateinit var entityManager: EntityManager

    /**
     * Instance of (@link RoleFacade}
     */
    @Autowired
    private lateinit var facade: RoleFacade

    /**
     * Test method for [RoleFacade.getAll].
     */
    @Test
    fun getAll() {
        val result = facade.getAll()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }
        RoleUtils.assertRolesListDeepEquals(result.data!!, RoleUtils.getRoles())

        assertThat(RoleUtils.getRolesCount(entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }

    /**
     * Test method for [RoleFacade.updateRoles].
     */
    @Test
    @DirtiesContext
    fun updateRoles() {
        val domainRoles = listOf(RoleUtils.getRole(2))
        val roles = domainRoles.map { it.name }
        val account = AccountUtils.newAccount(1)
        val domainAccount = AccountUtils.getAccount(1)
                .copy(roles = domainRoles)

        val result = facade.updateRoles(account, UpdateRoles(roles))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        AccountUtils.assertAccountDeepEquals(domainAccount, AccountUtils.getAccount(entityManager, 1))
        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        assertThat(RoleUtils.getRolesCount(entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }

    //TODO vhromada 14.09.2020: invalid data test
    /**
     * Test method for [RoleFacade.updateRoles] with account with null ID.
     */
    @Test
    fun updateRolesAccountNullId() {
        val result = facade.updateRoles(AccountUtils.newAccount(null), UpdateRoles(listOf(RoleUtils.getRole(2).name)))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_ID_NULL", "ID mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        assertThat(RoleUtils.getRolesCount(entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }

    /**
     * Test method for [RoleFacade.updateRoles] with not existing account.
     */
    @Test
    fun updateRolesNotExistAccount() {
        val result = facade.updateRoles(AccountUtils.newAccount(Int.MAX_VALUE), UpdateRoles(listOf(RoleUtils.getRole(2).name)))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ACCOUNT_NOT_EXIST", "Account doesn't exist.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        assertThat(RoleUtils.getRolesCount(entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }


    /**
     * Test method for [RoleFacade.updateRoles] with roles with null roles.
     */
    @Test
    fun updateRolesNullRoles() {
        val result = facade.updateRoles(AccountUtils.newAccount(1), UpdateRoles(null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ROLES_NULL", "Roles mustn't be null.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        assertThat(RoleUtils.getRolesCount(entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }

    /**
     * Test method for [RoleFacade.updateRoles] with roles with null value.
     */
    @Test
    fun updateRolesRolesWithNullRole() {
        val result = facade.updateRoles(AccountUtils.newAccount(1), UpdateRoles(listOf(null)))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ROLES_CONTAIN_NULL", "Roles mustn't contain null value.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        assertThat(RoleUtils.getRolesCount(entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }

    /**
     * Test method for [RoleFacade.updateRoles] with not existing role.
     */
    @Test
    fun updateRolesNotExistRole() {
        val result = facade.updateRoles(AccountUtils.newAccount(1), UpdateRoles(listOf("ROLE_TEST")))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ROLE_NOT_EXIST", "Role doesn't exist.")))
        }

        assertThat(AccountUtils.getAccountsCount(entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        assertThat(RoleUtils.getRolesCount(entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
    }

}
