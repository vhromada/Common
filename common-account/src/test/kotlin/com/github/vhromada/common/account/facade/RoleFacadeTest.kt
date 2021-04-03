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
private val INVALID_DATA_RESULT = Result.error<Unit>(key = "DATA_INVALID", message = "Data must be valid.")

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
        facade = RoleFacadeImpl(accountService = accountService, roleRepository = roleRepository, roleMapper = roleMapper, accountValidator = accountValidator, roleValidator = roleValidator)
    }

    /**
     * Test method for [RoleFacade.getAll].
     */
    @Test
    fun getAll() {
        val domainList = listOf(RoleUtils.getRole(index = 1), RoleUtils.getRole(index = 2))
        val entityList = domainList.map { it.name }

        whenever(roleRepository.findAll()).thenReturn(domainList)
        whenever(roleMapper.map(source = any<List<Role>>())).thenReturn(entityList)

        val result = facade.getAll()

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.data).isEqualTo(entityList)
            it.assertThat(result.events()).isEmpty()
        }

        verify(roleRepository).findAll()
        verify(roleMapper).map(source = domainList)
        verifyNoMoreInteractions(roleRepository, roleMapper)
        verifyZeroInteractions(accountService, accountValidator, roleValidator)
    }

    /**
     * Test method for [RoleFacade.updateRoles].
     */
    @Test
    fun updateRoles() {
        val role = RoleUtils.getRole(index = 2)
        val domainRoles = listOf(role)
        val roles = domainRoles.map { it.name }
        val account = AccountUtils.newAccount(id = 1)
        val domainAccount = AccountUtils.newAccountDomain(id = 1)
        val updateRoles = UpdateRoles(roles = roles)

        whenever(accountService.get(id = any())).thenReturn(Optional.of(domainAccount))
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(role))
        whenever(accountValidator.validateExist(account = any())).thenReturn(Result())
        whenever(roleValidator.validateUpdateRoles(roles = any())).thenReturn(Result())

        val result = facade.updateRoles(account = account, roles = updateRoles)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).get(id = account.id!!)
        verify(accountService).update(account = domainAccount)
        roles.forEach { verify(roleRepository).findByName(name = it) }
        verify(accountValidator).validateExist(account = account)
        verify(roleValidator).validateUpdateRoles(roles = updateRoles)
        verifyNoMoreInteractions(accountService, roleRepository, accountValidator, roleValidator)
        verifyZeroInteractions(roleMapper)
    }

    /**
     * Test method for [RoleFacade.updateRoles] with invalid account.
     */
    @Test
    fun updateRolesInvalidAccount() {
        val account = AccountUtils.newAccount(id = Int.MAX_VALUE)
        val roles = UpdateRoles(roles = listOf(RoleUtils.getRole(index = 1).name))

        whenever(accountValidator.validateExist(account = any())).thenReturn(INVALID_DATA_RESULT)
        whenever(roleValidator.validateUpdateRoles(roles = any())).thenReturn(Result())

        val result = facade.updateRoles(account = account, roles = roles)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(INVALID_DATA_RESULT.events())
        }

        verify(accountValidator).validateExist(account = account)
        verify(roleValidator).validateUpdateRoles(roles = roles)
        verifyNoMoreInteractions(accountValidator, roleValidator)
        verifyZeroInteractions(accountService, roleRepository, roleMapper)
    }

    /**
     * Test method for [RoleFacade.updateRoles] with invalid roles.
     */
    @Test
    fun updateRolesInvalidRoles() {
        val account = AccountUtils.newAccount(id = 1)
        val roles = UpdateRoles(roles = listOf("ROLE_TEST"))

        whenever(accountValidator.validateExist(account = any())).thenReturn(Result())
        whenever(roleValidator.validateUpdateRoles(roles = any())).thenReturn(INVALID_DATA_RESULT)

        val result = facade.updateRoles(account = account, roles = roles)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(INVALID_DATA_RESULT.events())
        }

        verify(accountValidator).validateExist(account = account)
        verify(roleValidator).validateUpdateRoles(roles = roles)
        verifyNoMoreInteractions(accountValidator, roleValidator)
        verifyZeroInteractions(accountService, roleRepository, roleMapper)
    }

}
