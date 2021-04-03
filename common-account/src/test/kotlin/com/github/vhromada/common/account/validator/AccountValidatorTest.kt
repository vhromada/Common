package com.github.vhromada.common.account.validator

import com.github.vhromada.common.account.repository.RoleRepository
import com.github.vhromada.common.account.service.AccountService
import com.github.vhromada.common.account.utils.AccountUtils
import com.github.vhromada.common.account.utils.RoleUtils
import com.github.vhromada.common.account.validator.impl.AccountValidatorImpl
import com.github.vhromada.common.entity.Account
import com.github.vhromada.common.result.Event
import com.github.vhromada.common.result.Severity
import com.github.vhromada.common.result.Status
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
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
 * A class represents test for class [AccountValidator].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class AccountValidatorTest {

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
     * Instance of [AccountValidator]
     */
    private lateinit var accountValidator: AccountValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        accountValidator = AccountValidatorImpl(accountService = accountService, roleRepository = roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateNew] with correct account.
     */
    @Test
    fun validateNew() {
        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.empty())
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = null, uuid = null)

        val result = accountValidator.validateNew(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).findByUsername(username = account.username!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateNew] with null account.
     */
    @Test
    fun validateNewNullAccount() {
        val result = accountValidator.validateNew(account = null)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_NULL", message = "Account mustn't be null.")))
        }

        verifyZeroInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateNew] with account with not null ID.
     */
    @Test
    fun validateNewNotNullId() {
        whenever(accountService.get(id = any())).thenReturn(Optional.empty())
        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.empty())
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = Int.MAX_VALUE, uuid = null)

        val result = accountValidator.validateNew(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_ID_NOT_NULL", message = "ID must be null.")))
        }

        verify(accountService).get(id = account.id!!)
        verify(accountService).findByUsername(username = account.username!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateNew] with account with not null UUID.
     */
    @Test
    fun validateNewNotNullUuid() {
        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.empty())
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = null, uuid = "uuid")

        val result = accountValidator.validateNew(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_UUID_NOT_NULL", message = "UUID must be null.")))
        }

        verify(accountService).findByUsername(username = account.username!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateNew] with account with null username.
     */
    @Test
    fun validateNewNullUsername() {
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = null, uuid = null)
            .copy(username = null)

        val result = accountValidator.validateNew(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_NULL", message = "Username mustn't be null.")))
        }

        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(roleRepository)
        verifyZeroInteractions(accountService)
    }

    /**
     * Test method for [AccountValidator.validateNew] with account with null password.
     */
    @Test
    fun validateNewNullPassword() {
        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.empty())
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = null, uuid = null)
            .copy(password = null)

        val result = accountValidator.validateNew(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_PASSWORD_NULL", message = "Password mustn't be null.")))
        }

        verify(accountService).findByUsername(username = account.username!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateNew] with account with not existing role.
     */
    @Test
    fun validateNewRoleNotExisting() {
        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.empty())
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.empty())

        val account = getValidatingData(id = null, uuid = null)

        val result = accountValidator.validateNew(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ROLE_NOT_EXIST", message = "Role doesn't exist.")))
        }

        verify(accountService).findByUsername(username = account.username!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateNew] with account with existing username.
     */
    @Test
    fun validateNewExistingUsername() {
        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.of(AccountUtils.newAccountDomain(id = null)))
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = null, uuid = null)

        val result = accountValidator.validateNew(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_ALREADY_EXIST", message = "Username already exists.")))
        }

        verify(accountService).findByUsername(username = account.username!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateUpdate] with correct account.
     */
    @Test
    fun validateUpdate() {
        whenever(accountService.get(id = any())).thenReturn(Optional.of(AccountUtils.newAccountDomain(id = 1)))
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = 1)

        val result = accountValidator.validateUpdate(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService, atLeastOnce()).get(id = account.id!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateUpdate] with null account.
     */
    @Test
    fun validateUpdateNullAccount() {
        val result = accountValidator.validateUpdate(account = null)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_NULL", message = "Account mustn't be null.")))
        }

        verifyZeroInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateUpdate] with account with null ID.
     */
    @Test
    fun validateUpdateNullId() {
        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.empty())
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = null)

        val result = accountValidator.validateUpdate(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_ID_NULL", message = "ID mustn't be null.")))
        }

        verify(accountService).findByUsername(username = account.username!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateUpdate] with account with null UUID.
     */
    @Test
    fun validateUpdateNullUuid() {
        whenever(accountService.get(id = any())).thenReturn(Optional.of(AccountUtils.newAccountDomain(id = 1)))
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = 1, uuid = null)

        val result = accountValidator.validateUpdate(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_UUID_NULL", message = "UUID mustn't be null.")))
        }

        verify(accountService, atLeastOnce()).get(id = account.id!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateUpdate] with account with null username.
     */
    @Test
    fun validateUpdateNullUsername() {
        whenever(accountService.get(id = any())).thenReturn(Optional.of(AccountUtils.newAccountDomain(id = 1)))
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = 1)
            .copy(username = null)

        val result = accountValidator.validateUpdate(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_NULL", message = "Username mustn't be null.")))
        }

        verify(accountService, atLeastOnce()).get(id = account.id!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateUpdate] with account with null password.
     */
    @Test
    fun validateUpdateNullPassword() {
        whenever(accountService.get(id = any())).thenReturn(Optional.of(AccountUtils.newAccountDomain(id = 1)))
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = 1)
            .copy(password = null)

        val result = accountValidator.validateUpdate(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_PASSWORD_NULL", message = "Password mustn't be null.")))
        }

        verify(accountService, atLeastOnce()).get(id = account.id!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateUpdate] with not existing account.
     */
    @Test
    fun validateUpdateAccountNotExisting() {
        whenever(accountService.get(id = any())).thenReturn(Optional.empty())
        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.empty())
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = Int.MAX_VALUE)

        val result = accountValidator.validateUpdate(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_NOT_EXIST", message = "Account doesn't exist.")))
        }

        verify(accountService, atLeastOnce()).get(id = account.id!!)
        verify(accountService).findByUsername(username = account.username!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateUpdate] with account with not existing role.
     */
    @Test
    fun validateUpdateRoleNotExisting() {
        whenever(accountService.get(id = any())).thenReturn(Optional.of(AccountUtils.newAccountDomain(id = 1)))
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.empty())

        val account = getValidatingData(id = 1)

        val result = accountValidator.validateUpdate(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ROLE_NOT_EXIST", message = "Role doesn't exist.")))
        }

        verify(accountService, atLeastOnce()).get(id = account.id!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateUpdate] with account with existing username.
     */
    @Test
    fun validateUpdateExistingUsername() {
        whenever(accountService.get(id = any())).thenReturn(Optional.of(AccountUtils.newAccountDomain(id = 1)))
        whenever(accountService.findByUsername(username = any())).thenReturn(Optional.of(AccountUtils.newAccountDomain(id = 1)))
        whenever(roleRepository.findByName(name = any())).thenReturn(Optional.of(RoleUtils.getRole(index = 1)))

        val account = getValidatingData(id = 1)
            .copy(username = "test")

        val result = accountValidator.validateUpdate(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_USERNAME_ALREADY_EXIST", message = "Username already exists.")))
        }

        verify(accountService, atLeastOnce()).get(id = account.id!!)
        verify(accountService).findByUsername(username = account.username!!)
        account.roles!!.forEach { verify(roleRepository).findByName(name = it) }
        verifyNoMoreInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateExist] with correct account.
     */
    @Test
    fun validateExist() {
        whenever(accountService.get(id = any())).thenReturn(Optional.of(AccountUtils.newAccountDomain(id = 1)))

        val account = getValidatingData(id = 1)

        val result = accountValidator.validateExist(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.OK)
            it.assertThat(result.events()).isEmpty()
        }

        verify(accountService).get(id = account.id!!)
        verifyNoMoreInteractions(accountService)
        verifyZeroInteractions(roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateExist] with null account.
     */
    @Test
    fun validateExistNullAccount() {
        val result = accountValidator.validateExist(account = null)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_NULL", message = "Account mustn't be null.")))
        }

        verifyZeroInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateExist] with account with null ID.
     */
    @Test
    fun validateExistNullId() {
        val result = accountValidator.validateExist(account = getValidatingData(id = null))

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_ID_NULL", message = "ID mustn't be null.")))
        }

        verifyZeroInteractions(accountService, roleRepository)
    }

    /**
     * Test method for [AccountValidator.validateExist] with not existing account.
     */
    @Test
    fun validateExistAccountNotExisting() {
        whenever(accountService.get(id = any())).thenReturn(Optional.empty())

        val account = getValidatingData(id = Int.MAX_VALUE)

        val result = accountValidator.validateExist(account = account)

        assertSoftly {
            it.assertThat(result.status).isEqualTo(Status.ERROR)
            it.assertThat(result.events()).isEqualTo(listOf(Event(severity = Severity.ERROR, key = "ACCOUNT_NOT_EXIST", message = "Account doesn't exist.")))
        }

        verify(accountService).get(id = account.id!!)
        verifyNoMoreInteractions(accountService)
        verifyZeroInteractions(roleRepository)
    }

    /**
     * Returns instance of [Account].
     *
     * @param id ID
     * @return instance of [Account]
     */
    private fun getValidatingData(id: Int?): Account {
        return AccountUtils.newAccount(id = id)
    }

    /**
     * Returns instance of [Account].
     *
     * @param id   ID
     * @param uuid UUID
     * @return instance of [Account]
     */
    private fun getValidatingData(id: Int?, uuid: String?): Account {
        return getValidatingData(id = id)
            .copy(uuid = uuid)
    }

}
