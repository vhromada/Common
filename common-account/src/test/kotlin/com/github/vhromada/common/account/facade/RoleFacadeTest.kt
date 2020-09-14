package com.github.vhromada.common.account.facade

import com.github.vhromada.common.account.domain.Role
import com.github.vhromada.common.account.entity.UpdateRoles
import com.github.vhromada.common.account.facade.impl.RoleFacadeImpl
import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.service.AccountService
import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.account.utils.RoleUtils
import com.github.vhromada.common.account.validator.AccountValidator
import com.github.vhromada.common.account.validator.RoleValidator
import com.github.vhromada.common.mapper.Mapper
import com.github.vhromada.common.result.Result
import com.github.vhromada.common.result.Status
import com.nhaarman.mockitokotlin2.any
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
import java.util.Optional

/**
 * Result for invalid data
 */
private val INVALID_DATA_RESULT = Result.error<Unit>("DATA_INVALID", "Data must be valid.")

/**
 * A class represents test for class [RoleFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class RoleFacadeTest {

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
     * Instance of [Mapper]
     */
    @Mock
    private lateinit var roleMapper: Mapper<Role, String>

    /**
     * Instance of [AccountValidator]
     */
    @Mock
    private lateinit var accountValidator: AccountValidator

    /**
     * Instance of [RoleValidator]
     */
    @Mock
    private lateinit var roleValidator: RoleValidator

    /**
     * Instance of [RoleFacade]
     */
    private lateinit var facade: RoleFacade

    /**
     * Initializes facade for accounts.
     */
    @BeforeEach
    fun setUp() {
        facade = RoleFacadeImpl(accountService, roleRepository, roleMapper, accountValidator, roleValidator)
    }

    /**
     * Test method for [RoleFacade.getAll].
     */
    @Test
    fun getAll() {
        val domainList = listOf(RoleUtils.getRole(1), RoleUtils.getRole(2))
        val entityList = domainList.map { it.name }

        whenever(roleRepository.findAll()).thenReturn(domainList)
        whenever(roleMapper.map(any<List<Role>>())).thenReturn(entityList)

        val result = facade.getAll()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entityList)
            it.assertThat(result.events()).isEmpty()
        }

        verify(roleRepository).findAll()
        verify(roleMapper).map(domainList)
        verifyNoMoreInteractions(roleRepository, roleMapper)
        verifyZeroInteractions(accountService, accountValidator, roleValidator)
    }

    /**
     * Test method for [RoleFacade.updateRoles].
     */
    @Test
    fun updateRoles() {
        val role = RoleUtils.getRole(2)
        val domainRoles = listOf(role)
        val roles = domainRoles.map { it.name }
        val account = AccountUtils.newAccount(1)
        val domainAccount = AccountUtils.newAccountDomain(1)
        val updateRoles = UpdateRoles(roles)

        whenever(accountService.get(any())).thenReturn(Optional.of(domainAccount))
        whenever(roleRepository.findByName(any())).thenReturn(Optional.of(role))
        whenever(accountValidator.validateExist(any())).thenReturn(Result())
        whenever(roleValidator.validateUpdateRoles(any())).thenReturn(Result())

        val result = facade.updateRoles(account, updateRoles)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).get(account.id!!)
        verify(accountService).update(domainAccount)
        roles.forEach { verify(roleRepository).findByName(it) }
        verify(accountValidator).validateExist(account)
        verify(roleValidator).validateUpdateRoles(updateRoles)
        verifyNoMoreInteractions(accountService, roleRepository, accountValidator, roleValidator)
        verifyZeroInteractions(roleMapper)
    }

    /**
     * Test method for [RoleFacade.updateRoles] with invalid account.
     */
    @Test
    fun updateRolesInvalidAccount() {
        val account = AccountUtils.newAccount(Int.MAX_VALUE)
        val roles = UpdateRoles(listOf(RoleUtils.getRole(1).name))

        whenever(accountValidator.validateExist(any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.updateRoles(account, roles)

        assertThat(result).isEqualTo(INVALID_DATA_RESULT)

        verify(accountValidator).validateExist(account)
        verifyNoMoreInteractions(accountValidator)
        verifyZeroInteractions(accountService, roleRepository, roleMapper, roleValidator)
    }

    /**
     * Test method for [RoleFacade.updateRoles] with invalid roles.
     */
    @Test
    fun updateRolesInvalidRoles() {
        val account = AccountUtils.newAccount(1)
        val roles = UpdateRoles(listOf("ROLE_TEST"))

        whenever(accountValidator.validateExist(any())).thenReturn(Result())
        whenever(roleValidator.validateUpdateRoles(any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.updateRoles(account, roles)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(INVALID_DATA_RESULT.events())
        }

        verify(accountValidator).validateExist(account)
        verify(roleValidator).validateUpdateRoles(roles)
        verifyNoMoreInteractions(accountValidator, roleValidator)
        verifyZeroInteractions(accountService, roleRepository, roleMapper)
    }

}
