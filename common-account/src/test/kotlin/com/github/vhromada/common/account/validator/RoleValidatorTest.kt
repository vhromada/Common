package com.github.vhromada.common.account.validator

import com.github.vhromada.common.account.entity.UpdateRoles
import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.utils.RoleUtils
import com.github.vhromada.common.account.validator.impl.RoleValidatorImpl
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.result.Status
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

/**
 * A class represents test for class [RoleValidator].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class RoleValidatorTest {

    /**
     * Instance of [RoleRepository]
     */
    @Mock
    private lateinit var roleRepository: RoleRepository

    /**
     * Instance of [RoleValidator]
     */
    private lateinit var roleValidator: RoleValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        roleValidator = RoleValidatorImpl(roleRepository)
    }

    /**
     * Test method for [RoleValidator.validateUpdateRoles] with correct updating roles.
     */
    @Test
    fun validateUpdateRoles() {
        whenever(roleRepository.findByName(any())).thenReturn(Optional.of(RoleUtils.getRole(1)))

        val roles = UpdateRoles(listOf("USER_ROLE"))

        val result = roleValidator.validateUpdateRoles(roles)

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        roles.roles!!.forEach { verify(roleRepository).findByName(it!!) }
        verifyNoMoreInteractions(roleRepository)
    }

    /**
     * Test method for [RoleValidator.validateUpdateRoles] with null updating roles.
     */
    @Test
    fun validateUpdateRolesNullUpdateRoles() {
        val result = roleValidator.validateUpdateRoles(null)

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ROLES_NULL", "Roles mustn't be null.")))
        }

        verifyZeroInteractions(roleRepository)
    }

    /**
     * Test method for [RoleValidator.validateUpdateRoles] with updating roles with null roles.
     */
    @Test
    fun validateUpdateRolesNullRoles() {
        val result = roleValidator.validateUpdateRoles(UpdateRoles(null))

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ROLES_NULL", "Roles mustn't be null.")))
        }

        verifyZeroInteractions(roleRepository)
    }

    /**
     * Test method for [RoleValidator.validateUpdateRoles] with updating roles with roles with null value.
     */
    @Test
    fun validateUpdateRolesNullRole() {
        val result = roleValidator.validateUpdateRoles(UpdateRoles(listOf(null)))

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ROLES_CONTAIN_NULL", "Roles mustn't contain null value.")))
        }

        verifyZeroInteractions(roleRepository)
    }

    /**
     * Test method for [RoleValidator.validateUpdateRoles] with updating roles with not existing role.
     */
    @Test
    fun validateUpdateNotExistRole() {
        whenever(roleRepository.findByName(any())).thenReturn(Optional.empty())

        val roles = UpdateRoles(listOf("ROLE_TEST"))

        val result = roleValidator.validateUpdateRoles(roles)

        SoftAssertions.assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(Severity.ERROR, "ROLE_NOT_EXIST", "Role doesn't exist.")))
        }

        roles.roles!!.forEach { verify(roleRepository).findByName(it!!) }
        verifyNoMoreInteractions(roleRepository)
    }

}
